package backend.server.service.Repository;

import backend.server.service.domain.Authorisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthorisationRepository extends JpaRepository<Authorisation, Long> {
    @Query("SELECT a FROM Authorisation a WHERE a.ressourceAccessor.id = :ressourceAccessorId AND a.dossier.id = :dossierId")
    Optional<Authorisation> findByRessourceAccessorIdAndDossierId(@Param("ressourceAccessorId") Long ressourceAccessorId, @Param("dossierId") Long dossierId);
}