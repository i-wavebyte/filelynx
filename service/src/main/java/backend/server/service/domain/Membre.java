package backend.server.service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity @AllArgsConstructor @NoArgsConstructor @Builder @Data
public class Membre extends RessourceAccessor{
    private String username;
    private String nom;
    private String prenom;
    private String email;
    @ManyToOne
    private Groupe groupe;
}
