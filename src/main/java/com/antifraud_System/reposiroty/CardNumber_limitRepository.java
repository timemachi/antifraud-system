package com.antifraud_System.reposiroty;


import com.antifraud_System.entity.CardNumber_limit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardNumber_limitRepository extends CrudRepository<CardNumber_limit, Long> {

    public Optional<CardNumber_limit> findByNumber(String number);
}
