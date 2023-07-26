package backend.server.service.controller;

import backend.server.service.POJO.PageResponse;
import backend.server.service.POJO.Quota;
import backend.server.service.Repository.*;
import backend.server.service.Service.*;
import backend.server.service.domain.*;
import backend.server.service.enums.LogType;
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
        if (userRepository.existsByUsername(membre.getUsername())) {
            System.out.println("existsByUsername");
            String messageErreur = "Erreur : Le nom d'utilisateur '" + membre.getUsername() + "' est déjà utilisé !";
            return ResponseEntity.badRequest().body(new MessageResponse(messageErreur));
        }
        // Vérifier si l'adresse email est disponible
        if (userRepository.existsByEmail(membre.getEmail())) {
            System.out.println("existsByEmail");
            String messageErreur = "Erreur : L'adresse email '" + membre.getEmail() + "' est déjà utilisée !";
            return ResponseEntity.badRequest().body(new MessageResponse(messageErreur));
        }
        System.out.println(membre);

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
        Membre m = membreService.addMembre(newMembre);
        // Ajouter un message de log pour l'ajout du nouveau membre
        Log logMessage = Log.builder().message("Membre " + membre.getUsername() + " créé et ajouté au groupe " + membre.getGroupe()).type(LogType.CRÉER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);

        // Retourner une réponse HTTP avec un message de réussite
        return ResponseEntity.ok(new MessageResponse("Utilisateur enregistré avec succès !"));
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/createGroup/{group}")
    public ResponseEntity<?> createGroup(@PathVariable String group) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        try
        {
            compagnieService.createGroupe(group, 1024.*1024.*1024.*5,compagnie.getId());
            Log logMessage = Log.builder().message("Groupe " + group + " créé").type(LogType.CRÉER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
            logRepository.save(logMessage);
            Dossier dossier = new Dossier();
            dossier.setNom(group);
            dossier.setCompagnie(compagnie);
            dossier.setGroupRoot(true);
            Authorisation authorisation = Authorisation.generateFullAccess();
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
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PutMapping("/ChangeMemberGroup/{username}/{group}")
    public ResponseEntity<?> changeMemberGroup(@PathVariable String username, @PathVariable String group) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Membre membre = membreService.getMembre(username);
        membre.setGroupe(groupeService.getGroupe(group,compagnieNom));
        membreService.updateMembre(membre);
        Log logMessage = Log.builder().message("Membre " + membre.getUsername() + " changed group from " + membre.getGroupe() + " to" + group).type(LogType.MODIFIER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);
        return ResponseEntity.ok(new MessageResponse("Le groupe d'utilisateurs a bien été remplacé par " + group));
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getQuotaStatus")
    public ResponseEntity<?> getQuotaStatus() {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Quota quota1 = new Quota();
        quota1.setQuota(compagnie.getQuota());
        quota1.setUsedQuota(compagnie.getUsedQuota());
        quota1.setQuotaLeft(compagnie.getQuota() - compagnie.getUsedQuota());
        return ResponseEntity.ok(quota1);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getCompagnieLogs")
    public ResponseEntity<?> getCompagnieLogs() {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        return ResponseEntity.ok(compagnie.getLogs());
    }

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
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @DeleteMapping("/deleteGroupe/{group}")
    public ResponseEntity<?> deleteGroup(@PathVariable String group) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        try {
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
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PutMapping("/updateGroupe/{groupeId}/{newName}")
    public ResponseEntity<?> updateGroup(@PathVariable Long groupeId, @PathVariable String newName) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Groupe grp = groupeRepository.findByIdAndCompagnieNom(groupeId, compagnieNom);
        String nom = grp.getNom();
        try{
            compagnieService.updateGroupe(groupeId, newName);
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
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/distinctGroups")
    public List<String> getAllUniqueGroupes() {
        return compagnieService.getAllUniqueGroups();
    }
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
}