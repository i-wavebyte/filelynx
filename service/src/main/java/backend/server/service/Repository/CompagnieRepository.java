package backend.server.service.Repository;

import backend.server.service.domain.Compagnie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompagnieRepository extends JpaRepository<Compagnie, Long> {
    Compagnie findByNom(String nom);
    void deleteByNom(String nom);
}
