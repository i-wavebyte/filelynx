package backend.server.service.Repository;

import backend.server.service.domain.Membre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MembreRepository extends JpaRepository<Membre, Long> {
    Membre findByUsername(String username);

}
