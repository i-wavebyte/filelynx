package backend.server.service.Service;

import backend.server.service.POJO.PageResponse;
import backend.server.service.domain.Groupe;

public interface IGroupeService {
    Groupe addGroupe(Groupe groupe);
    Groupe getGroupe(String nom, String compagnieNom);
    Groupe getGroupe(Long id);
    void deleteGroupe(Long id);
    Groupe updateGroupe(Groupe groupe);
    PageResponse<Groupe> getGroupesPage(int page, int size, String sortBy, String sortOrder, String searchQuery );
}
