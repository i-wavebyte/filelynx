package backend.server.service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @AllArgsConstructor @Data @NoArgsConstructor @Builder
public class Dossier {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nom;
    @ManyToOne
    private Dossier racine;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "racine")
    private List<Fichier> fichiers = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "racine")
    private List<Dossier> dossiers = new ArrayList<>();

    public String getFullPath(){
        String path= "/"+nom;
        if(racine != null) path = racine.getFullPath() + path;
        return path;
    }


}
