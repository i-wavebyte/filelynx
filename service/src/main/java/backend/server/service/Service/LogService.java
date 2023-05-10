package backend.server.service.Service;

import backend.server.service.Repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service @Slf4j @Transactional @RequiredArgsConstructor
public class LogService {
    private final LogRepository logRepository;
}
