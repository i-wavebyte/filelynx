package backend.server.service.Service;

import backend.server.service.Repository.CompagnieRepository;
import backend.server.service.Repository.GroupeRepository;
import backend.server.service.Repository.MembreRepository;
import backend.server.service.domain.Compagnie;
import backend.server.service.domain.Groupe;
import backend.server.service.domain.Membre;
import backend.server.service.security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

 @Service @Slf4j @Transactional
public class CompagnieService implements ICompagnieService{
     private final GroupeRepository groupeRepository;
     private final MembreRepository membreRepository;

     private  UserRepository userRepository;
     private final CompagnieRepository compagnieRepository;

     private final GroupeService groupeService;

     public CompagnieService(CompagnieRepository compagnieRepository, GroupeRepository groupeRepository, MembreRepository membreRepository, GroupeService groupeService){
          this.compagnieRepository = compagnieRepository;
          this.groupeRepository = groupeRepository;
          this.membreRepository = membreRepository;
          this.groupeService = groupeService;
     }
     @Override
    public Compagnie getCompagnie(Long id){
        return compagnieRepository.findById(id).orElseThrow(()-> new RuntimeException("Compagnie not found") );
    }
    @Override
    public Compagnie getCompagnie(String nom){
        return compagnieRepository.findByNom(nom);
    }
    @Override
    public List<Compagnie> getAllCompagnies(){
        return compagnieRepository.findAll();
    }
    @Override
    public Compagnie createCompagnie(Compagnie compagnie){
        return compagnieRepository.save(compagnie);
    }
    @Override
    public Compagnie updateCompagnie(Compagnie compagnie){
        return compagnieRepository.save(compagnie);
    }
    @Override
    public void deleteCompagnie(Long id){
        compagnieRepository.deleteById(id);
    }
    @Override
    public void deleteCompagnie(String nom){
        compagnieRepository.deleteByNom(nom);
    }
    @Override
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
    @Override
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
    @Override
    public void deleteGroupe(String nom){
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        Groupe groupe = groupeRepository.findByNomAndCompagnieNom(nom, compagnieName);
        if(groupe == null){
            throw new RuntimeException("Groupe non trouvé");
        }
        if(!(compagnieName.equals(groupe.getCompagnie().getNom()))){
            throw new RuntimeException("Non autorisé");
        }
        if(groupe.getNom().toLowerCase().equals(compagnieName.toLowerCase())){
            throw new RuntimeException("Impossible de supprimer le groupe par défaut");
        }
        log.info("groupe name: "+groupe.getNom()+" compagnie name: "+groupe.getCompagnie().getNom());
        groupeRepository.delete(groupe);
    }
    @Override
    public Groupe updateGroupe(Long groupeId, String newName) {

        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();

        Groupe grp = groupeRepository.findByIdAndCompagnieNom(groupeId, compagnieName);
        if(grp.getNom().toLowerCase().equals(compagnieName.toLowerCase())){
            throw new RuntimeException("Impossible de modifier le groupe par défaut");
        }
        grp.setNom(newName);
        // Save the updated Professor and return it
        return groupeRepository.save(grp);
    }
    @Override
    public void deleteMembre(Long membreId, String username){
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        Membre membre = membreRepository.findByUsername(username);
        if(membre == null){
            throw new RuntimeException("Membre introuvable");
        }
        if(!(compagnieName.equals(membre.getCompagnie().getNom()))){
            throw new RuntimeException("Non autorisé");
        }
        log.info("membre name: "+membre.getNom()+" compagnie name: "+membre.getCompagnie().getNom());
        membreRepository.deleteById(membreId);
//        userRepository.deleteByUsername(username);
    }
    @Override
    public Membre updateMembre(Membre membre) {
        Optional<Membre> optionalExistingMembre = membreRepository.findById(membre.getId());

        if (optionalExistingMembre.isPresent()) {
            Membre existingMembre = optionalExistingMembre.get();

            // Update the desired fields
            existingMembre.setNom(membre.getNom());
            existingMembre.setPrenom(membre.getPrenom());
            existingMembre.setEmail(membre.getEmail());
            existingMembre.setUsername(membre.getUsername());

            // Save the updated entity back to the database
            Membre updatedMembre = membreRepository.save(existingMembre);
            return updatedMembre;
        } else {
            throw new RuntimeException("membre introuvable");
        }
    }
    @Override
    public List<String> getAllUniqueGroups() {
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        return groupeRepository.findAllUniqueGroupes(compagnieName);
    }

}
