package backend.server.service.Service;

import backend.server.service.Repository.CompagnieRepository;
import backend.server.service.domain.Compagnie;
import backend.server.service.domain.Groupe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor @Service @Slf4j @Transactional
public class CompagnieService {
    private final CompagnieRepository compagnieRepository;

    private final GroupeService groupeService;

    public Compagnie getCompagnie(Long id){
        return compagnieRepository.findById(id).orElseThrow(()-> new RuntimeException("Compagnie not found") );
    }

    public Compagnie getCompagnie(String nom){
        return compagnieRepository.findByNom(nom);
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

    public Compagnie createGroupe(String nom, double quota){
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();

        //check if the compagnie has a groupe with the same name
        if(groupeService.getGroupe(nom, compagnieName) != null){
            throw new RuntimeException("Groupe already exists");
        }

        Groupe groupe = Groupe.builder().nom(nom).quota(quota).build();
        //get the id of the current authenticated user via the security context holder
        Compagnie compagnie = compagnieRepository.findByNom(compagnieName);
        groupe.setCompagnie(compagnie);
        compagnie.getGroupes().add(groupe);
        return compagnieRepository.save(compagnie);
    }

    public Compagnie createGroupe(String nom, double quota, Long CompagnieId){
        //check if the compagnie has a groupe with the same name
        if(groupeService.getGroupe(nom, SecurityContextHolder.getContext().getAuthentication().getName()) != null){
            throw new RuntimeException("Groupe already exists");
        }
        Groupe groupe = Groupe.builder().nom(nom).quota(quota).build();

        Compagnie compagnie = compagnieRepository.findById(CompagnieId).orElseThrow(()-> new RuntimeException("Compagnie not found") );

        groupe.setCompagnie(compagnie);
        compagnie.getGroupes().add(groupe);
        return compagnieRepository.save(compagnie);
    }

}
