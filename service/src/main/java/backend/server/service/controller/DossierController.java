package backend.server.service.controller;


import backend.server.service.Repository.LogRepository;
import backend.server.service.Service.CompagnieService;
import backend.server.service.Service.DossierService;
import backend.server.service.Service.IDossierService;
import backend.server.service.domain.Compagnie;
import backend.server.service.domain.Dossier;
import backend.server.service.domain.Fichier;
import backend.server.service.domain.Log;
import backend.server.service.enums.LogType;
import backend.server.service.security.POJOs.responses.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dossier")
@CrossOrigin(origins = "*", maxAge = 3600) // Allow requests from any origin for one hour
public class DossierController {

    @Autowired
    private IDossierService dossierService;

    @Autowired
    private CompagnieService compagnieService;

    @Autowired
    private LogRepository logRepository;

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/add/{parentFolderId}")
    public ResponseEntity<?> addDossier(@RequestBody Dossier d, @PathVariable Long parentFolderId) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        try {
            dossierService.addDossier(d, parentFolderId);
            Log logMessage = Log.builder().message("Dossier "+d.getNom()+" ajouté à la société "+compagnieNom).type(LogType.CRÉER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
            logRepository.save(logMessage);
            return ResponseEntity.ok(new MessageResponse("dossier ajouté avec succès!"));
        }
        catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/admin/getRoot")
    public Dossier getRoot() {
        return dossierService.getRootDossier();
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
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        try{
            dossierService.renameDossier(dossierId, name);
            Log logMessage = Log.builder().message("Dossier "+name+" ajouté à la société "+compagnieNom).type(LogType.MODIFIER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
            logRepository.save(logMessage);
            return ResponseEntity.ok(new MessageResponse("dossier renommé avec succès!"));
        }
        catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
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
