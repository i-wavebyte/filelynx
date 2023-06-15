package backend.server.service.controller;

import backend.server.service.Service.FichierService;
import backend.server.service.domain.Fichier;
import backend.server.service.domain.Label;
import backend.server.service.security.POJOs.responses.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fichier")
@CrossOrigin(origins = "*", maxAge = 3600) // Allow requests from any origin for one hour
public class FileController {

    @Autowired
    private FichierService fichierService;

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/add/{parentFolderId}")
    public ResponseEntity<?> addFile(@RequestBody Fichier f,@PathVariable Long parentFolderId) {
        fichierService.addFichier(f, parentFolderId);
        return ResponseEntity.ok(new MessageResponse("fichier ajouté avec succès!"));

    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/delete/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long fileId) {
        fichierService.deleteFichier(fileId);
        return ResponseEntity.ok(new MessageResponse("fichier supprimé avec succès!"));

    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/rename/{fileId}")
    public ResponseEntity<?> renameFile(@PathVariable Long fileId,@RequestParam String name) {
        fichierService.rename(fileId, name);
        return ResponseEntity.ok(new MessageResponse("fichier renommé avec succès!"));

    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/update")
    public ResponseEntity<?> updateFile(@RequestBody Fichier f) {
        fichierService.updateFichier(f);
        return ResponseEntity.ok(new MessageResponse("fochier modifie avec succès!"));

    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/move/{fileId}")
    public ResponseEntity<?> moveFile(@PathVariable Long fileId,@RequestParam Long targetFolderId) {
        fichierService.changerEmplacement(fileId, targetFolderId);
        return ResponseEntity.ok(new MessageResponse("changement de l'emplacement du fichier réussie!"));

    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/admin/get")
    public Fichier getFile(@RequestBody Long fileId) {
        return fichierService.getFichier(fileId);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/admin/changeCategory/{fileId}")
    public void changeCategory(@PathVariable Long fileId,@RequestBody Long categoryId) {
        fichierService.changeCategory(fileId, categoryId);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/admin/changeLabel/{fileId}")
    public void changeLabel(@PathVariable Long fileId,@RequestBody List<Label> labels) {
        fichierService.updateLabels(fileId, labels);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/admin/getFromParent")
    public List<Fichier> getFilesFromParent(@RequestBody Long parentFolderId) {
        return fichierService.getFichiersByParent(parentFolderId);
    }

}
