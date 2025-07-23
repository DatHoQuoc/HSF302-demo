package com.dat.book_network.user;

import lombok.*;

import java.time.LocalDate;

/**
 * @author matve
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dateOfBirth;
    private String imageUrl;
}
