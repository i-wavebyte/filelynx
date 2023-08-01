package backend.server.service.domain;

import backend.server.service.enums.ETAT;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.swing.*;
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
    @JsonIncludeProperties({"id","nom"})
    private Dossier racine;
    @ManyToOne
    @JsonIncludeProperties({"id","nom"})
    private Categorie categorie;
    @ManyToMany(cascade = CascadeType.ALL)
    @JsonIncludeProperties({"id","nom"})
    private List<Label> labels = new ArrayList<>();
    @Enumerated (value = EnumType.STRING)
    private ETAT etat;
    public String getFullPath(){
        String path= "/"+ nom +"."+extension;
        if(racine != null) path = racine.getFullPath() + path;
        return path;
    }
    @ManyToOne
    @JsonIncludeProperties({"id","nom"})
    private Compagnie compagnie;
    @ManyToOne
    @JsonIncludeProperties({"id","nom"})
    private Groupe Groupe;


    public String toString(){
        String str = "\nFichier: "+nom+"."+extension+" ("+type+")";

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
