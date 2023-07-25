package backend.server.service.Repository;

import backend.server.service.domain.Authorisation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorisationRepository extends JpaRepository<Authorisation, Long> {
}
