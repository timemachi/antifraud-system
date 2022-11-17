package com.antifraud_System.reposiroty;


import com.antifraud_System.entity.SuspiciousIP;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IpRepository extends CrudRepository<SuspiciousIP, Long> {

    Optional<SuspiciousIP> findByIp(String ip);

    List<SuspiciousIP> findAll();
}
