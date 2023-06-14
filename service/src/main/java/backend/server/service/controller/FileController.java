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
@RequestMapping("/api/v1/file")
@CrossOrigin(origins = "*", maxAge = 3600) // Allow requests from any origin for one hour
public class FileController {

    @Autowired
    private FichierService fichierService;

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/add")
    public void addFile(Fichier f, Long parentFolderId) {
        fichierService.addFichier(f, parentFolderId);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/delete")
    public void deleteFile(Long fileId) {
        fichierService.deleteFichier(fileId);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/rename")
    public void renameFile(Long fileId, String name) {
        fichierService.rename(fileId, name);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/update")
    public void updateFile(Fichier f) {
        fichierService.updateFichier(f);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/move")
    public void moveFile(Long fileId, Long targetFolderId) {
        fichierService.changerEmplacement(fileId, targetFolderId);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/admin/get")
    public Fichier getFile(Long fileId) {
        return fichierService.getFichier(fileId);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/admin/changeCategory")
    public void changeCategory(Long fileId, Long categoryId) {
        fichierService.changeCategory(fileId, categoryId);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/admin/changeLabel")
    public void changeLabel(Long fileId, List<Label> labels) {
        fichierService.updateLabels(fileId, labels);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/admin/getFromParent")
    public List<Fichier> getFilesFromParent(Long parentFolderId) {
        return fichierService.getFichiersByParent(parentFolderId);
    }

}
