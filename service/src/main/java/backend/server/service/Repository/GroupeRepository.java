package backend.server.service.Repository;

import backend.server.service.domain.Groupe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupeRepository extends JpaRepository<Groupe, Long> {
    // find a group by its name and the name of the company it belongs to
    Groupe findByNomAndCompagnieNom(String nom, String compagnieNom);
    // calculate the sum of quotas for all groups of a company
    @Query("SELECT COALESCE(SUM(g.quota), 0) FROM Groupe g WHERE g.compagnie.nom = :compagnieNom")
    Double sumQuotasByCompagnieNom(@Param("compagnieNom") String compagnieNom);
    Groupe findByNom(String nom);
    Groupe findByIdAndCompagnieNom(Long id, String compagnieNom);
}
