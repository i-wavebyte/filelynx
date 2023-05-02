package backend.server.service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Groupe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nom;
    private Double quota;
    @ManyToOne
    private Compagnie compagnie;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "groupe")
    private List<Membre> membres = new ArrayList<>();
}
