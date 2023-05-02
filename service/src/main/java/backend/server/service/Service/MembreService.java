package backend.server.service.Service;

import backend.server.service.Repository.MembreRepository;
import backend.server.service.domain.Membre;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional @Slf4j
@AllArgsConstructor
public class MembreService {

    private final MembreRepository membreRepository;

    public Membre getMembre(Long id){
        return membreRepository.findById(id).orElseThrow(()-> new RuntimeException("Membre not found") );
    }

    public Membre addMembre(Membre membre){
        return membreRepository.save(membre);
    }

    public Membre updateMembre(Membre membre){
        return membreRepository.save(membre);
    }

    public void deleteMembre(Long id){
        membreRepository.deleteById(id);
    }

}
