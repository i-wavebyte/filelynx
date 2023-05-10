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
    @ManyToOne
    private Dossier dossier;
    private boolean lecture;
    private boolean ecriture;
    private boolean modification;
    private boolean suppression;
    private boolean partage;
    private boolean telechargement;
    private boolean upload;
    private boolean creationDossier;
    @ManyToOne
    @JoinColumn(name = "ressource_accessor_id")
    private RessourceAccessor ressourceAccessor;

    public static Authorisation generateFullAccess(){
        return Authorisation.builder()
                .lecture(true)
                .ecriture(true)
                .modification(true)
                .suppression(true)
                .partage(true)
                .telechargement(true)
                .upload(true)
                .creationDossier(true)
                .build();
    }

}
