package com.udea.bancoudea.service;

import com.udea.bancoudea.DTO.TransactionDTO;
import com.udea.bancoudea.entity.Customer;
import com.udea.bancoudea.entity.Transaction;
import com.udea.bancoudea.repository.CustomerRepository;
import com.udea.bancoudea.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public TransactionDTO transferMoney(TransactionDTO transactionDTO) {
        // validar cuenta != nil
        if(transactionDTO.getSenderAccountNumber()==null || transactionDTO.getReceiverAccountNumber()==null) {
            throw new IllegalArgumentException("Sender and Receiver account name cannot be null");
        }

        // buscar clientes por # cuenta
        Customer sender = customerRepository.findByAccountNumber(transactionDTO.getSenderAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Sender Account not found"));

        Customer receiver = customerRepository.findByAccountNumber(transactionDTO.getReceiverAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Receiver Account not found"));

        // validar saldo de remitente
        if(sender.getBalance()<transactionDTO.getAmount()) {
            throw new IllegalArgumentException("Sender balance not enough");
        }

        // realiza transferencia
        sender.setBalance(sender.getBalance()-transactionDTO.getAmount());
        receiver.setBalance(receiver.getBalance()+transactionDTO.getAmount());

        // guardar cambios
        customerRepository.save(sender);
        customerRepository.save(receiver);

        //crea y guarda transaccion
        Transaction transaction = new Transaction();
        transaction.setSenderAccountNumber(sender.getAccountNumber());
        transaction.setReceiverAccountNumber(receiver.getAccountNumber());
        transaction.setAmount(transactionDTO.getAmount());
        transaction = transactionRepository.save(transaction);

        // retornar transaccion como DTO
        TransactionDTO savedTransaction = new TransactionDTO();
        savedTransaction.setId(transaction.getId());
        savedTransaction.setSenderAccountNumber(transaction.getSenderAccountNumber());
        savedTransaction.setReceiverAccountNumber(transaction.getReceiverAccountNumber());
        savedTransaction.setAmount(transaction.getAmount());
        return savedTransaction;
    }

    public List<TransactionDTO> getTransactionForAccount(String accountNumber) {
        List<Transaction> transactions = transactionRepository
                .findBySenderAccountNumberOrReceiverAccountNumber(accountNumber, accountNumber);

        return transactions.stream().map(transaction -> {
            TransactionDTO dto = new TransactionDTO();
            dto.setSenderAccountNumber(transaction.getSenderAccountNumber());
            dto.setReceiverAccountNumber(transaction.getReceiverAccountNumber());
            dto.setAmount(transaction.getAmount());
            return dto;
        }).collect(Collectors.toList());
    }
}
