package backend.server.service.Service;

import backend.server.service.Repository.DossierRepository;
import backend.server.service.Repository.FichierRepository;
import backend.server.service.domain.Dossier;
import backend.server.service.domain.Fichier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor @Service @Transactional @Slf4j
public class FichierService {
    @Autowired
    private FichierRepository fichierRepository;
    @Autowired
    private DossierRepository dossierRepository;

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

    public void deleteFichier(Long id)
    {
        Fichier file = fichierRepository.findById(id).orElseThrow(()-> new RuntimeException("File not found"));
        fichierRepository.delete(file);
    }
}
