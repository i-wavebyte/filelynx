package backend.server.service.Service;

import backend.server.service.Repository.DossierRepository;
import backend.server.service.Repository.FichierRepository;
import backend.server.service.domain.Categorie;
import backend.server.service.domain.Dossier;
import backend.server.service.domain.Fichier;
import backend.server.service.domain.Label;
import backend.server.service.enums.ETAT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor @Service @Slf4j
public class FichierService {
    @Autowired
    private FichierRepository fichierRepository;
    @Autowired
    private DossierRepository dossierRepository;
    @Autowired
    private CategorieService categorieService;

    public Fichier addFichier(Fichier f, Long ParentFolderId)
    {
        Dossier dossierParent = ParentFolderId!=null ? dossierRepository.findById(ParentFolderId).orElseThrow(()-> new RuntimeException("Folder not found")): null;

        f.setRacine(dossierParent);
        f= fichierRepository.save(f);
        if (ParentFolderId!=null) {
            dossierParent.getFichiers().add(f);

            dossierRepository.save(dossierParent);
        }
        log.info("File created at {}", f.getFullPath());
        return f;
    }

    public void deleteFichier(Long fichierId)
    {
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException("File not found"));
        fichierRepository.delete(file);
    }

    public Fichier rename(Long fichierId, String name)
    {
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException("File not found"));
        file.setNom(name);
        return fichierRepository.save(file);
    }

    public Fichier updateFichier(Fichier f)
    {
        return fichierRepository.save(f);
    }

    public Fichier changerEmplacement(Long fichierId,Long dossierCibleId )
    {
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException("File not found"));
        Dossier dossierCible = dossierRepository.findById(dossierCibleId).orElseThrow(()-> new RuntimeException("Folder not found"));
        file.setRacine(dossierCible);
        return fichierRepository.save(file);
    }

    public Fichier getFichier(Long id)
    {
        return fichierRepository.findById(id).orElseThrow(()-> new RuntimeException("File not found"));
    }

    public Fichier updateEtat(Long fichierId, ETAT etat)
    {
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException("File not found"));
        file.setEtat(etat);
        return fichierRepository.save(file);
    }

    public Fichier changeCategory(Long fichierId,Long categorieId){
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException("File not found"));
        Categorie categorie = categorieService.getCategorie(categorieId);
        file.setCategorie(categorie);
        return fichierRepository.save(file);
    }

    public Fichier updateLabels(Long fichierId, List<Label> labels){
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException("File not found"));
        file.setLabels(labels);
        return fichierRepository.save(file);
    }

    public List<Fichier> getFichiersByParent(Long parentId){
        Dossier dossier = dossierRepository.findById(parentId).orElseThrow(()-> new RuntimeException("Folder not found"));
        return dossier.getFichiers();
    }

}
