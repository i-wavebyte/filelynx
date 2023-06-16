package backend.server.service.Service;

import backend.server.service.domain.Compagnie;
import backend.server.service.domain.Dossier;

import java.util.List;

public interface IDossierService {
    Dossier addDossier(Dossier d, Long ParentFolderId);
    Dossier addDossier(Dossier d, Long ParentFolderId, Compagnie compagnie);
    Dossier renameDossier(Long DossierId,String name);

    void fileTree(Long DossierId, Long level);

    void fileTreeFiles(Long fichierId, Long level);

    void delete(Long DossierId);
    Dossier changerEmplacement(Long dossierId,Long dossierCibleId );
    List<Dossier> getChildrenDossiers(Long dossierId);
    Dossier getDossier(Long dossierId);
}
