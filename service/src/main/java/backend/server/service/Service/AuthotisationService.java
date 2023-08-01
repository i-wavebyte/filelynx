package backend.server.service.Service;

import backend.server.service.Repository.AuthorisationRepository;
import backend.server.service.Repository.CompagnieRepository;
import backend.server.service.Repository.MembreRepository;
import backend.server.service.Repository.RessourceAccessorRepository;
import backend.server.service.domain.*;
import backend.server.service.enums.AuthLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Required;
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

    public void authorize(Long resourceAccessorId, Long dossierId, String authType) {
        log.debug("Checking authorization for user with resourceAccessorId {} and authType {}", resourceAccessorId, authType);
        if (!hasAuth(resourceAccessorId, dossierId, authType)) {
            log.debug("Access denied for user with resourceAccessorId {} and authType {}", resourceAccessorId, authType);
            throw new RuntimeException("Access denied");
        }
    }

    public void generateDefaultAuths(Long resourceAccessorId, Dossier dossier){
        RessourceAccessor ressourceAccessor = ressourceAccessorRepository.findById(resourceAccessorId)
                .orElseThrow(() -> new RuntimeException("resourceAccessorId not found"));
        if (ressourceAccessor instanceof Membre) {
            Authorisation selfAuth = Authorisation.generateFullAccess();
            selfAuth.setAuthLevel(AuthLevel.MEMBRE);
            selfAuth.setRessourceAccessor(ressourceAccessor);
            selfAuth.setDossier(dossier);
            //authorisationRepository.save(selfAuth);
            Authorisation groupeAuth = Authorisation.generateReadOnly();
            selfAuth.setAuthLevel(AuthLevel.GROUPE);
            groupeAuth.setRessourceAccessor(((Membre) ressourceAccessor).getGroupe());
            groupeAuth.setDossier(dossier);
            //authorisationRepository.save(groupeAuth);
            Authorisation compagnieAuth = Authorisation.generateReadOnly();
            selfAuth.setAuthLevel(AuthLevel.COMPAGNIE);
            compagnieAuth.setRessourceAccessor(((Membre) ressourceAccessor).getGroupe().getCompagnie());
            compagnieAuth.setDossier(dossier);
            dossier.getAuthorisations().add(selfAuth);
            dossier.getAuthorisations().add(groupeAuth);
            dossier.getAuthorisations().add(compagnieAuth);
            //authorisationRepository.save(compagnieAuth);

        }
        if (ressourceAccessor instanceof Compagnie) {
            Authorisation compagnieAuth = Authorisation.generateFullAccess();
            compagnieAuth.setRessourceAccessor(((Membre) ressourceAccessor).getGroupe().getCompagnie());
            compagnieAuth.setDossier(dossier);
            dossier.getAuthorisations().add(compagnieAuth);
            //authorisationRepository.save(compagnieAuth);
        }
    }
    public boolean determineResourceAssessor(){
        String resourceAccessorRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        return resourceAccessorRole.contains("ROLE_COMPAGNIE");
    }



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