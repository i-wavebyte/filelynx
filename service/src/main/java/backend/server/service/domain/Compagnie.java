package backend.server.service.domain;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @AllArgsConstructor @NoArgsConstructor @Data
public class Compagnie extends RessourceAccessor{
    private String nom;
    private Double quota;
    private Double usedQuota;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "compagnie")
    private List<Groupe> groupes = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "compagnie")
    private List<Dossier> dossiers = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "compagnie")
    private List<Fichier> fichiers = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "compagnie") @JsonIncludeProperties({"id", "message", "type", "date", "trigger"})
    private List<Log> logs = new ArrayList<>();
}
