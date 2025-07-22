package com.dat.book_network.oauth2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {
    @GetMapping("/redirect")
    public ResponseEntity<?> oauth2Redirect(@RequestParam String token) {
        // This endpoint is optional — just returns a success message with a redirect path.
        Map<String, String> response = new HashMap<>();
        response.put("message", "OAuth2 login successful");
        response.put("token", token); // Echo back the token (if needed)
        response.put("redirectUrl", "/dashboard"); // This could be used by frontend for navigation

        return ResponseEntity.ok(response);
    }
}
