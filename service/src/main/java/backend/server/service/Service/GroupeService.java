package backend.server.service.Service;

import backend.server.service.POJO.PageResponse;
import backend.server.service.domain.Groupe;
import backend.server.service.Repository.GroupeRepository;
import backend.server.service.domain.Membre;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @Transactional @Slf4j @RequiredArgsConstructor
public class GroupeService {

    private final GroupeRepository groupeRepository;

    Groupe addGroupe(Groupe groupe) {
        return groupeRepository.save(groupe);
    }

    public Groupe getGroupe(String nom, String compagnieNom) {
        return groupeRepository.findByNomAndCompagnieNom(nom, compagnieNom);
    }

    public Groupe getGroupe(Long id) {
        return groupeRepository.findById(id).orElse(null);
    }


    void deleteGroupe(Long id) {
        groupeRepository.deleteById(id);
    }

    Groupe updateGroupe(Groupe groupe) {
        return groupeRepository.save(groupe);
    }

//    public PageResponse<Groupe> getGroupesPage(int page, int size, String sortBy, String sortOrder, String searchQuery ){
//
//        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
//        Sort sort = Sort.by(direction, sortBy);
//        int start = page * size;
//        int end = Math.min(start + size, (int) membreRepository.count());
//        List<Membre> membres = membreRepository.findAll(sort);
//
//        if (searchQuery != null && !searchQuery.isEmpty()){
//            membres = membres.stream()
//                    .filter(membre -> membre.getUsername().toLowerCase().contains(searchQuery.toLowerCase())
//                            || membre.getNom().toLowerCase().contains(searchQuery.toLowerCase())
//                            || membre.getPrenom().toLowerCase().contains(searchQuery.toLowerCase())
//                            || membre.getEmail().toLowerCase().contains(searchQuery.toLowerCase())
//                    )
//                    .collect(Collectors.toList());
//        }
//
//        if (groupFilter != null && !groupFilter.isEmpty()) {
//            membres = membres.stream()
//                    .filter(professor -> professor.getGroupe().getNom().equalsIgnoreCase(groupFilter))
//                    .collect(Collectors.toList());
//        }
//
//        List<Membre> pageContent = membres.subList(start, Math.min(end, membres.size()));
//        return new PageResponse<>(pageContent, membres.size());
//    }

}
