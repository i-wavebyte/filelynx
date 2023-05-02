package backend.server.service.security.controllers;

import backend.server.service.Service.CompagnieService;
import backend.server.service.domain.Compagnie;
import backend.server.service.security.JwtUtils;
import backend.server.service.security.POJOs.requests.LoginRequest;
import backend.server.service.security.POJOs.requests.SignupRequest;
import backend.server.service.security.POJOs.requests.TokenRefreshRequest;
import backend.server.service.security.POJOs.responses.JwtResponse;
import backend.server.service.security.POJOs.responses.MessageResponse;
import backend.server.service.security.POJOs.responses.TokenRefreshResponse;
import backend.server.service.security.entities.EROLE;
import backend.server.service.security.entities.RefreshToken;
import backend.server.service.security.entities.Role;
import backend.server.service.security.entities.User;
import backend.server.service.security.exceptions.TokenRefreshException;
import backend.server.service.security.repositories.RoleRepository;
import backend.server.service.security.repositories.UserRepository;
import backend.server.service.security.services.RefreshTokenService;
import backend.server.service.security.services.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// This is a REST controller that handles user authentication and registration requests
//codegpt do : generate an importable minified json file to postman for this controller, the root is http://localhost:8080
@CrossOrigin(origins = "*", maxAge = 3600) // Allow requests from any origin for one hour
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    RefreshTokenService refreshTokenService;
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    CompagnieService compagnieService;

    /**
     * Authenticates a user and returns a JWT token if successful
     *
     * @param loginRequest the login request object containing the user's credentials
     * @return a JWT response object containing the token and user details
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
                userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    /**
     * Registers a new user account
     *
     * @param signUpRequest the sign-up request object containing the user's details
     * @return a message response object indicating the success or failure of the registration
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        // Check if username is already taken
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        // Check if email is already in use
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()));

        // Set user roles
        Set<Role> roles = new HashSet<>();
        Role CompagnieRole = roleRepository.findByName(EROLE.ROLE_COMPAGNIE).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(CompagnieRole);
        user.setRoles(roles);
        userRepository.save(user);

        // Create new compagnie
        Compagnie compagnie = new Compagnie();
        compagnie.setNom(signUpRequest.getUsername());
        compagnie.setQuota(1024.*1024.*1024.*50);
        compagnie = compagnieService.createCompagnie(compagnie);

        // Creating a default groupe for the compagnie
        compagnieService.createGroupe(compagnie.getNom(), 1024.*1024.*1024.*5, compagnie.getId());

        return ResponseEntity.ok(new MessageResponse("Compagnie registered successfully!"));
    }
}