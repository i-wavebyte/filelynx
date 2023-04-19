package backend.server.service.domain;

import backend.server.service.enums.ETAT;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    @ManyToOne
    private Categorie categorie;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Label> labels = new ArrayList<>();
    @Enumerated (value = EnumType.STRING)
    private ETAT etat;
    public String getFullPath(){
        String path= "/"+ nom +"."+extension;
        if(racine != null) path = racine.getFullPath() + path;
        return path;
    }

    public String toString(){
        String str = "\nFichier: "+nom+"."+extension+" ("+type+")";
        str += " - "+tailleToHighestUnit();
        str += " - "+etat.toString();
        str += " - "+categorie.getNom();
        str += "\n"+labels.size()+" labels:";
        for (Label l: labels) {
            str += "\n\t-"+l.getNom();
        }

        return str;
    }

    public String tailleToHighestUnit(){
        String unit = "o";
        Double taille = this.taille;
        if(taille > 1024){
            taille /= 1024;
            unit = "Ko";
        }
        if(taille > 1024){
            taille /= 1024;
            unit = "Mo";
        }
        if(taille > 1024){
            taille /= 1024;
            unit = "Go";
        }
        return String.format("%.2f", taille) + " " + unit;
    }
}
