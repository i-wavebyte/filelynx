package backend.server.service.Service;

import backend.server.service.Repository.DossierRepository;
import backend.server.service.Repository.FichierRepository;
import backend.server.service.domain.Compagnie;
import backend.server.service.domain.Dossier;
import backend.server.service.domain.Fichier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class DossierService {
    @Autowired
    private DossierRepository dossierRepository;
    @Autowired
    private FichierRepository fichierRepository;
    @Autowired
    private FichierService fichierService;
    @Autowired
    private CompagnieService compagnieService;
    public Dossier addDossier(Dossier d, Long ParentFolderId)
    {
        Dossier dossierParent = ParentFolderId!=null ? dossierRepository.findById(ParentFolderId).orElseThrow(()-> new RuntimeException("Folder not found")): null;
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        d.setCompagnie(compagnieService.getCompagnie(compagnieNom));
        d.setRacine(dossierParent);
        d= dossierRepository.save(d);
        if (ParentFolderId!=null) {
            dossierParent.getDossiers().add(d);

            dossierRepository.save(dossierParent);
        }
        log.info("File created at {}", d.getFullPath());
        return d;
    }

    public Dossier addDossier(Dossier d, Long ParentFolderId, Compagnie compagnie)
    {
        Dossier dossierParent = ParentFolderId!=null ? dossierRepository.findById(ParentFolderId).orElseThrow(()-> new RuntimeException("Folder not found")): null;

        d.setCompagnie(compagnie);
        d.setRacine(dossierParent);
        d= dossierRepository.save(d);
        if (ParentFolderId!=null) {
            dossierParent.getDossiers().add(d);
            dossierRepository.save(dossierParent);
        }
        log.info("File created at {}", d.getFullPath());
        return d;
    }

    public Dossier renameDossier(Long DossierId,String name)
    {
        Dossier dossier = dossierRepository.findById(DossierId).orElseThrow(()-> new RuntimeException("Folder not found"));
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

}
