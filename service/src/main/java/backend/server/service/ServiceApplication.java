package backend.server.service;

import backend.server.service.Service.CategorieService;
import backend.server.service.Service.DossierService;
import backend.server.service.Service.FichierService;
import backend.server.service.Service.LabelService;
import backend.server.service.domain.Categorie;
import backend.server.service.domain.Dossier;
import backend.server.service.domain.Fichier;
import backend.server.service.domain.Label;
import backend.server.service.enums.ETAT;
import backend.server.service.security.entities.EROLE;
import backend.server.service.security.entities.Role;
import backend.server.service.security.repositories.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

@SpringBootApplication @Slf4j
public class ServiceApplication {



    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    // this command line runner creates new roles and new users for testing
    @Bean
    CommandLineRunner run(RoleRepository roleRepository, DossierService dossierService, FichierService fichierService, LabelService labelService, CategorieService categorieService) {
        return args -> {
            try{
                roleRepository.save(new Role(null, EROLE.ROLE_USER));
                roleRepository.save(new Role(null, EROLE.ROLE_COMPAGNIE));
            }
            catch (Exception e){
                log.info("roles already created");
            }




            Dossier root = dossierService.addDossier(Dossier.builder().nom("root").build(),null);
            Dossier school = dossierService.addDossier(Dossier.builder().nom("school").build(),root.getId());
            Dossier games = dossierService.addDossier(Dossier.builder().nom("games").build(),root.getId());
            Dossier math = dossierService.addDossier(Dossier.builder().nom("math").build(),school.getId());
            Dossier geometry = dossierService.addDossier(Dossier.builder().nom("geometry").build(),math.getId());
            Dossier algebra = dossierService.addDossier(Dossier.builder().nom("algebra").build(),math.getId());
            school = dossierService.renameDossier(school.getId(),"mdrasa");
            log.info("current school name file named {}",school.getFullPath());
            Fichier pdf = fichierService.addFichier(Fichier.builder().nom("TP1").extension("pdf").type("Document").labels(new ArrayList<>()).build(), math.getId());
            dossierService.fileTree(root.getId(),1L);
            pdf.setTaille(2569874.);
            Label label = Label.builder().nom("à faire").build();
            Label label2 = Label.builder().nom("devoir").build();
            labelService.addLabel(label);
            labelService.addLabel(label2);
            Categorie categorie = Categorie.builder().nom("études").build();
            categorieService.addCategorie(categorie);
            pdf.getLabels().add(label);
            pdf.getLabels().add(label2);
            pdf.setCategorie(categorie);
            pdf = fichierService.updateFichier(pdf);
            pdf.setEtat(ETAT.ACCEPTED);
            log.info(pdf.toString());
        };
    }

}
