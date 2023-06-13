package backend.server.service.Service;

import backend.server.service.POJO.PageResponse;
import backend.server.service.Repository.LogRepository;
import backend.server.service.domain.Groupe;
import backend.server.service.domain.Log;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.TypeCache;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service @Slf4j @Transactional @RequiredArgsConstructor
public class LogService {
    private final LogRepository logRepository;
    public PageResponse<Log> getLogsPage(int page, int size, String sortBy) {
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        Sort sort = Sort.by(Sort.Order.desc(sortBy));
        int start = page * size;
        int end = Math.min(start+size, (int) logRepository.count());
        List<Log> logs = logRepository.findAllByCompagnieNom(compagnieName, sort);
        List<Log> pageContent = logs.subList(start, Math.min(end, logs.size()));
        return new PageResponse<>(pageContent, logs.size());
    }
}
