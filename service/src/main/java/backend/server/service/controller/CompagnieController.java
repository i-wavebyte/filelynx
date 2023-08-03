package backend.server.service.controller;

import backend.server.service.POJO.PageResponse;
import backend.server.service.POJO.Quota;
import backend.server.service.Repository.*;
import backend.server.service.Service.*;
import backend.server.service.domain.*;
import backend.server.service.enums.AuthLevel;
import backend.server.service.enums.LogType;
import backend.server.service.payloads.EntitiesCountResponse;
import backend.server.service.payloads.RegisterUserRequest;
import backend.server.service.security.POJOs.responses.MessageResponse;
import backend.server.service.security.entities.EROLE;
import backend.server.service.security.entities.User;
import backend.server.service.security.repositories.RoleRepository;
import backend.server.service.security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/compagnie")
@CrossOrigin(origins = "*", maxAge = 3600) // Allow requests from any origin for one hour
@RequiredArgsConstructor @Slf4j
public class CompagnieController {
    private final ICompagnieService compagnieService;
    private final CompagnieRepository compagnieRepository;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final IGroupeService groupeService;
    private final GroupeRepository groupeRepository;
    private final IMembreService membreService;
    private final LogRepository logRepository;
    private final CategorieRepository categorieRepository;
    private final LabelRepository labelRepository;
    private final ILogService logService;
    private final ICategorieService categorieService;
    private final ILabelService labelService;
    private final IDossierService dossierService;
    private final QuotaService quotaService;

