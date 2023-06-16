package backend.server.service.Repository;

import backend.server.service.domain.Dossier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DossierRepository extends JpaRepository<Dossier,Long> {
//    Boolean findByName(String name);
    //find the dossier by compagnie name and has no racine
    Dossier findByCompagnieNomAndRacineIsNull(String compagnieNom);
}
