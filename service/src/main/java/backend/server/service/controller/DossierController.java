package backend.server.service.controller;


import backend.server.service.Service.DossierService;
import backend.server.service.Service.IDossierService;
import backend.server.service.domain.Dossier;
import backend.server.service.domain.Fichier;
import backend.server.service.security.POJOs.responses.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dossier")
@CrossOrigin(origins = "*", maxAge = 3600) // Allow requests from any origin for one hour
public class DossierController {

    @Autowired
    private IDossierService dossierService;

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/add/{parentFolderId}")
    public ResponseEntity<?> addDossier(@RequestBody Dossier d, @PathVariable Long parentFolderId) {
        dossierService.addDossier(d, parentFolderId);
        return ResponseEntity.ok(new MessageResponse("dossier ajouté avec succès!"));
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/delete/{dossierId}")
    public ResponseEntity<?> deleteDossier(@PathVariable Long dossierId) {
        dossierService.delete(dossierId);
        return ResponseEntity.ok(new MessageResponse("dossier supprimé avec succès!"));

    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/rename/{dossierId}")
    public ResponseEntity<?> renameDossier(@PathVariable Long dossierId,@RequestParam String name) {
        dossierService.renameDossier(dossierId, name);
        return ResponseEntity.ok(new MessageResponse("dossier renommé avec succès!"));
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/changerEmplacement/{dossierId}")
    public ResponseEntity<?> changerEmplacement(@PathVariable Long dossierId,@RequestParam Long targetFolderId) {
        dossierService.changerEmplacement(dossierId, targetFolderId);
        return ResponseEntity.ok(new MessageResponse("changement de l'emplacement du dossier réussie!"));
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/admin/getChildrenFolders/{dossierId}")
    public List<Dossier> getChildrenDossiers(@PathVariable Long dossierId) {
        return dossierService.getChildrenDossiers(dossierId);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/admin/get/{dossierId}")
    public Dossier getDossier(@PathVariable Long dossierId) {
        return dossierService.getDossier(dossierId);
    }



}
