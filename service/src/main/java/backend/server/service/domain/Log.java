package backend.server.service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity @NoArgsConstructor @AllArgsConstructor @Data @Builder
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String message;
    private String type;
    private String date;
    @ManyToOne (cascade = CascadeType.ALL)
    private RessourceAccessor trigger;
    @ManyToOne (cascade = CascadeType.ALL)
    private Compagnie compagnie;
}