    /**
     * Ajoute un nouveau membre à la compagnie actuelle.
     *
     * @param membre Les informations du membre à ajouter.
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/RegisterMembre")
    public ResponseEntity<?> addMembre(@RequestBody RegisterUserRequest membre) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieRepository.findByNom(compagnieNom);
        // Vérifier si la compagnie existe
        if(compagnie == null){
            String messageErreur = "Erreur : Compagnie introuvable !";
            return ResponseEntity.badRequest().body(new MessageResponse(messageErreur));
        }
//         Vérifier si le nom d'utilisateur est disponible
        if (Boolean.TRUE.equals(userRepository.existsByUsername(membre.getUsername()))) {
            String messageErreur = "Erreur : Le nom d'utilisateur '" + membre.getUsername() + "' est déjà utilisé !";
            return ResponseEntity.badRequest().body(new MessageResponse(messageErreur));
        }
        // Vérifier si l'adresse email est disponible
        if (Boolean.TRUE.equals(userRepository.existsByEmail(membre.getEmail()))) {
            String messageErreur = "Erreur : L'adresse email '" + membre.getEmail() + "' est déjà utilisée !";
            return ResponseEntity.badRequest().body(new MessageResponse(messageErreur));
        }

        // Créer un nouvel utilisateur avec le rôle ROLE_USER
        User user = new User(membre.getUsername(), membre.getEmail(), encoder.encode(membre.getPassword()));
        user.setRoles(Collections.singleton(roleRepository.findByName(EROLE.ROLE_USER).orElseThrow(() -> new RuntimeException("Erreur : Rôle introuvable."))));
        userRepository.save(user);

        // Ajouter le nouveau membre à la base de données
        Membre newMembre = Membre.builder()
                .nom(membre.getNom())
                .prenom(membre.getPrenom())
                .email(membre.getEmail())
                .username(membre.getUsername())
                .groupe(groupeService.getGroupe(membre.getGroupe(),compagnieNom))
                .compagnie(compagnie)
                .build();
        membreService.addMembre(newMembre);
        // Ajouter un message de log pour l'ajout du nouveau membre
        Log logMessage = Log.builder().message("Membre " + membre.getUsername() + " créé et ajouté au groupe " + membre.getGroupe()).type(LogType.CRÉER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);

        // Retourner une réponse HTTP avec un message de réussite
        return ResponseEntity.ok(new MessageResponse("Utilisateur enregistré avec succès !"));
    }

    /**
     * Crée un nouveau groupe dans la compagnie actuelle.
     * @param group Le nom du groupe à créer.
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/createGroup/{group}")
    public ResponseEntity<?> createGroup(@PathVariable String group) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        try
        {
            Groupe groupe =  compagnieService.createGroupe(group, 1024.*1024.*1024.*5,compagnie.getId());
            Log logMessage = Log.builder().message("Groupe " + group + " créé").type(LogType.CRÉER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
            logRepository.save(logMessage);
            Dossier dossier = new Dossier();
            dossier.setNom(group);
            dossier.setCompagnie(compagnie);
            dossier.setGroupRoot(true);
            dossier.setGroupe(groupe);
            Authorisation authorisation = Authorisation.generateFullAccess();
            authorisation.setAuthLevel(AuthLevel.GROUPE);
            authorisation.setRessourceAccessor(groupeService.getGroupe(group,compagnieNom));
            authorisation.setDossier(dossier);
            dossier.getAuthorisations().add(authorisation);


            dossierService.addDossier(dossier,dossierService.getRootDossier().getId());
            return ResponseEntity.ok(new MessageResponse("Groupe créé avec succès"));
        }
        catch(RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Transfére un membre d'un groupe à un autre.
     * @param username Le nom d'utilisateur du membre à transférer.
     * @param group Le nom du groupe dans lequel le membre doit être transféré.
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PutMapping("/ChangeMemberGroup/{username}/{group}")
    public ResponseEntity<?> changeMemberGroup(@PathVariable String username, @PathVariable String group) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Membre membre = membreService.getMembre(username);
        Groupe oldGroupe = membre.getGroupe();
        membre.setGroupe(groupeService.getGroupe(group,compagnieNom));
        membreService.updateMembre(membre);
        Log logMessage = Log.builder().message("Membre " + membre.getUsername() + " à changer de groupe de " + oldGroupe + " vers " + group).type(LogType.MODIFIER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);
        return ResponseEntity.ok(new MessageResponse("Le groupe d'utilisateurs a bien été remplacé par " + group));
    }

    /**
     * retourne l'etat d'utilisation du quota de la compagnie
     * @param
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getQuotaStatus")
    public ResponseEntity<?> getQuotaStatus() {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Quota quota1 = new Quota();
        quota1.setQuota(compagnie.getQuota());
        quota1.setUsedQuota(quotaService.getTotalQuotaOfCompagnie());
        quota1.setQuotaLeft(compagnie.getQuota() - quotaService.getTotalQuotaOfCompagnie());
        return ResponseEntity.ok(quota1);
    }

    /**
     * retourne les logs de la compagnie
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getCompagnieLogs")
    public ResponseEntity<?> getCompagnieLogs() {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        return ResponseEntity.ok(compagnie.getLogs());
    }

    /**
     * retourne les logs de la compagnie, paginés
     * @param page nombre de la page
     * @param size taille de la page
     * @param sortBy propriété utilisée pour le tri
     * @return la page de logs demandée
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getLogsPagination")
    public  PageResponse<Log> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size,
            @RequestParam(defaultValue = "date") String sortBy
    )
    {
        return logService.getLogsPage(page, size, sortBy);
    }

    /**
     * retourne les Membres (collaborateurs) de la compagnie, paginés et filtrés
     * @param page nombre de la page
     * @param size taille de la page
     * @param sortBy propriété utilisée pour le tri
     * @param sortOrder ordre de tri
     * @param searchQuery chaine de caractère utilisée pour la recherche
     * @param groupFilter filtre sur le groupe
     * @return la page de Membres demandée
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getUsers")
    public PageResponse<Membre> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nom") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortOrder,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String groupFilter
    ) {
        return membreService.getMembresPage(page, size, sortBy, sortOrder, searchQuery, groupFilter);
    }

    /**
     * retourne les groupes de la compagnie, paginés et filtrés
     * @param page nombre de la page
     * @param size taille de la page
     * @param sortBy propriété utilisée pour le tri
     * @param sortOrder ordre de tri
     * @param searchQuery chaine de caractère utilisée pour la recherche
     * @return la page de groupes demandée
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getGroups")
    public PageResponse<Groupe> getAllGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "nom") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortOrder,
            @RequestParam(required = false) String searchQuery
    ) {
        return groupeService.getGroupesPage(page, size, sortBy, sortOrder, searchQuery);
    }

    /**
     * supprime un groupe de la compagnie (sauf le groupe par défaut) et remet les membres dans le groupe par défaut, le dossier du groupe est supprimé
     * @param group nom du groupe à supprimer
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @DeleteMapping("/deleteGroupe/{group}")
    public ResponseEntity<?> deleteGroup(@PathVariable String group) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        try {
            Groupe groupe = groupeService.getGroupe(group,compagnieNom);
            Groupe defaultGroup = groupeService.getGroupe(compagnieNom,compagnieNom);
            if(groupe.getNom().equals(compagnieNom))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Vous ne pouvez pas supprimer le groupe par défaut"));
            for (Membre membre : groupe.getMembres()) {
                membre.setGroupe(defaultGroup);
                membreService.updateMembre(membre);
                Log logMessage = Log.builder().message("Membre " + membre.getUsername() + " Déplacé vers le groupe " + compagnieNom).type(LogType.DÉPLACER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
                logRepository.save(logMessage);
            }
            groupe.getMembres().clear();
            Dossier dossierGroupe = dossierService.getGroupRoot(groupe);
            Log.builder().message("Le dossier " + dossierGroupe.getFullPath() + " a était supprimé.").type(LogType.SUPPRIMER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
            dossierService.delete(dossierGroupe.getId());
            compagnieService.deleteGroupe(group);
            // Ajouter un message de log pour l'ajout du nouveau membre
            Log logMessage = Log.builder().message("Groupe " + group + " retiré de la Société " + compagnieNom).type(LogType.SUPPRIMER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
            logRepository.save(logMessage);
            return ResponseEntity.ok(new MessageResponse("Groupe supprimé avec succès"));
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * supprime un membre de la compagnie
     * @param membreId id du membre à supprimer
     * @param username nom d'utilisateur du membre à supprimer
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @DeleteMapping("/deleteMembre/{membreId}/{username}")
    public ResponseEntity<?> deleteMembre(@PathVariable Long membreId, @PathVariable String username) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        try {
            compagnieService.deleteMembre(membreId, username);
            // Ajouter un message de log pour l'ajout du nouveau membre
            Log logMessage = Log.builder().message("Membre " + username + " retiré de la Société " + compagnieNom).type(LogType.SUPPRIMER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
            logRepository.save(logMessage);
            return ResponseEntity.ok(new MessageResponse("Membre supprimé avec succès"));
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Modifie le nom d'un groupe, le dossier du groupe est renommé également avec le nouveau nom, les membres du groupe ne sont pas modifiés
     * @param groupeId id du groupe à modifier
     * @param newName nouveau nom du groupe
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PutMapping("/updateGroupe/{groupeId}/{newName}")
    public ResponseEntity<?> updateGroup(@PathVariable Long groupeId, @PathVariable String newName) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Groupe grp = groupeRepository.findByIdAndCompagnieNom(groupeId, compagnieNom);
        String nom = grp.getNom();
        Dossier dossier = dossierService.getGroupRoot(grp);
        try{
            compagnieService.updateGroupe(groupeId, newName);
            dossierService.renameDossier(dossier.getId(), newName);
            // Ajouter un message de log pour l'ajout du nouveau membre
            Log logMessage = Log.builder().message("Groupe " + nom + " de la Société " + compagnieNom + " a été mis à jour ").type(LogType.MODIFIER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
            logRepository.save(logMessage);
            return ResponseEntity.ok(new MessageResponse("Groupe mis à jour avec succès"));
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Modifie les informations d'un membre
     * @param membre membre à modifier
     * @return  Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PutMapping("/updateMembre")
    public ResponseEntity<?> updateMembre(@RequestBody Membre membre) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        String username= membre.getUsername();
        compagnieService.updateMembre(membre);
        Log logMessage = Log.builder().message("Membre " + username + " de la Société " + compagnieNom + " a été mis à jour").type(LogType.MODIFIER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);
        return ResponseEntity.ok(new MessageResponse("Membre mis à jour avec succès"));
    }

    /**
     * Modifie le nom d'une catégorie
     * @param categorieId id de la catégorie à modifier
     * @param newName nouveau nom de la catégorie
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PutMapping("/updateCategorie/{categorieId}/{newName}")
    public ResponseEntity<?> updateCategorie(@PathVariable Long categorieId, @PathVariable String newName ) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Categorie categorie = categorieRepository.findByIdAndCompagnieNom(categorieId, compagnieNom);
        String categorieName = categorie.getNom();
        categorieService.updateCategorie(categorieId, newName);
        Log logMessage = Log.builder().message("Catégorie " + categorieName + " de la Société " + compagnieNom + " a été mis à jour").type(LogType.MODIFIER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);
        return ResponseEntity.ok(new MessageResponse("Categorie mis à jour avec succès"));
    }

    /**
     * Supprime une catégorie
     * @param categorieId id de la catégorie à supprimer
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @DeleteMapping("/deleteCategorie/{categorieId}")
    public ResponseEntity<?> deleteCategorie(@PathVariable Long categorieId) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Categorie categorie = categorieRepository.findByIdAndCompagnieNom(categorieId, compagnieNom);
        String categorieName = categorie.getNom();
        categorieService.deleteCategorie(categorieId);
        Log logMessage = Log.builder().message("Catégorie " + categorieName + " retiré de la Société " + compagnieNom).type(LogType.SUPPRIMER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);
        return ResponseEntity.ok(new MessageResponse("Categorie supprimé avec succès"));
    }

    /**
     * Modifie le nom d'une étiquette
     * @param labelId id de l'étiquette à modifier
     * @param newName nouveau nom de l'étiquette
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PutMapping("/updateLabel/{labelId}/{newName}")
    public ResponseEntity<?> updateLabel(@PathVariable Long labelId, @PathVariable String newName ) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Label label = labelRepository.findByIdAndCompagnieNom(labelId, compagnieNom);
        String labelName = label.getNom();
        labelService.updateLabel(labelId, newName);
        Log logMessage = Log.builder().message("Étiquete " + labelName + " de la Société " + compagnieNom + " a été mis à jour").type(LogType.MODIFIER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);
        return ResponseEntity.ok(new MessageResponse("Étiquete mis à jour avec succès"));
    }

    /**
     * Supprime une étiquette
     * @param labelId id de l'étiquette à supprimer
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @DeleteMapping("/deleteLabel/{labelId}")
    public ResponseEntity<?> deleteLabel(@PathVariable Long labelId) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Label label = labelRepository.findByIdAndCompagnieNom(labelId, compagnieNom);
        String labelName = label.getNom();
        labelService.deleteLabel(labelId);
        Log logMessage = Log.builder().message("Étiquete " + labelName + " retiré de la Société " + compagnieNom).type(LogType.SUPPRIMER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);
        return ResponseEntity.ok(new MessageResponse("Étiquete supprimé avec succès"));
    }

    /**
     * @return une liste des groupes de la compagnie ou au moins un membre est affecté
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/distinctGroups")
    public List<String> getAllUniqueGroupes() {
        return compagnieService.getAllUniqueGroups();
    }

    /**
     * @return une liste des labels de la compagnie
     */

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/labels")
    public List<String> getAllLabels() {
         return compagnieService.getAllLabels();
    }

