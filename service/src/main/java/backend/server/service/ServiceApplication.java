package backend.server.service;

import backend.server.service.security.entities.EROLE;
import backend.server.service.security.entities.Role;
import backend.server.service.security.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    // this command line runner creates new roles and new users for testing
    @Bean
    CommandLineRunner run(RoleRepository roleRepository) {
        return args -> {
            roleRepository.save(new Role(null, EROLE.ROLE_USER));
            roleRepository.save(new Role(null, EROLE.ROLE_MODERATOR));
            roleRepository.save(new Role(null, EROLE.ROLE_ADMIN));

        };
    }

}
