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

    @Override
    public void deleteFichier(Long fichierId)
    {
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException("File not found"));
        fichierRepository.delete(file);
    }
    @Override
    public Fichier rename(Long fichierId, String name)
    {
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException("File not found"));
        file.setNom(name);
        return fichierRepository.save(file);
    }
    @Override
    public Fichier updateFichier(Fichier f)
    {
        return fichierRepository.save(f);
    }
    @Override
    public Fichier changerEmplacement(Long fichierId,Long dossierCibleId )
    {
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException("File not found"));
        Dossier dossierCible = dossierRepository.findById(dossierCibleId).orElseThrow(()-> new RuntimeException("Folder not found"));
        file.setRacine(dossierCible);
        return fichierRepository.save(file);
    }
    @Override
    public Fichier getFichier(Long id)
    {
        return fichierRepository.findById(id).orElseThrow(()-> new RuntimeException("File not found"));
    }
    @Override
    public Fichier updateEtat(Long fichierId, ETAT etat)
    {
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException("File not found"));
        file.setEtat(etat);
        return fichierRepository.save(file);
    }
    @Override
    public Fichier changeCategory(Long fichierId,Long categorieId){
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException("File not found"));
        Categorie categorie = categorieService.getCategorie(categorieId);
        file.setCategorie(categorie);
        return fichierRepository.save(file);
    }
    @Override
    public Fichier updateLabels(Long fichierId, List<Label> labels){
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException("File not found"));
        file.setLabels(labels);
        return fichierRepository.save(file);
    }
    @Override
    public List<Fichier> getFichiersByParent(Long parentId){
        Dossier dossier = dossierRepository.findById(parentId).orElseThrow(()-> new RuntimeException("Folder not found"));
        return dossier.getFichiers();
    }

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
        return list;
    }
}
