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
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor @Service @Slf4j
public class FichierService implements IFichierService{

    private static final String path = "/Users/macbookpro/Desktop/files/";
    @Autowired
    private FichierRepository fichierRepository;
    @Autowired
    private DossierRepository dossierRepository;
    @Autowired
    private CategorieService categorieService;

    /**
     * ajoute et persiste un nouveau fichier
     * @param f fichier à ajouter
     * @param ParentFolderId id du dossier parent
     * @return le fichier ajouté
     */
    @Override
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

    /**
     * Supprime un fichier
     * @param fichierId id du fichier à supprimer
     */
    @Override
    public void deleteFichier(Long fichierId)
    {
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException("File not found"));
        fichierRepository.delete(file);
    }

    /**
     * Rennome un fichier
     * @param fichierId id du fichier à rennomer
     * @param name nouveau nom du fichier
     * @return le fichier rennomé
     */
    @Override
    public Fichier rename(Long fichierId, String name)
    {
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException("File not found"));
        file.setNom(name);
        return fichierRepository.save(file);
    }

    /**
     * Met à jour un fichier
     * @param f fichier à mettre à jour
     * @return le fichier mis à jour
     */
    @Override
    public Fichier updateFichier(Fichier f)
    {
        return fichierRepository.save(f);
    }

    /**
     * deplace un fichier vers un dossier cible
     * @param fichierId id du fichier à deplacer
     * @param dossierCibleId id du dossier cible
     * @return le fichier deplacé
     */
    @Override
    public Fichier changerEmplacement(Long fichierId,Long dossierCibleId )
    {
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException("File not found"));
        Dossier dossierCible = dossierRepository.findById(dossierCibleId).orElseThrow(()-> new RuntimeException("Folder not found"));
        file.setRacine(dossierCible);
        return fichierRepository.save(file);
    }

    /**
     * retourne un fichier
     * @param id id du fichier à retourner
     * @return le fichier
     */
    @Override
    public Fichier getFichier(Long id)
    {
        return fichierRepository.findById(id).orElseThrow(()-> new RuntimeException("File not found"));
    }

    /**
     * Change l'etat d'un fichier
     * @param fichierId id du fichier à modifier
     * @param etat nouvel etat du fichier
     * @return le fichier modifié
     */
    @Override
    public Fichier updateEtat(Long fichierId, ETAT etat)
    {
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException("File not found"));
        file.setEtat(etat);
        return fichierRepository.save(file);
    }

    /**
     * Change la categorie d'un fichier
     * @param fichierId id du fichier à modifier
     * @param categorieId id de la nouvelle categorie
     * @return le fichier modifié
     */
    @Override
    public Fichier changeCategory(Long fichierId,Long categorieId){
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException("File not found"));
        Categorie categorie = categorieService.getCategorie(categorieId);
        file.setCategorie(categorie);
        return fichierRepository.save(file);
    }

    /**
     * Met ajour les labels d'un fichier
     * @param fichierId id du fichier à modifier
     * @param labels liste des nouveaux labels
     * @return le fichier modifié
     */
    @Override
    public Fichier updateLabels(Long fichierId, List<Label> labels){
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException("File not found"));
        file.setLabels(labels);
        return fichierRepository.save(file);
    }

    /**
     * retourne la liste des fichiers d'un dossier
     * @param parentId id du dossier parent
     * @return la liste des fichiers
     */
    @Override
    public List<Fichier> getFichiersByParent(Long parentId){
        Dossier dossier = dossierRepository.findById(parentId).orElseThrow(()-> new RuntimeException("Folder not found"));
        return dossier.getFichiers();
    }

    /**
     * Charge un fichier vers le disque dur
     * @param file fichier à charger
     * @return le fichier chargé
     */
    /*a Spring-specific class used to handle file uploads.
    returns a List<String> containing the filenames of all
    files present in the directory where the new file will be uploaded*/

    @Override
    public List<String> uploadFile(MultipartFile file)
            throws Exception {
        // Save file on system
        if (!file.getOriginalFilename().isEmpty()) {
            BufferedOutputStream outputStream =
                    new BufferedOutputStream(
                            new FileOutputStream(new File(path,
                                    file.getOriginalFilename())));
            outputStream.write(file.getBytes());
            outputStream.flush();
            outputStream.close();
        } else {
            throw new Exception();
        }
        List<String> list = new ArrayList<String>();
        File files = new File(path);
        String[] fileList = files.list();
        for (String name : fileList) {
            list.add(name);
        }
        ;
        return list;
    }
}
