package backend.server.service.Service;

import backend.server.service.POJO.PageResponse;
import backend.server.service.domain.Groupe;
import backend.server.service.Repository.GroupeRepository;
import backend.server.service.domain.Membre;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @Transactional @Slf4j
public class GroupeService implements IGroupeService{

    private final GroupeRepository groupeRepository;

    public GroupeService(GroupeRepository groupeRepository) {
        this.groupeRepository = groupeRepository;
    }
    @Override
    public Groupe addGroupe(Groupe groupe) {
        return groupeRepository.save(groupe);
    }

    @Override
    public Groupe getGroupe(String nom, String compagnieNom) {
        log.info("nom: "+ nom + " compagnieNom: "+ compagnieNom);
        return groupeRepository.findByNomAndCompagnieNom(nom, compagnieNom);
    }
    @Override
    public Groupe getGroupe(Long id) {
        return groupeRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteGroupe(Long id) {
        groupeRepository.deleteById(id);
    }

    @Override
    public Groupe updateGroupe(Groupe groupe) {
        return groupeRepository.save(groupe);
    }
   @Override
    public PageResponse<Groupe> getGroupesPage(int page, int size, String sortBy, String sortOrder, String searchQuery ){

        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Sort sort = Sort.by(direction, sortBy);
        int start = page * size;
        int end = Math.min(start + size, (int) groupeRepository.count());
        List<Groupe> groupes = groupeRepository.findAllByCompagnieNom(compagnieName,sort);
        if (searchQuery != null && !searchQuery.isEmpty()){
            groupes = groupes.stream()
                    .filter(groupe -> groupe.getNom().toLowerCase().contains(searchQuery.toLowerCase()))
                    .collect(Collectors.toList());
        }
        List<Groupe> pageContent = groupes.subList(start, Math.min(end, groupes.size()));
        return new PageResponse<>(pageContent, groupes.size());
    }

}
