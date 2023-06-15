package backend.server.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @OneToMany(cascade = CascadeType.ALL , mappedBy = "dossier")
    private List<Authorisation> authorisations = new ArrayList<>();
    @ManyToOne
    private Compagnie compagnie;

    public String getFullPath(){
        String path= "/"+nom;
        if(racine != null) path = racine.getFullPath() + path;
        return path;
    }

    @Override
    public String toString() {
        return nom;
    }

}
