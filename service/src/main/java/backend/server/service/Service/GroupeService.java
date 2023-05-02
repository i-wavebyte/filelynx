package backend.server.service.Service;

import backend.server.service.domain.Groupe;
import backend.server.service.Repository.GroupeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service @Transactional @Slf4j @RequiredArgsConstructor
public class GroupeService {

    private final GroupeRepository groupeRepository;

    Groupe addGroupe(Groupe groupe) {
        return groupeRepository.save(groupe);
    }

    public Groupe getGroupe(String nom, String compagnieNom) {
        return groupeRepository.findByNomAndCompagnieNom(nom, compagnieNom);
    }

    public Groupe getGroupe(Long id) {
        return groupeRepository.findById(id).orElse(null);
    }


    void deleteGroupe(Long id) {
        groupeRepository.deleteById(id);
    }

    Groupe updateGroupe(Groupe groupe) {
        return groupeRepository.save(groupe);
    }

}
