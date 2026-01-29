package com.dwalter.basketo.modules.identity.infrastructure.adapters;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
class AdminAuthController {

    private final SpringDataAdminUserRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        return adminRepository.findByEmail(request.email())
                .filter(admin -> passwordEncoder.matches(request.password(), admin.getPasswordHash()))
                .map(admin -> {
                    String token = jwtUtils.generateToken(admin.getEmail(), "ROLE_ADMIN");
                    return ResponseEntity.ok(new AdminLoginResponse(token));
                })
                .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request, HttpServletRequest httpRequest) {
        String role = (String) httpRequest.getAttribute("userRole");
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String email = (String) httpRequest.getAttribute("userEmail");
        
        return adminRepository.findByEmail(email)
                .map(admin -> {
                    if (!passwordEncoder.matches(request.oldPassword(), admin.getPasswordHash())) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).<Void>build();
                    }
                    admin.setPasswordHash(passwordEncoder.encode(request.newPassword()));
                    adminRepository.save(admin);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    record AdminLoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    record AdminLoginResponse(String accessToken) {}

    record ChangePasswordRequest(
            @NotBlank String oldPassword,
            @NotBlank @Size(min = 8) String newPassword
    ) {}
}
