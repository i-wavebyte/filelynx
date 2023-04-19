package backend.server.service;

import backend.server.service.Service.DossierService;
import backend.server.service.Service.FichierService;
import backend.server.service.domain.Dossier;
import backend.server.service.domain.Fichier;
import backend.server.service.security.entities.EROLE;
import backend.server.service.security.entities.Role;
import backend.server.service.security.repositories.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication @Slf4j
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    // this command line runner creates new roles and new users for testing
    @Bean
    CommandLineRunner run(RoleRepository roleRepository, DossierService dossierService, FichierService fichierService) {
        return args -> {
            roleRepository.save(new Role(null, EROLE.ROLE_USER));
            roleRepository.save(new Role(null, EROLE.ROLE_MODERATOR));
            roleRepository.save(new Role(null, EROLE.ROLE_ADMIN));


            Dossier root = dossierService.addDossier(Dossier.builder().nom("root").build(),null);
            Dossier school = dossierService.addDossier(Dossier.builder().nom("school").build(),root.getId());
            Dossier games = dossierService.addDossier(Dossier.builder().nom("games").build(),root.getId());
            Dossier math = dossierService.addDossier(Dossier.builder().nom("math").build(),school.getId());
            Dossier geometry = dossierService.addDossier(Dossier.builder().nom("geometry").build(),math.getId());
            Dossier algebra = dossierService.addDossier(Dossier.builder().nom("algebra").build(),math.getId());
            school = dossierService.renameDossier(school.getId(),"mdrasa");
            log.info("current school name file named {}",school.getFullPath());
            Fichier pdf = fichierService.addFichier(Fichier.builder().nom("TP1").extension("pdf").build(), math.getId());
            dossierService.fileTree(root.getId(),1L);
        };
    }

}
