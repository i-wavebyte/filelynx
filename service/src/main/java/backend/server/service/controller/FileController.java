package backend.server.service.controller;

import backend.server.service.Service.FichierService;
import backend.server.service.Service.IFichierService;
import backend.server.service.domain.Fichier;
import backend.server.service.domain.Label;
import backend.server.service.security.POJOs.responses.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fichier")
@CrossOrigin(origins = "*", maxAge = 3600) // Allow requests from any origin for one hour
public class FileController {

    private static final String path = "/Users/macbookpro/Desktop/files/upload/";
    private static final String pathd = "/Users/macbookpro/Desktop/files/download/";

    @Autowired
    private IFichierService fichierService;

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

    @PostMapping("/upload")
    public ResponseEntity<List<String>> fileUpload
            (@RequestParam("file") MultipartFile file)
            throws Exception {
        return new ResponseEntity<>(fichierService.uploadFile(file),
                HttpStatus.OK);

    }

    private HttpHeaders headers(String name) {

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + name);
        header.add("Cache-Control",
                "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return header;

    }
    @GetMapping(path = "/download/{name}")
    public ResponseEntity<ByteArrayResource> download(@PathVariable("name") String name) throws IOException {
        System.out.println("filename: "+name);
        // 1. Construct the File object representing the file to be downloaded
        File file = new File(path + name);

        // 2. Create a Path object from the File's absolute path
        Path filePath = Paths.get(file.getAbsolutePath());

        // 3. Read the file's content into a ByteArrayResource
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(filePath));
        System.out.println("siiiiiiiiiiiize: "+ResponseEntity.ok().contentLength(file.length()));

        BufferedOutputStream outputStream =
                new BufferedOutputStream(
                        new FileOutputStream(new File(pathd,
                                "tswira.png")));
        outputStream.write(resource.getByteArray());
        outputStream.flush();
        outputStream.close();
        // 4. Prepare and return the ResponseEntity with the file content
        return ResponseEntity
                .ok()
                .headers(this.headers(name)) // Add custom headers for the response
                .contentLength(file.length()) // Set the Content-Length header
                .contentType(MediaType.parseMediaType("application/octet-stream")) // Set the Content-Type header
                .body(resource); // Set the response body with the file content
    }



}
