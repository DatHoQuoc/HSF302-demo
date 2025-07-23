package com.dat.book_network.user;

import com.dat.book_network.book.BookRepository;
import com.dat.book_network.file.FileStorageService;
import com.dat.book_network.history.BookTransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final FileStorageService fileStorageService;

    public UserProfileResponse getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserProfileResponse.builder()
                .id(user.getId().toString())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .dateOfBirth(user.getDateOfBirth())
                .imageUrl(user.getImageUrl())
                .build();
    }

    public void updateProfile(String firstName, String lastName, LocalDate dateOfBirth, MultipartFile file) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (firstName!= null) {
            user.setFirstName(firstName);
        }
        if (lastName != null) {
            user.setLastName(lastName);
        }
        if (dateOfBirth != null) {
            user.setDateOfBirth(dateOfBirth);
        }
        if(file != null && file.isEmpty()) {
            Integer userId = user.getId();
            boolean isPublic = true;
            String profilePictureUrl = fileStorageService.saveFile(file, userId, isPublic);
            if (profilePictureUrl != null) {
                user.setImageUrl(profilePictureUrl);
            } else {
                System.err.println("Failed to upload profile picture for user: " + email);
            }
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
