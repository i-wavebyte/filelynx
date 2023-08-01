package backend.server.service.Service;

import backend.server.service.Repository.DossierRepository;
import backend.server.service.Repository.FichierRepository;
import backend.server.service.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class DossierService implements IDossierService {

    private final DossierRepository dossierRepository;
    private final FichierRepository fichierRepository;
    private final IFichierService fichierService;
    private final ICompagnieService compagnieService;
    private final IAuthotisationService authotisationService;

    public Dossier addDossier(Dossier d, Long parentFolderId)
    {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);

        List<Dossier> dossiers = compagnie.getDossiers();
        for (Dossier dossier: dossiers)
        {
            if (dossier.getNom().equals(d.getNom()))
                throw new RuntimeException("Dossier: "+d.getNom()+" existe déjà");
        }
        Dossier dossierParent = parentFolderId!=null ? dossierRepository.findById(parentFolderId).orElseThrow(()-> new RuntimeException("Folder not found")): null;
        d.setCompagnie(compagnieService.getCompagnie(compagnieNom));
        d.setRacine(dossierParent);
        d= dossierRepository.save(d);
        if (parentFolderId!=null) {
            dossierParent.getDossiers().add(d);

            dossierRepository.save(dossierParent);
        }
        log.info("File created at {}", d.getFullPath());
        return d;
    }
    public Dossier addDossier(Dossier d, Long parentFolderId, Compagnie compagnie, boolean skipAuthCreation)
    {
        //determine si un dossier portant le méme nom existe déja
        List<Dossier> dossiers = compagnie.getDossiers();
        for (Dossier dossier: dossiers)
        {
            if (dossier.getNom().equals(d.getNom()))
                throw new RuntimeException("Dossier: "+d.getNom()+" existe déjà");
        }
        //determine le dossier parent
        Dossier dossierParent = parentFolderId!=null ? dossierRepository.findById(parentFolderId).orElseThrow(()-> new RuntimeException("Folder not found")): null;
        //ajoute le dossier a la base de donnée
        d.setCompagnie(compagnie);
        d.setRacine(dossierParent);
        //determine le chemin du dossier

        //ajoute les autorisations par défaut
        if(!skipAuthCreation){
            authotisationService.generateDefaultAuths(authotisationService.extractResourceAssessorIdFromSecurityContext(),d);
        }

        if (parentFolderId!=null) {
            dossierParent.getDossiers().add(d);
            dossierRepository.save(dossierParent);
        }
        log.info("File created at {}", d.getFullPath());
        return d;
    }

    public Dossier renameDossier(Long dossierId,String name)
    {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Dossier dossier = dossierRepository.findById(dossierId).orElseThrow(()-> new RuntimeException("Folder not found"));

        List<Dossier> dossiers = compagnie.getDossiers();
        for (Dossier d: dossiers)
        {
            if (d.getNom().equals(name))
                throw new RuntimeException("Dossier: "+name+" existe déjà");
        }
        dossier.setNom(name);
        for(Dossier d : dossier.getDossiers())
        {
            d.setRacine(dossier);
        }
        dossierRepository.saveAll(dossier.getDossiers());
        for(Fichier f : dossier.getFichiers())
        {
            f.setRacine(dossier);
        }
        fichierRepository.saveAll(dossier.getFichiers());
        log.info("File Renamed at {}", dossier.getFullPath());
        return dossierRepository.save(dossier);
    }

    // fix the display here
    public void fileTree(Long DossierId, Long level) {
        Dossier dossier = dossierRepository.findById(DossierId).orElseThrow(() -> new RuntimeException("Folder not found"));
        for (int i = 0; i < level - 1; i++) {
            System.out.print("|   ");
        }
        System.out.println("└── " +"[d]"+ dossier.getNom());
        for (Dossier d : dossier.getDossiers()) {
            fileTree(d.getId(), level + 1);
        }
        for (Fichier f : dossier.getFichiers()) {
            fileTreeFiles(f.getId(), level + 1);
        }
    }

    public void fileTreeFiles(Long fichierId, Long level) {
        Fichier fichier = fichierRepository.findById(fichierId).orElseThrow(() -> new RuntimeException("Folder not found"));
        for (int i = 0; i < level - 1; i++) {
            System.out.print("|   ");
        }
        System.out.println("└── " + fichier.getNom()+"."+fichier.getExtension());
    }

    public void delete(Long DossierId) {
        log.info("Deleting dossier with ID: {}", DossierId);
        Dossier dossier = dossierRepository.findById(DossierId).orElseThrow(() -> new RuntimeException("Folder not found"));
        deleteRecursively(dossier);
    }

    private void deleteRecursively(Dossier dossier) {
        log.info("Deleting recursively dossier with ID: {}", dossier.getId());
        List<Dossier> childDossiers = new ArrayList<>(dossier.getDossiers());

        for (Dossier child : childDossiers) {
            dossier.getDossiers().remove(child);
            child.setRacine(null);
            deleteRecursively(child);
        }

        dossierRepository.delete(dossier);
        log.info("Deleted dossier with ID: {}", dossier.getId());
    }

    public Dossier changerEmplacement(Long dossierId,Long dossierCibleId ) {
        Dossier dossier = dossierRepository.findById(dossierId).orElseThrow(() -> new RuntimeException("Folder not found"));
        Dossier dossierCible = dossierRepository.findById(dossierCibleId).orElseThrow(() -> new RuntimeException("Folder not found"));
        dossier.setRacine(dossierCible);
        return dossierRepository.save(dossier);
    }

    public List<Dossier> getChildrenDossiers(Long dossierId){
        Dossier dossier = dossierRepository.findById(dossierId).orElseThrow(() -> new RuntimeException("Folder not found"));
//        System.out.println("here's the folder: "+ dossier);
        return dossier.getDossiers();
    }

    public Dossier getDossier(Long dossierId) {
        return dossierRepository.findById(dossierId).orElseThrow(() -> new RuntimeException("Folder not found"));
    }

    @Override
    public Dossier getRootDossier() {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        return dossierRepository.findByCompagnieNomAndRacineIsNull(compagnieNom);
    }

    @Override
    public Groupe getGroupRootGroupe(Long dossierId) {
        Dossier dossier = dossierRepository.findById(dossierId).orElseThrow(() -> new RuntimeException("Folder not found"));
        if(dossier.getRacine()==null){
            return null;
        }
        while(!dossier.isGroupRoot()){
            dossier = dossier.getRacine();
        }
        return dossier.getGroupe();
    }

    @Override
    public Dossier getGroupRoot(Groupe groupe) {
        return dossierRepository.findByGroupeIdAndRacineIsNotNullAndIsGroupRootTrue(groupe.getId());
    }


}
