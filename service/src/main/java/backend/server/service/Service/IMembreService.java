package backend.server.service.Service;

import backend.server.service.POJO.PageResponse;
import backend.server.service.domain.Membre;

import java.util.List;

public interface IMembreService {
    Membre getMembre(Long id);
    Membre addMembre(Membre membre);
    Membre updateMembre(Membre membre);
    void deleteMembre(Long id);
    Membre getMembre(String username);
    PageResponse<Membre> getMembresPage(int page, int size, String sortBy, String sortOrder, String searchQuery, String groupFilter );
    List<Membre> getMembresByGroupeId(Long groupeId);
}
