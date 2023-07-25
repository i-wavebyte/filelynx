package backend.server.service.Service;

import backend.server.service.Repository.AuthorisationRepository;
import backend.server.service.Repository.RessourceAccessorRepository;
import backend.server.service.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor @Service @Slf4j @Transactional
public class AuthotisationService {

    private final RessourceAccessorRepository ressourceAccessorRepository;
    private final AuthorisationRepository authorisationRepository;
    public Authorisation GetAuthorisation(Long ressourceAccessorId, Long dossierId) {
        Optional<Authorisation> auth;
        RessourceAccessor ressourceAccessor = ressourceAccessorRepository.findById(ressourceAccessorId).orElseThrow(()->
                new RuntimeException("resourceAccessorId not found"));
        if(ressourceAccessor instanceof Membre){
            auth = authorisationRepository.findByRessourceAccessorIdAndDossierId(ressourceAccessorId,dossierId);
            if(auth.isPresent()){
                return auth.get();
            }
            else{
                ressourceAccessor = ((Membre) ressourceAccessor).getGroupe();
            }
        }

        if(ressourceAccessor instanceof Groupe){
            auth = authorisationRepository.findByRessourceAccessorIdAndDossierId(ressourceAccessorId,dossierId);
            if(auth.isPresent()){
                return auth.get();
            }
            else{
                ressourceAccessor = ((Groupe) ressourceAccessor).getCompagnie();
            }
        }

        if(ressourceAccessor instanceof Compagnie){
            auth = authorisationRepository.findByRessourceAccessorIdAndDossierId(ressourceAccessorId,dossierId);
            if(auth.isPresent()){
                return auth.get();
            }
            else{
                throw new RuntimeException("Authorisation not found");
            }
        }
        throw new RuntimeException("Authorisation not found");
    }

    public boolean hasAuth(Long resourceAccessorId, Long dossierId,String authType){
        Authorisation auth = GetAuthorisation(ressourceAccessorId,dossierId);

        if(authType.equals("lecture")){
            return auth.isLecture();
        }
        if(authType.equals("ecriture")){
            return auth.isEcriture();
        }
        if(authType.equals("partage")){
            return auth.isPartage();
        }
        if(authType.equals("suppression")){
            return auth.isSuppression();
        }
        if(authType.equals("upload")){
            return auth.isUpload();
        }
        if(authType.equals("modification")){
            return auth.isModification();
        }
        if(authType.equals("telechargement")){
            return auth.isTelechargement();
        }
        if(authType.equals("creationDossier")){
            return auth.isCreationDossier();
        }
        throw new RuntimeException("Invalid AuthType");
    }

    public void authorize(Long resourceAccessorId, Long dossierId,String authType){
        if(!hasAuth(resourceAccessorId, dossierId, authType)){
            throw new RuntimeException("Access denied");
        }
    }
}
