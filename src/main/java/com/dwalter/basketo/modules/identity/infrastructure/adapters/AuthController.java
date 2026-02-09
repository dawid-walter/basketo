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
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest request) {
        identityService.requestLoginPin(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/request-pin-by-order")
    public ResponseEntity<Void> requestPinByOrder(@RequestBody RequestPinByOrderRequest request) {
        identityService.requestLoginPinByOrderNumber(request.orderNumber());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyResponse> verify(@RequestBody VerifyRequest request) {
        boolean isValid = identityService.verifyPin(request.email(), request.pin());
        if (isValid) {
            String token = jwtUtils.generateToken(request.email());
            return ResponseEntity.ok(new VerifyResponse(true, token));
        }
        return ResponseEntity.status(401).body(new VerifyResponse(false, null));
    }

    @PostMapping("/verify-by-order")
    public ResponseEntity<VerifyResponse> verifyByOrder(@RequestBody VerifyByOrderRequest request) {
        var result = identityService.verifyPinByOrderNumber(request.orderNumber(), request.pin());
        if (result.isValid()) {
            String token = jwtUtils.generateToken(result.email());
            return ResponseEntity.ok(new VerifyResponse(true, token));
        }
        return ResponseEntity.status(401).body(new VerifyResponse(false, null));
    }

    record LoginRequest(String email) {}
    record RequestPinByOrderRequest(String orderNumber) {}
    record VerifyRequest(String email, String pin) {}
    record VerifyByOrderRequest(String orderNumber, String pin) {}
    record VerifyResponse(boolean success, String accessToken) {}
}
