package com.antifraud_System.service;


import com.antifraud_System.entity.SuspiciousIP;
import com.antifraud_System.exception.BadRequestException;
import com.antifraud_System.exception.IpExistException;
import com.antifraud_System.exception.IpNotFoundException;
import com.antifraud_System.reposiroty.IpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SuspiciousIpService {

    @Autowired
    IpRepository ipRepo;

    @Transactional
    public SuspiciousIP addIP(SuspiciousIP ip) {
        String ipAddress = ip.getIp();

        ipValidator(ipAddress);

        try {
            findIpByAddress(ip.getIp());
            throw new IpExistException();
        } catch (IpNotFoundException ignored) {}
        ipRepo.save(ip);
        return findIpByAddress(ip.getIp());
    }

    @Transactional
    public Map<String, String> deleteIp(String ip) {
        ipValidator(ip);

        ipRepo.delete(findIpByAddress(ip));

        return Map.of("status", "IP " + ip + " successfully removed!");

    }

    public List<SuspiciousIP> getAllIp() {
        return ipRepo.findAll();
    }



    public SuspiciousIP findIpByAddress(String address) {
        //ipValidator(address);
        Optional<SuspiciousIP> ip = ipRepo.findByIp(address);
        if (ip.isEmpty()) {
            throw new IpNotFoundException(address + "is not found");
        }
        return ip.get();
    }

    private void ipValidator(String ipAddress) {
        //https://stackoverflow.com/questions/3481828/how-do-i-split-a-string-in-java
        String[] address = ipAddress.split("\\.");
        if (address.length != 4) {
            throw new BadRequestException("ip address consists of four series of numbers but get " + address.length);
        }
        for (String add : address) {

                int number = Integer.parseInt(add);
                if (number > 255 || number < 0) {
                    throw new BadRequestException("ip address must from 0 to 255");
                }

        }
    }
}
