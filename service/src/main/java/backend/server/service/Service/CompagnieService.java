package backend.server.service.Service;

import backend.server.service.Repository.CompagnieRepository;
import backend.server.service.Repository.GroupeRepository;
import backend.server.service.Repository.MembreRepository;
import backend.server.service.domain.Compagnie;
import backend.server.service.domain.Groupe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor @Service @Slf4j @Transactional
public class CompagnieService {
    private final CompagnieRepository compagnieRepository;

    private final GroupeRepository groupeRepository;

    private final GroupeService groupeService;

    private final MembreRepository membreRepository;

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

    public Groupe createGroupe(String nom, double quota){
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("compagnie name: "+ compagnieName+" nom de groupe: "+ nom);
        //check if the compagnie has a groupe with the same name
        if(groupeService.getGroupe(nom, compagnieName) != null){
            throw new RuntimeException("Groupe already exists");
        }
        Groupe groupe = Groupe.builder().nom(nom).quota(quota).build();
        //get the id of the current authenticated user via the security context holder
        Compagnie compagnie = compagnieRepository.findByNom(compagnieName);
        if(groupeRepository.sumQuotasByCompagnieNom(compagnieName) + quota > compagnie.getQuota()){
            throw new RuntimeException("Quota allocation exceeded");
        }
        groupe.setCompagnie(compagnie);
        compagnie.getGroupes().add(groupe);
        compagnieRepository.save(compagnie);
        return groupeService.getGroupe(nom, compagnieName);
    }
    public Groupe createGroupe(String nom, double quota, Long CompagnieId){
        //check if the compagnie has a groupe with the same name
        if(groupeService.getGroupe(nom, SecurityContextHolder.getContext().getAuthentication().getName()) != null){
            throw new RuntimeException("Groupe already exists");
        }
        Groupe groupe = Groupe.builder().nom(nom).quota(quota).build();

        Compagnie compagnie = compagnieRepository.findById(CompagnieId).orElseThrow(()-> new RuntimeException("Compagnie not found") );
        if(groupeRepository.sumQuotasByCompagnieNom(compagnie.getNom()) + quota > compagnie.getQuota()){
            throw new RuntimeException("Quota allocation exceeded");
        }
        groupe.setCompagnie(compagnie);
        compagnie.getGroupes().add(groupe);
        compagnieRepository.save(compagnie);
        return groupeService.getGroupe(nom, compagnie.getNom());
    }

    public void deleteGroupe(String nom){
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        Groupe groupe = groupeRepository.findByNomAndCompagnieNom(nom, compagnieName);
        if(groupe == null){
            throw new RuntimeException("Groupe not found");
        }
        if(!(compagnieName.equals(groupe.getCompagnie().getNom()))){
            throw new RuntimeException("Unauthorized");
        }
        if(groupe.getNom().toLowerCase().equals(compagnieName.toLowerCase())){
            throw new RuntimeException("Cannot delete default groupe");
        }
        log.info("groupe name: "+groupe.getNom()+" compagnie name: "+groupe.getCompagnie().getNom());
        groupeRepository.delete(groupe);
    }

    public Groupe updateGroupe(Long groupeId, String newName) {

        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();

        Groupe grp = groupeRepository.findByIdAndCompagnieNom(groupeId, compagnieName);
        grp.setNom(newName);
        // Save the updated Professor and return it
        return groupeRepository.save(grp);
    }

    public List<String> getAllUniqueGroups() {
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        return groupeRepository.findAllUniqueGroupes(compagnieName);
    }

}
