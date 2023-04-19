package backend.server.service.Repository;

import backend.server.service.domain.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategorieRepository extends JpaRepository<Categorie,Long> {
    Categorie findByNom(String nom);
}
