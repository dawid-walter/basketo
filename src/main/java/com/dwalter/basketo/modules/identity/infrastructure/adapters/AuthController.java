package com.dwalter.basketo.modules.identity.infrastructure.adapters;

import com.dwalter.basketo.modules.identity.application.IdentityApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
class AuthController {

    private final IdentityApplicationService identityService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest request) {
        identityService.requestLoginPin(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyResponse> verify(@RequestBody VerifyRequest request) {
        boolean isValid = identityService.verifyPin(request.email(), request.pin());
        return ResponseEntity.ok(new VerifyResponse(isValid));
    }

    record LoginRequest(String email) {}
    record VerifyRequest(String email, String pin) {}
    record VerifyResponse(boolean success) {}
}
