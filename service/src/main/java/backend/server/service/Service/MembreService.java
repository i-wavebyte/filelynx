package backend.server.service.Service;

import backend.server.service.POJO.PageResponse;
import backend.server.service.Repository.MembreRepository;
import backend.server.service.domain.Membre;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional @Slf4j
@AllArgsConstructor
public class MembreService {

    private final MembreRepository membreRepository;

    public Membre getMembre(Long id){
        return membreRepository.findById(id).orElseThrow(()-> new RuntimeException("Membre not found") );
    }

    public Membre addMembre(Membre membre){
        return membreRepository.save(membre);
    }

    public Membre updateMembre(Membre membre){
        return membreRepository.save(membre);
    }

    public void deleteMembre(Long id){
        membreRepository.deleteById(id);
    }

    public Membre getMembre(String username){
        return membreRepository.findByUsername(username);
    }

    public PageResponse<Membre> getMembresPage(int page, int size, String sortBy, String searchQuery, String sortOrder, String groupFilter ){

        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Sort sort = Sort.by(direction, sortBy);
        int start = page * size;
        int end = Math.min(start + size, (int) membreRepository.count());
        List<Membre> membres = membreRepository.findAll(sort);

        if (searchQuery != null && !searchQuery.isEmpty()){
            membres = membres.stream()
                    .filter(membre -> membre.getUsername().toLowerCase().contains(searchQuery.toLowerCase())
                            || membre.getNom().toLowerCase().contains(searchQuery.toLowerCase())
                            || membre.getPrenom().toLowerCase().contains(searchQuery.toLowerCase())
                            || membre.getEmail().toLowerCase().contains(searchQuery.toLowerCase())
                    )
                    .collect(Collectors.toList());
        }

        if (groupFilter != null && !groupFilter.isEmpty()) {
            membres = membres.stream()
                    .filter(professor -> professor.getGroupe().getNom().equalsIgnoreCase(groupFilter))
                    .collect(Collectors.toList());
        }

        List<Membre> pageContent = membres.subList(start, Math.min(end, membres.size()));
        return new PageResponse<>(pageContent, membres.size());
    }

}
