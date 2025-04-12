package com.udea.bancoudea.mapper;

import com.udea.bancoudea.DTO.TransactionDTO;
import com.udea.bancoudea.entity.Transaction;
import org.mapstruct.factory.Mappers;

public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);
    TransactionDTO toDTO(Transaction transaction);
}
