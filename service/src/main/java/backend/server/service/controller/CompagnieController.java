package backend.server.service.controller;

import backend.server.service.Service.CompagnieService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/compagnie")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class CompagnieController {
    private final CompagnieService compagnieService;

    private final PasswordEncoder encoder;

}
