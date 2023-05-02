package backend.server.service.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @AllArgsConstructor @NoArgsConstructor @Data
public class Compagnie {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nom;
    private Double quota;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "compagnie")
    private List<Groupe> groupes = new ArrayList<>();
}
