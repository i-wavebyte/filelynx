package backend.server.service.Service;

import backend.server.service.POJO.PageResponse;
import backend.server.service.domain.Categorie;
import backend.server.service.domain.Label;

import java.util.List;

public interface ILabelService {
    public Label addLabel(Label label);
    public void deleteLabel(Label label);
    public Label updateLabel(Label label);
    public List<Label> getAllLabels();
    public Label getLabel(Long id);
    PageResponse<Label> getLabelsPage(int page, int size, String sortBy, String sortOrder, String searchQuery );
}