package com.antifraud_System.reposiroty;


import com.antifraud_System.entity.StolenCard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StolenCardRepository extends CrudRepository<StolenCard, Long> {

    Optional<StolenCard> findByNumber(String number);

    List<StolenCard> findAll();

}
