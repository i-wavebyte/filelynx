package backend.server.service.Repository;

import backend.server.service.domain.Dossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DossierRepository extends JpaRepository<Dossier,Long> {
    @Query("SELECT COUNT(s) > 0 FROM Dossier s WHERE s.nom = :name")
    Boolean existsByName(String name);
}
