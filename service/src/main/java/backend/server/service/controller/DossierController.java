package backend.server.service.controller;


import backend.server.service.Service.DossierService;
import backend.server.service.domain.Dossier;
import backend.server.service.domain.Fichier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dossier")
@CrossOrigin(origins = "*", maxAge = 3600) // Allow requests from any origin for one hour
public class DossierController {

    @Autowired
    private DossierService dossierService;

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/add/{parentFolderId}")
    public void addDossier(@RequestBody Dossier d,@PathVariable Long parentFolderId) {
        System.out.println("heeere  :  "+d +"\n");

        System.out.println("heeere  :  "+parentFolderId +"\n");
        dossierService.addDossier(d, parentFolderId);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/delete")
    public void deleteDossier(@RequestBody Long dossierId) {
        dossierService.delete(dossierId);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/rename/{dossierId}")
    public void renameDossier(@PathVariable Long dossierId,@RequestBody String name) {
        dossierService.renameDossier(dossierId, name);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/changerEmplacement/{dossierId}")
    public void changerEmplacement(@PathVariable Long dossierId,@RequestBody Long targetFolderId) {
        dossierService.changerEmplacement(dossierId, targetFolderId);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/admin/getChildrenFiles")
    public List<Dossier> getChildrenDossiers(@RequestBody Long dossierId) {
        return dossierService.getChildrenDossiers(dossierId);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/admin/get")
    public Dossier getDossier(@RequestBody Long dossierId) {
        return dossierService.getDossier(dossierId);
    }



}
