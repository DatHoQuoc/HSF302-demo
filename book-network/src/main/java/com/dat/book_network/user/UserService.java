package com.dat.book_network.user;

import com.dat.book_network.book.BookRepository;
import com.dat.book_network.history.BookTransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author matve
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository historyRepository;


    public UserProfileResponse getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserProfileResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .dateOfBirth(user.getDateOfBirth())
                .imageUrl(user.getImageUrl())
                .build();
    }

    public void updateProfile(UpdateUserProfileRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getImageUrl() != null) {
            user.setImageUrl(request.getImageUrl());
        }

        userRepository.save(user);
    }

    public UserDashboardResponse getUserDashboard() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Total books owned by the user
        int totalBooks = bookRepository.countByOwner(user);

        // Books borrowed today (returned = false)
        int borrowedToday = historyRepository.countApprovedReturnedBooksByUser(user);

        // Books shared (shareable = true)
        int sharedToday = bookRepository.countByOwnerAndShareableTrue(user);

        // Books returned today (returned = true)
        int returnedToday = historyRepository.countReturnedBooksByUser(user);

        return UserDashboardResponse.builder()
                .totalBooks(stat(totalBooks))
                .borrowedBooks(stat(borrowedToday))
                .sharedBooks(stat(sharedToday))
                .returnedBooks(stat(returnedToday))
                .build();
    }

    private UserDashboardResponse.Stat stat(int value) {
        return new UserDashboardResponse.Stat(value);
    }
}
