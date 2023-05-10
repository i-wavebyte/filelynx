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
        };
    }

}
