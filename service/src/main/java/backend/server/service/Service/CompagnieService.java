package backend.server.service.Service;

import backend.server.service.Repository.*;
import backend.server.service.domain.*;
import backend.server.service.payloads.EntitiesCountResponse;
import backend.server.service.security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

 @Service @Slf4j @Transactional @RequiredArgsConstructor
public class CompagnieService implements ICompagnieService{
     private final GroupeRepository groupeRepository;
     private final MembreRepository membreRepository;
     private final UserRepository userRepository;
     private final LabelRepository labelRepository;
     private final CategorieRepository categorieRepository;
     private final CompagnieRepository compagnieRepository;
     private final DossierRepository dossierRepository;
     private final FichierRepository fichierRepository;


    private final GroupeService groupeService;
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

     /**
      * Crée un nouveau groupe, l'ajoute à la compagnie et le persiste si le quota de la compagnie n'est pas dépassé, un dossier est créé pour le groupe
      * @param nom nom du groupe
      * @param quota quota du groupe (espace disque)
      * @return le groupe créé
      */
    @Override
    public Groupe createGroupe(String nom, double quota){
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        //System.out.println("compagnie name: "+ compagnieName+" nom de groupe: "+ nom);
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

     /**
      * Crée un nouveau groupe, l'ajoute à la compagnie et le persiste si le quota de la compagnie n'est pas dépassé, un dossier est créé pour le groupe
      * @param nom nom du groupe
      * @param quota quota du groupe (espace disque)
      * @param CompagnieId id de la compagnie
      * @return le groupe créé
      */
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

     /**
      * Supprime un groupe de la compagnie si le groupe existe et si l'utilisateur est autorisé à le faire
      * @param nom nom du groupe
      */
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
        if(groupe.getNom().equalsIgnoreCase(compagnieName)){
            throw new RuntimeException("Impossible de supprimer le groupe par défaut");
        }
        log.info("groupe name: "+groupe.getNom()+" compagnie name: "+groupe.getCompagnie().getNom());
        groupeRepository.delete(groupe);
    }
    /**
     * Supprime un groupe de la compagnie si le groupe existe et si l'utilisateur est autorisé à le faire
     * @param groupeId id du groupe
     */
    @Override
    public Groupe updateGroupe(Long groupeId, String newName) {

        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();

        Groupe grp = groupeRepository.findByIdAndCompagnieNom(groupeId, compagnieName);
        if(grp.getNom().equalsIgnoreCase(compagnieName)){
            throw new RuntimeException("Impossible de modifier le groupe par défaut");
        }
        grp.setNom(newName);
        // Save the updated Professor and return it
        return groupeRepository.save(grp);
    }

     /**
      * Supprime un Memebre de la compagnie si le membre existe et si l'utilisateur est autorisé à le faire
      * @param membreId id du membre
      * @param username nom d'utilisateur du membre
      */
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
        userRepository.deleteByUsername(username);
    }

     /**
      * Mis à jour un membre passé en paramètre
      * @param membre membre à mettre à jour
      * @return le membre mis à jour
      */
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
            return membreRepository.save(existingMembre);
        } else {
            throw new RuntimeException("membre introuvable");
        }
    }

     /**
      * Récupère tous les groupes de la compagnie
      * @return la liste des groupes de la compagnie
      */
    @Override
    public List<String> getAllUniqueGroups() {
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        return groupeRepository.findAllUniqueGroupes(compagnieName);
    }

     /**
      * Récupére le nombre des membres, groupes, fichiers et dossiers de la compagnie
      * @return Un objet de type EntitiesCountResponse contenant le nombre des membres, groupes, fichiers et dossiers
      */
     @Override
     public EntitiesCountResponse getEntitiesCount() {
         EntitiesCountResponse entitiesCountResponse = new EntitiesCountResponse();
         String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
         entitiesCountResponse.setDossiers(dossierRepository.countByCompagnieNom(compagnieName));
         entitiesCountResponse.setFichiers(fichierRepository.countByCompagnieNom(compagnieName));
         entitiesCountResponse.setGroupes(groupeRepository.countByCompagnieNom(compagnieName));
         entitiesCountResponse.setMembres(membreRepository.countByCompagnieNom(compagnieName));
         return entitiesCountResponse;
     }

     /**
      * Récupére toutes les étiquettes de la compagnie et les retourne sous forme d'une liste de chaînes de caractères
      * @return la liste des étiquettes de la compagnie
      */
     @Override
     public List<String> getAllLabels() {
         List<Label> l = labelRepository.findAll();
         List<String> listLabels = new ArrayList<>();
         for (Label n: l)
         {
             listLabels.add(n.getNom());
         }
         return (listLabels);
     }

     /**
      * Récupére tous les groupes de la compagnie et les retourne sous forme d'une liste de chaînes de caractères
      * @return la liste des groupes de la compagnie
      */
     @Override
     public List<String> getAllCategories() {
         List<Categorie> l = categorieRepository.findAll();
         List<String> listCategories = new ArrayList<>();
         for (Categorie n: l)
         {
             listCategories.add(n.getNom());
         }
         return (listCategories);
     }



}
