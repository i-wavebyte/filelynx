package backend.server.service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class RessourceAccessor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(mappedBy = "ressourceAccessor")
    private Authorisation Authorisation;

    @OneToMany (cascade = CascadeType.ALL)
    private List<Authorisation> authorisations = new ArrayList<>();

    @OneToMany
    private List<Log> logs = new ArrayList<>();

}