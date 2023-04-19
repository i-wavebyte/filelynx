package backend.server.service.Repository;

import backend.server.service.domain.Dossier;
import backend.server.service.domain.Fichier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FichierRepository extends JpaRepository<Fichier,Long> {
}
