package org.example.expert.domain.log.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.reposiotory.LogRepository;
import org.example.expert.domain.manager.entity.Manager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogRepository logRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logManagerRegistration(Manager manager) {
        Log log = new Log(manager.getId(), "create");
        logRepository.save(log);
    }
}
