package backend.server.service.domain;

import backend.server.service.enums.AuthLevel;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
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
    @JsonIncludeProperties({"id","nom"})
    private Dossier dossier;
    private boolean lecture;
    private boolean ecriture;
    private boolean modification;
    private boolean suppression;
    private boolean partage;
    private boolean telechargement;
    private boolean upload;
    private boolean creationDossier;
    @Enumerated(EnumType.STRING)
    private AuthLevel authLevel;
    @ManyToOne
    @JsonIncludeProperties({"id"})
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

    public static Authorisation generateReadOnly(){
        return Authorisation.builder()
                .lecture(true)
                .ecriture(false)
                .modification(false)
                .suppression(false)
                .partage(false)
                .telechargement(false)
                .upload(false)
                .creationDossier(false)
                .build();
    }

    private void checkLecture() {
        if (!this.lecture) {
            throw new RuntimeException("l'entité doit avoir le droit a la lecture pour avoir ce droit");
        }
    }

    public void setLecture(boolean lecture) {
        this.lecture = lecture;
    }

    public void setEcriture(boolean ecriture) {
        checkLecture();
        this.ecriture = ecriture;
    }

    public void setModification(boolean modification) {
        checkLecture();
        this.modification = modification;
    }

    public void setSuppression(boolean suppression) {
        checkLecture();
        this.suppression = suppression;
    }

    public void setPartage(boolean partage) {
        checkLecture();
        this.partage = partage;
    }

    public void setTelechargement(boolean telechargement) {
        checkLecture();
        this.telechargement = telechargement;
    }

    public void setUpload(boolean upload) {
        checkLecture();
        this.upload = upload;
    }

    public void setCreationDossier(boolean creationDossier) {
        checkLecture();
        this.creationDossier = creationDossier;
    }
}
