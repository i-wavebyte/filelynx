package backend.server.service.Repository;

import backend.server.service.domain.Groupe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupeRepository extends JpaRepository<Groupe, Long> {
    // find a group by its name and the name of the company it belongs to
    Groupe findByNomAndCompagnieNom(String nom, String compagnieNom);
}
