package backend.server.service.Service;

import backend.server.service.domain.Authorisation;
import backend.server.service.domain.Compagnie;
import backend.server.service.domain.Dossier;
import backend.server.service.domain.RessourceAccessor;

public interface IAuthotisationService {
    Long extractResourceAssessorIdFromSecurityContext();
    void generateDefaultAuths(Long resourceAccessorId, Dossier dossier);
    Authorisation getAuthorisation(Long ressourceAccessorId, Long dossierId);
    boolean hasAuth(Long resourceAccessorId, Long dossierId, String authType);
    void authorize(Long resourceAccessorId, Long dossierId, String authType);
    boolean determineResourceAssessor();
    Compagnie extractCompagnieFromResourceAccessor();
    RessourceAccessor extractResourceAccessorFromSecurityContext();
}
