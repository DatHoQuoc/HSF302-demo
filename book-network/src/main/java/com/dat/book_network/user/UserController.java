package com.dat.book_network.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author matve
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        return ResponseEntity.ok(userService.getProfile());
    }

    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody UpdateUserProfileRequest request) {
        userService.updateProfile(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<UserDashboardResponse> getUserDashboard() {
        return ResponseEntity.ok(userService.getUserDashboard());
    }
}
