package backend.server.service.Service;

import backend.server.service.Repository.CompagnieRepository;
import backend.server.service.domain.Compagnie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor @Service @Slf4j @Transactional
public class CompagnieService {
    private final CompagnieRepository compagnieRepository;

    public Compagnie getCompagnie(Long id){
        return compagnieRepository.findById(id).orElseThrow(()-> new RuntimeException("Compagnie not found") );
    }

    public void getCompagnie(String nom){
        compagnieRepository.findByNom(nom);
    }

    public List<Compagnie> getAllCompagnies(){
        return compagnieRepository.findAll();
    }

    public Compagnie createCompagnie(Compagnie compagnie){
        return compagnieRepository.save(compagnie);
    }

    public Compagnie updateCompagnie(Compagnie compagnie){
        return compagnieRepository.save(compagnie);
    }

    public void deleteCompagnie(Long id){
        compagnieRepository.deleteById(id);
    }

    public void deleteCompagnie(String nom){
        compagnieRepository.deleteByNom(nom);
    }

}
