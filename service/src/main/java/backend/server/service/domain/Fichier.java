package backend.server.service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class Fichier {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nom;
    private String extension;
    private String realPath;
    private String type;
    private Double taille;
    @ManyToOne
    private Dossier racine;

    public String getFullPath(){
        String path= "/"+ nom +"."+extension;
        if(racine != null) path = racine.getFullPath() + path;
        return path;
    }
}
