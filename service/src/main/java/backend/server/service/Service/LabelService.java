package backend.server.service.Service;

import backend.server.service.POJO.PageResponse;
import backend.server.service.Repository.LabelRepository;
import backend.server.service.domain.Categorie;
import backend.server.service.domain.Label;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @Transactional @Slf4j
public class LabelService implements ILabelService{

    @Autowired
    private LabelRepository labelRepository;

    public Label addLabel(Label label)
    {
        return labelRepository.save(label);
    }

    public void deleteLabel(Label label)
    {
        labelRepository.delete(label);
    }

    public Label updateLabel(Label label)
    {
        return labelRepository.save(label);
    }

    public List<Label> getAllLabels()
    {
        return labelRepository.findAll();
    }

    public Label getLabel(Long id)
    {
        return labelRepository.findById(id).orElseThrow(()-> new RuntimeException("Label not found"));
    }

    @Override
    public PageResponse<Label> getLabelsPage(int page, int size, String sortBy, String sortOrder, String searchQuery) {
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Sort sort = Sort.by(direction, sortBy);
        int start = page * size;
        int end = Math.min(start + size, (int) labelRepository.count());
        List<Label> labels = labelRepository.findAllByCompagnieNom(compagnieName,sort);
        if (searchQuery != null && !searchQuery.isEmpty()){
            labels = labels.stream()
                    .filter(label -> label.getNom().toLowerCase().contains(searchQuery.toLowerCase()))
                    .collect(Collectors.toList());
        }
        List<Label> pageContent = labels.subList(start, Math.min(end, labels.size()));
        return new PageResponse<>(pageContent, labels.size());
    }
}
