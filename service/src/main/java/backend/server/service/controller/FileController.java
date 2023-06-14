package backend.server.service.controller;

import backend.server.service.Service.FichierService;
import backend.server.service.domain.Fichier;
import backend.server.service.domain.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fichier")
@CrossOrigin(origins = "*", maxAge = 3600) // Allow requests from any origin for one hour
public class FileController {

    private FichierService fichierService;

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/add/{parentFolderId}")
    public void addFile(@RequestBody Fichier f,@PathVariable Long parentFolderId) {
        fichierService.addFichier(f, parentFolderId);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/delete")
    public void deleteFile(@RequestBody Long fileId) {
        fichierService.deleteFichier(fileId);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/rename/{fileId}")
    public void renameFile(@PathVariable Long fileId,@RequestBody String name) {
        fichierService.rename(fileId, name);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/update")
    public void updateFile(@RequestBody Fichier f) {
        fichierService.updateFichier(f);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/move/{fileId}")
    public void moveFile(@PathVariable Long fileId,@RequestBody Long targetFolderId) {
        fichierService.changerEmplacement(fileId, targetFolderId);
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
