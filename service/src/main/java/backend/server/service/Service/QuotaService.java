package backend.server.service.Service;

import backend.server.service.Repository.AuthorisationRepository;
import backend.server.service.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service @Transactional @RequiredArgsConstructor
public class QuotaService implements IQuotaService{


    private final AuthorisationRepository authorisationRepository;
    private final AuthotisationService  authotisationService;

    public Double getTotalQuotaOfGroup(Long ressourceAccessorId){
        List<Authorisation> authorisations = authorisationRepository.findAllByRessourceAccessorId(ressourceAccessorId);
        Double totalQuota = 0.0;
        for (Authorisation authorisation : authorisations) {
            for(Fichier dossier : authorisation.getDossier().getFichiers()){
                totalQuota += dossier.getTaille();
            }
        }
        return totalQuota;
    }

    public Double getTotalQuotaOfCompagnie(){
        Compagnie compagnie = (Compagnie) authotisationService.extractResourceAccessorFromSecurityContext();
        Double totalQuota = 0.0;
        for (Groupe groupe : compagnie.getGroupes()) {
            totalQuota += getTotalQuotaOfGroup(groupe.getId());
        }
        return totalQuota;
    }

    public void CheckQuotaOfGroupe(Fichier f){
        RessourceAccessor ressourceAccessor = authotisationService.extractResourceAccessorFromSecurityContext();
        if(ressourceAccessor instanceof Membre){
            ressourceAccessor = ((Membre) ressourceAccessor).getGroupe();
            Double totalQuota = getTotalQuotaOfGroup(ressourceAccessor.getId());
            if(totalQuota + f.getTaille() > ((Groupe) ressourceAccessor).getQuota()){
                throw new RuntimeException("Quota dépassé pour le groupe");
            }
        }
    }

    public void checkQuotaOfCompagnie(Fichier f){
        Compagnie compagnie = (Compagnie) authotisationService.extractResourceAccessorFromSecurityContext();
        Double totalQuota = getTotalQuotaOfCompagnie();
        if(totalQuota + f.getTaille() > compagnie.getQuota()){
            throw new RuntimeException("Quota dépassé pour la compagnie");
        }
    }

    public void QuotaAuthFilter(Fichier f){
        RessourceAccessor ressourceAccessor = authotisationService.extractResourceAccessorFromSecurityContext();
        if(ressourceAccessor instanceof Membre){
            checkQuotaOfCompagnie(f);
            CheckQuotaOfGroupe(f);
        }

    }
}
