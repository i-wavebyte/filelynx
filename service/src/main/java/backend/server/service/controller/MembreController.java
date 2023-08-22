package backend.server.service.controller;

import backend.server.service.Repository.*;
import backend.server.service.Service.*;
import backend.server.service.security.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/collaborateurs")
@CrossOrigin(origins = "*", maxAge = 3600) // Allow requests from any origin for one hour
@RequiredArgsConstructor
@Slf4j
public class MembreController {
    private final ICompagnieService compagnieService;
    private final CompagnieRepository compagnieRepository;
    private final PasswordEncoder encoder;

    private final RoleRepository roleRepository;
    private final IGroupeService groupeService;
    private final GroupeRepository groupeRepository;
    private final IMembreService membreService;
    private final LogRepository logRepository;
    private final CategorieRepository categorieRepository;
    private final LabelRepository labelRepository;
    private final ILogService logService;
    private final ICategorieService categorieService;
    private final ILabelService labelService;
    private final IDossierService dossierService;
    private final QuotaService quotaService;
    private final IAuthotisationService authotisationService;
}
