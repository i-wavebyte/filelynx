package backend.server.service.controller;

import backend.server.service.POJO.Quota;
import backend.server.service.Repository.LogRepository;
import backend.server.service.Service.CompagnieService;
import backend.server.service.Service.GroupeService;
import backend.server.service.Service.MembreService;
import backend.server.service.domain.Compagnie;
import backend.server.service.domain.Groupe;
import backend.server.service.domain.Log;
import backend.server.service.domain.Membre;
import backend.server.service.enums.LogType;
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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/compagnie")
@CrossOrigin(origins = "*", maxAge = 3600) // Allow requests from any origin for one hour
@RequiredArgsConstructor
public class CompagnieController {
    private final CompagnieService compagnieService;

    private final PasswordEncoder encoder;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final GroupeService groupeService;

    private final MembreService membreService;

    private final LogRepository logRepository;

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
        Log logMessage = Log.builder().message("Membre " + membre.getUsername() + " created and added to group " + membre.getGroup()).type(LogType.CREATE).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/ChangeMemberGroup/{username}/{group}")
    public ResponseEntity<?> changeMemberGroup(@PathVariable String username, @PathVariable String group) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Membre membre = membreService.getMembre(username);
        membre.setGroupe(groupeService.getGroupe(group,compagnieNom));
        membreService.updateMembre(membre);
        Log logMessage = Log.builder().message("Membre " + membre.getUsername() + " changed group from " + membre.getGroupe() + " to" + group).type(LogType.UPDATE).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);
        return ResponseEntity.ok(new MessageResponse("User group changed successfully to " + group));
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/createGroup/{group}")
    public ResponseEntity<?> createGroup(@PathVariable String group) {
        compagnieService.createGroupe(group, 1024.*1024.*1024.*5);
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Log logMessage = Log.builder().message("Group " + group + " created").type(LogType.CREATE).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);
        return ResponseEntity.ok(new MessageResponse("Group created successfully"));
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getQuotaStatus")
    public ResponseEntity<?> getQuotaStatus() {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Quota quota1 = new Quota();
        quota1.setQuota(compagnie.getQuota());
        quota1.setUsedQuota(compagnie.getUsedQuota());
        quota1.setQuotaLeft(compagnie.getQuota() - compagnie.getUsedQuota());
        return ResponseEntity.ok(quota1);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getCompagnieLogs")
    public ResponseEntity<?> getCompagnieLogs() {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        return ResponseEntity.ok(compagnie.getLogs());
    }
}
