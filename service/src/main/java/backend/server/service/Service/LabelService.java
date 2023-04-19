package backend.server.service.Service;

import backend.server.service.Repository.LabelRepository;
import backend.server.service.domain.Label;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service @Transactional @Slf4j
public class LabelService {

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
}
