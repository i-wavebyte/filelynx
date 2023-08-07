package backend.server.service.Service;

import backend.server.service.domain.Compagnie;
import backend.server.service.domain.Groupe;
import backend.server.service.domain.Membre;
import backend.server.service.payloads.ConsumptionHistoryChart;
import backend.server.service.payloads.EntitiesCountResponse;
import backend.server.service.payloads.GroupConsumption;
import backend.server.service.payloads.QuotaUsedToday;

import java.util.List;

public interface ICompagnieService {

    Compagnie getCompagnie(Long id);
    Compagnie getCompagnie(String nom);
    List<Compagnie> getAllCompagnies();
    Compagnie createCompagnie(Compagnie compagnie);
    Compagnie updateCompagnie(Compagnie compagnie);
    void deleteCompagnie(Long id);
    void deleteCompagnie(String nom);
    Groupe createGroupe(String nom, double quota);
    Groupe createGroupe(String nom, double quota, Long CompagnieId);
    void deleteGroupe(String nom);
    Groupe updateGroupe(Long groupeId, String newName);
    void deleteMembre(Long membreId, String username);
    Membre updateMembre(Membre membre);
    List<String> getAllUniqueGroups();
    EntitiesCountResponse getEntitiesCount();
    QuotaUsedToday getQuotaUsedToday();

    List<String> getAllLabels();

    List<String> getAllCategories();

    ConsumptionHistoryChart getQuotaUsedByDay();

    List<GroupConsumption> getAllGroupsConsumption();
}
