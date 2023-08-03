package backend.server.service.Service;

import backend.server.service.Repository.AuthorisationRepository;
import backend.server.service.Repository.CompagnieRepository;
import backend.server.service.Repository.MembreRepository;
import backend.server.service.Repository.RessourceAccessorRepository;
import backend.server.service.domain.*;
import backend.server.service.enums.AuthLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class AuthotisationService implements IAuthotisationService{

    private final RessourceAccessorRepository ressourceAccessorRepository;
    private final AuthorisationRepository authorisationRepository;
    private final CompagnieRepository compagnieRepository;
    private final MembreRepository membreRepository;

    /**
     * Extrait l'id du ressourceAccessor (compagnie ou membre) depuis le contexte de sécurité
     * @return l'id du ressourceAccessor
     */
    public Long extractResourceAssessorIdFromSecurityContext(){
        String resourceAccessorRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        String resourceAccessorName = SecurityContextHolder.getContext().getAuthentication().getName();
        //compagnie = true, membre = false
        boolean isCompagnieOrMembre = false;
        if(resourceAccessorRole.contains("ROLE_COMPAGNIE")){
            isCompagnieOrMembre = true;
        }
        if(resourceAccessorRole.contains("ROLE_MEMBRE")){
            isCompagnieOrMembre = false;
        }
        if(isCompagnieOrMembre){
            Compagnie compagnie = compagnieRepository.findByNom(resourceAccessorName);
            return compagnie.getId();

        }else{
            Membre membre = membreRepository.findByUsername(resourceAccessorName);
            return membre.getId();
        }
    }

    /**
     * retourne l'objet d'autorisation pour un ressourceAccessor et un dossier donné
     * @param ressourceAccessorId l'id du ressourceAccessor
     * @param dossierId l'id du dossier
     * @return l'objet d'autorisation
     */
    public Authorisation getAuthorisation(Long ressourceAccessorId, Long dossierId) {
        log.debug("Fetching authorisation for ressourceAccessorId {} and dossierId {}", ressourceAccessorId, dossierId);
        Optional<Authorisation> auth;
        RessourceAccessor ressourceAccessor = ressourceAccessorRepository.findById(ressourceAccessorId)
                .orElseThrow(() -> new RuntimeException("resourceAccessorId not found"));
        if (ressourceAccessor instanceof Membre) {
            auth = authorisationRepository.findByRessourceAccessorIdAndDossierId(ressourceAccessor.getId(), dossierId);
            if (auth.isPresent()) {
                log.debug("Authorisation found for ressourceAccessorId {} and dossierId {}", ressourceAccessorId, dossierId);
                return auth.get();
            } else {
                ressourceAccessor = ((Membre) ressourceAccessor).getGroupe();
            }
        }

        if (ressourceAccessor instanceof Groupe) {
            auth = authorisationRepository.findByRessourceAccessorIdAndDossierId(ressourceAccessor.getId(), dossierId);
            if (auth.isPresent()) {
                log.debug("Authorisation found for ressourceAccessorId {} and dossierId {}", ressourceAccessorId, dossierId);
                return auth.get();
            } else {
                ressourceAccessor = ((Groupe) ressourceAccessor).getCompagnie();
            }
        }

        if (ressourceAccessor instanceof Compagnie) {
            auth = authorisationRepository.findByRessourceAccessorIdAndDossierId(ressourceAccessor.getId(), dossierId);
            if (auth.isPresent()) {
                log.debug("Authorisation found for ressourceAccessorId {} and dossierId {}", ressourceAccessorId, dossierId);
                return auth.get();
            } else {
                throw new RuntimeException("Authorisation not found");
            }
        }
        throw new RuntimeException("Authorisation not found");
    }

    /**
     * Si le ressourceAccessor a un objet d'authorisation sur un dossier, verifie si un ressourceAccessor a une autorisation pour un dossier donné
     * @param resourceAccessorId l'id du ressourceAccessor
     * @param dossierId l'id du dossier
     * @param authType le type d'autorisation
     * @return true si le ressourceAccessor a une autorisation pour le dossier, false sinon
     */
    public boolean hasAuth(Long resourceAccessorId, Long dossierId, String authType) {
        log.debug("Checking if user with resourceAccessorId {} has authType {}", resourceAccessorId, authType);
        Authorisation auth = getAuthorisation(resourceAccessorId, dossierId);
        if (!auth.isLecture()) {
            log.debug("Access denied for user with resourceAccessorId {}", resourceAccessorId);
            throw new RuntimeException("Access denied");
        }
        switch (authType) {
            case "lecture":
                return true;
            case "ecriture":
                return auth.isEcriture();
            case "partage":
                return auth.isPartage();
            case "suppression":
                return auth.isSuppression();
            case "upload":
                return auth.isUpload();
            case "modification":
                return auth.isModification();
            case "telechargement":
                return auth.isTelechargement();
            case "creationDossier":
                return auth.isCreationDossier();
            default:
                log.debug("Invalid authType {} for user with resourceAccessorId {}", authType, resourceAccessorId);
                throw new RuntimeException("Invalid AuthType");
        }
    }

    /**
     * verify l'existance d'une autorisation pour un ressourceAccessor sur un dossier donné concernant une action donnée, si le ressourceAccessor n'a pas d'autorisation, lance une exception, sinon, la requéte continue normalement
     * @param resourceAccessorId l'id du ressourceAccessor
     * @param dossierId l'id du dossier
     * @param authType le type d'autorisation
     */
    public void authorize(Long resourceAccessorId, Long dossierId, String authType) {
        log.debug("Checking authorization for user with resourceAccessorId {} and authType {}", resourceAccessorId, authType);
        if (!hasAuth(resourceAccessorId, dossierId, authType)) {
            log.debug("Access denied for user with resourceAccessorId {} and authType {}", resourceAccessorId, authType);
            throw new RuntimeException("Access denied");
        }
    }

    /**
     * Génere et persiste des autorisations par défaut pour un ressourceAccessor, son groupe et sa compagnie sur un dossier donné
     * @param resourceAccessorId l'id du ressourceAccessor
     * @param dossier le dossier
     */
    public void generateDefaultAuths(Long resourceAccessorId, Dossier dossier){
        RessourceAccessor ressourceAccessor = ressourceAccessorRepository.findById(resourceAccessorId)
                .orElseThrow(() -> new RuntimeException("resourceAccessorId not found"));
        if (ressourceAccessor instanceof Membre) {
            Authorisation selfAuth = Authorisation.generateFullAccess();
            selfAuth.setAuthLevel(AuthLevel.MEMBRE);
            selfAuth.setRessourceAccessor(ressourceAccessor);
            selfAuth.setDossier(dossier);
            Authorisation groupeAuth = Authorisation.generateReadOnly();
            selfAuth.setAuthLevel(AuthLevel.GROUPE);
            groupeAuth.setRessourceAccessor(((Membre) ressourceAccessor).getGroupe());
            groupeAuth.setDossier(dossier);
            Authorisation compagnieAuth = Authorisation.generateNoAuths();
            compagnieAuth.setAuthLevel(AuthLevel.COMPAGNIE);
            compagnieAuth.setRessourceAccessor(((Membre) ressourceAccessor).getGroupe().getCompagnie());
            compagnieAuth.setDossier(dossier);
            dossier.getAuthorisations().add(selfAuth);
            dossier.getAuthorisations().add(groupeAuth);
            dossier.getAuthorisations().add(compagnieAuth);
        }
        if (ressourceAccessor instanceof Compagnie) {
            Authorisation compagnieAuth = Authorisation.generateFullAccess();
            compagnieAuth.setRessourceAccessor((ressourceAccessor));
            compagnieAuth.setDossier(dossier);
            compagnieAuth.setAuthLevel(AuthLevel.COMPAGNIE);
            dossier.getAuthorisations().add(compagnieAuth);
        }
    }

    /**
     * Determine si le ressourceAccessor (L'entité actuellement connectée) est une compagnie ou un membre
     * @return true si le ressourceAccessor est une compagnie, false si c'est une membre
     */
    public boolean determineResourceAssessor(){
        String resourceAccessorRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        return resourceAccessorRole.contains("ROLE_COMPAGNIE");
    }


    /**
     * Extrait la compagnie du ressourceAccessor (L'entité actuellement connectée)
     * @return L'objet compagnie du compte compagnie actuellement connecté
     */
    public Compagnie extractCompagnieFromResourceAccessor(){
        String resourceAccessorName = SecurityContextHolder.getContext().getAuthentication().getName();
        if(determineResourceAssessor()){
            return compagnieRepository.findByNom(resourceAccessorName);
        }
        else{
            Membre membre = membreRepository.findByUsername(resourceAccessorName);
            return membre.getGroupe().getCompagnie();
        }
    }

    /**
     * Extrait le ressourceAccessor (L'entité actuellement connectée) du contexte de sécurité
     * @return L'objet ressourceAccessor du compte actuellement connecté
     */
    public RessourceAccessor extractResourceAccessorFromSecurityContext(){
        String resourceAccessorName = SecurityContextHolder.getContext().getAuthentication().getName();
        if(determineResourceAssessor()){
            return compagnieRepository.findByNom(resourceAccessorName);
        }
        else{
            return membreRepository.findByUsername(resourceAccessorName);
        }
    }
}