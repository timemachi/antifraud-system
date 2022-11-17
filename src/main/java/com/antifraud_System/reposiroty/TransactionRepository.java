package com.antifraud_System.reposiroty;


import com.antifraud_System.entity.TransactionInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<TransactionInfo, Long> {

    List<TransactionInfo> findByNumberAndDateBetween(
            String number, LocalDateTime dateStart, LocalDateTime dateEnd);

    List<TransactionInfo> findByNumberOrderByTransactionId(String number);

    List<TransactionInfo> findAll();
}
