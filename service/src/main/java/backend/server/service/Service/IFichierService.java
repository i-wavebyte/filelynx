package backend.server.service.Service;

import backend.server.service.domain.Fichier;
import backend.server.service.domain.Label;
import backend.server.service.enums.ETAT;

import java.util.List;

public interface IFichierService {

    Fichier addFichier(Fichier f, Long ParentFolderId);
    void deleteFichier(Long fichierId);
    Fichier rename(Long fichierId, String name);
    Fichier updateFichier(Fichier f);
    Fichier changerEmplacement(Long fichierId,Long dossierCibleId );
    Fichier getFichier(Long id);
    Fichier updateEtat(Long fichierId, ETAT etat);
    Fichier changeCategory(Long fichierId,Long categorieId);
    Fichier updateLabels(Long fichierId, List<Label> labels);
    List<Fichier> getFichiersByParent(Long parentId);
}
