package backend.server.service.controller;

import backend.server.service.Service.CompagnieService;
import backend.server.service.Service.GroupeService;
import backend.server.service.Service.MembreService;
import backend.server.service.domain.Compagnie;
import backend.server.service.domain.Membre;
import backend.server.service.payloads.RegisterUserRequest;
import backend.server.service.security.POJOs.responses.MessageResponse;
import backend.server.service.security.entities.EROLE;
import backend.server.service.security.entities.Role;
import backend.server.service.security.entities.User;
import backend.server.service.security.repositories.RoleRepository;
import backend.server.service.security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/compagnie")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class CompagnieController {
    private final CompagnieService compagnieService;

    private final PasswordEncoder encoder;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final GroupeService groupeService;

    private final MembreService membreService;

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/RegisterMembre")
    public ResponseEntity<?> addMembre(@RequestBody RegisterUserRequest membre) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);

        // Check if username is already taken
        if (userRepository.existsByUsername(membre.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        // Check if email is already in use
        if (userRepository.existsByEmail(membre.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }
        // Create new user's account
        User user = new User(membre.getUsername(), membre.getEmail(), encoder.encode(membre.getPassword()));

        // Set user roles
        Set<Role> roles = new HashSet<>();
        Role CompagnieRole = roleRepository.findByName(EROLE.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(CompagnieRole);
        user.setRoles(roles);
        userRepository.save(user);

        // Create new membre
        Membre newMembre = Membre.builder()
                .nom(membre.getNom())
                .prenom(membre.getPrenom())
                .email(membre.getEmail())
                .username(membre.getUsername())
                .groupe(groupeService.getGroupe(membre.getGroup(),compagnieNom))
                .build();

        membreService.addMembre(newMembre);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

}
