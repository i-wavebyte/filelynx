package backend.server.service.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Authorisation {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private Dossier dossier;
    private boolean lecture;
    private boolean ecriture;
    private boolean modification;
    private boolean suppression;
    private boolean partage;
    private boolean telechargement;
    private boolean upload;
    private boolean creationDossier;
    @OneToOne
    @JoinColumn(name = "ressource_accessor_id")
    private RessourceAccessor ressourceAccessor;


}
