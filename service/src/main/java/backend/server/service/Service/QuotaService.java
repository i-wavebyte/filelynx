package backend.server.service.Service;

import backend.server.service.Repository.AuthorisationRepository;
import backend.server.service.Repository.GroupeRepository;
import backend.server.service.Repository.RessourceAccessorRepository;
import backend.server.service.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service @Transactional @RequiredArgsConstructor
public class QuotaService implements IQuotaService{

    private final AuthorisationRepository authorisationRepository;
    private final AuthotisationService  authotisationService;
    private final RessourceAccessorRepository ressourceAccessorRepository;
    private final GroupeRepository groupeRepository;
    public Double getTotalQuotaOfGroup(Long ressourceAccessorId){
        RessourceAccessor ressourceAccessor = ressourceAccessorRepository.findById(ressourceAccessorId).orElseThrow(()-> new RuntimeException("RessourceAccessor not found"));
        if(ressourceAccessor instanceof Membre){
            ressourceAccessorId = ((Membre) ressourceAccessor).getGroupe().getId();
        }
        else if(ressourceAccessor instanceof Groupe){
            ressourceAccessorId = ressourceAccessor.getId();
        }
        else{
            throw new RuntimeException("ressourceAccessor is not a groupe");
        }
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
        else{
            checkQuotaOfCompagnie(f);
        }
    }

    public double getTotalAllocatedQuota(){
        String compangnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Groupe> groupes = groupeRepository.findAllByCompagnieNom(compangnieName);
        double totalAllocatedQuota = 0;
        for (Groupe groupe : groupes) {
            totalAllocatedQuota += groupe.getQuota();
        }
        return totalAllocatedQuota;
    }
}
