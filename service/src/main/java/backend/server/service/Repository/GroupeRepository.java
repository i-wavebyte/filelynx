package backend.server.service.Repository;

import backend.server.service.domain.Groupe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupeRepository extends JpaRepository<Groupe, Long> {
    Groupe findByNomAndCompagnieNom(String nom, String compagnieNom);
}