    /**
     * @return une liste des catégories de la compagnie
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/categories")
    public List<String> getAllCategories() {
        return compagnieService.getAllCategories();
    }


    /**
     * @param page numéro de la page
     * @param size nombre d'éléments par page
     * @param sortBy nom de la colonne à trier
     * @param sortOrder ordre de tri
     * @param searchQuery mot clé de recherche
     * @return une liste des catégories de la compagnie, paginé et triable, avec un mot clé de recherche
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getCategories")
    public PageResponse<Categorie> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "nom") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortOrder,
            @RequestParam(required = false) String searchQuery
    ) {
        return categorieService.getCategoriesPage(page, size, sortBy, sortOrder, searchQuery);
    }

    /**
     * @param page numéro de la page
     * @param size nombre d'éléments par page
     * @param sortBy nom de la colonne à trier
     * @param sortOrder ordre de tri
     * @param searchQuery mot clé de recherche
     * @return une liste des étiquettes de la compagnie, paginé et triable, avec un mot clé de recherche
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getLabels")
    public PageResponse<Label> getAllLabels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "nom") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortOrder,
            @RequestParam(required = false) String searchQuery
    ) {
        return labelService.getLabelsPage(page, size, sortBy, sortOrder, searchQuery);
    }

    /**
     * ajoute une étiquette
     * @param label étiquette à ajouter
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/addLabel")
    public ResponseEntity<?> addLabel(@RequestBody Label label) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        try {
            labelService.addLabel(label);
            // Ajouter un message de log pour l'ajout du nouveau membre
            Log logMessage = Log.builder().message("Label " + label.getNom() + " ajouté.").type(LogType.CRÉER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
            logRepository.save(logMessage);
            return ResponseEntity.ok(new MessageResponse("Label ajouté avec succès"));
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * ajoute une catégorie
     * @param categorie catégorie à ajouter
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/addCategorie")
    public ResponseEntity<?> addCategorie(@RequestBody Categorie categorie) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        try {
            categorieService.addCategorie(categorie);
            // Ajouter un message de log pour l'ajout du nouveau membre
            Log logMessage = Log.builder().message("Catégorie " + categorie.getNom() + " ajoutée.").type(LogType.CRÉER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
            logRepository.save(logMessage);
            return ResponseEntity.ok(new MessageResponse("Catégorie ajoutée avec succès"));
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * @param groupe nom du groupe
     * @return une liste des membres de la compagnie affectés au groupe
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getMembresByGroupe/{groupe}")
    public List<Membre> getMembresByGroupe(@PathVariable Long groupe) {
        return membreService.getMembresByGroupeId(groupe);
    }

    /**
     *
     * @return un objet contenant le nombre de membres, le nombre de groupes, le nombre de dossiers et le nombre de fichiers de la compagnie
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getEntitiesCount")
    public EntitiesCountResponse getEntitiesCount() {
        return compagnieService.getEntitiesCount();
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getQuotaUsedToday")
    public ResponseEntity<?> getQuotaUsedToday(){
        return ResponseEntity.ok(compagnieService.getQuotaUsedToday());
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getTotalAllocatedQuota")
    public ResponseEntity<?> getTotalAllocatedQuota(){
        return ResponseEntity.ok(quotaService.getTotalAllocatedQuota());
    }



}