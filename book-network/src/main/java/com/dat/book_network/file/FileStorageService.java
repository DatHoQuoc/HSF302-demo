package com.dat.book_network.file;

import com.dat.book_network.book.Book;
import com.dat.book_network.book.BookContentUploadResult;
import com.dat.book_network.book.BookRepository;
import com.dat.book_network.history.BookTransactionHistoryRepository;
import jakarta.annotation.Nonnull;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {
    private final SupabaseService supabaseService;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
    private final BookRepository bookRepository;

    @Value("${supabase.private-bucket.name}")
    private String privateBucketName;

    public String saveFile(
            @Nonnull MultipartFile sourceFile,
            @Nonnull Integer userId,
            boolean isPublic) {

        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        final String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;

        final String pathWithinBucket = "users/" + userId + "/" + uniqueFileName;

        try {
            byte[] fileBytes = sourceFile.getBytes();
            String fileUrl = supabaseService.uploadFile(fileBytes, pathWithinBucket, isPublic);

            if (fileUrl != null) {
                log.info("File uploaded successfully to Supabase. URL: {}", fileUrl);
                return fileUrl;
            } else {
                log.warn("Failed to upload file to Supabase. No URL returned.");
                return null;
            }
        } catch (IOException e) {
            log.error("Failed to read file bytes from MultipartFile: {}", e.getMessage(), e);
            return null;
        } catch (RestClientException e) { // Catch exceptions from SupabaseService
            log.error("Supabase API error during file upload: {}", e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error("An unexpected error occurred during Supabase file upload: {}", e.getMessage(), e);
            return null;
        }
    }

    public byte[] downloadFile(String bucketName, String pathWithinBucket) {
        try {
            log.info("Attempting to download file from bucket: {} at path: {}", bucketName, pathWithinBucket);
            byte[] fileBytes = supabaseService.downloadFile(bucketName, pathWithinBucket);
            if (fileBytes != null && fileBytes.length > 0) {
                log.info("File downloaded successfully from Supabase.");
                return fileBytes;
            } else {
                log.warn("No bytes returned for download or file is empty.");
                return null;
            }
        } catch (RestClientException e) {
            log.error("Supabase API error during file download: {}", e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error("An unexpected error occurred during Supabase file download: {}", e.getMessage(), e);
            return null;
        }
    }

    public BookContentUploadResult uploadBookContent(
            @Nonnull MultipartFile pdfFile,
            @Nonnull Integer bookId,
            @Nonnull Integer uploadedByUserId) {

        // Validate file type
        if (!isPdfFile(pdfFile)) {
            log.error("File is not a PDF. Only PDF files are allowed for book content.");
            return null;
        }

        // Validate book exists
        if (!bookRepository.existsById(bookId)) {
            log.error("Book with ID {} does not exist.", bookId);
            return null;
        }

        final String fileExtension = getFileExtension(pdfFile.getOriginalFilename());
        final String uniqueFileName = "book_" + bookId + "_content_" + UUID.randomUUID().toString() + "." + fileExtension;

        // Store in books folder with private access
        final String pathWithinBucket = "books/content/" + bookId + "/" + uniqueFileName;

        try {
            byte[] fileBytes = pdfFile.getBytes();
            // Upload as private file (isPublic = false) - this will return a signed URL
            String signedUrl = supabaseService.uploadFile(fileBytes, pathWithinBucket, false);

            if (signedUrl != null) {
                log.info("Book content uploaded successfully for book ID: {}. Signed URL generated.", bookId);

                // Update book with content file path
                updateBookWithContentPath(bookId, pathWithinBucket);

                return new BookContentUploadResult(pathWithinBucket, signedUrl);
            } else {
                log.warn("Failed to upload book content for book ID: {}. No signed URL returned.", bookId);
                return null;
            }
        } catch (IOException e) {
            log.error("Failed to read PDF file bytes for book ID {}: {}", bookId, e.getMessage(), e);
            return null;
        } catch (RestClientException e) {
            log.error("Supabase API error during book content upload for book ID {}: {}", bookId, e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error("An unexpected error occurred during book content upload for book ID {}: {}", bookId, e.getMessage(), e);
            return null;
        }
    }

    public byte[] downloadBorrowedBookContent(
            @Nonnull Integer bookId,
            @Nonnull Integer userId) {

        try {
            // Check if user has currently borrowed this book (not returned)
            boolean hasBorrowedBook = bookTransactionHistoryRepository
                    .existsByBookIdAndUserIdAndReturnedFalse(bookId, userId);

            if (!hasBorrowedBook) {
                log.warn("User {} has not borrowed book {} or has already returned it. Download denied.", userId, bookId);
                return null;
            }

            // Get book content path
            String contentPath = getBookContentPath(bookId);

            if (contentPath == null) {
                log.warn("No content file found for book ID: {}", bookId);
                return null;
            }

            log.info("Attempting to download book content for user: {} and book: {}", userId, bookId);

            // Use private bucket name from SupabaseService
            // Since downloadFile method expects bucketName and pathWithinBucket separately,
            // we need to extract the private bucket name
            byte[] fileBytes = supabaseService.downloadFile(getPrivateBucketName(), contentPath);

            if (fileBytes != null && fileBytes.length > 0) {
                log.info("Book content downloaded successfully for user: {} and book: {}", userId, bookId);
                return fileBytes;
            } else {
                log.warn("No bytes returned for book content download or file is empty for book ID: {}", bookId);
                return null;
            }
        } catch (RestClientException e) {
            log.error("Supabase API error during book content download for book ID {}: {}", bookId, e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error("An unexpected error occurred during book content download for book ID {}: {}", bookId, e.getMessage(), e);
            return null;
        }
    }

    public String generateBookContentSignedUrl(
            @Nonnull Integer bookId,
            @Nonnull Integer userId,
            int expiresInSeconds) {

        try {
            // Check if user has currently borrowed this book
            boolean hasBorrowedBook = bookTransactionHistoryRepository
                    .existsByBookIdAndUserIdAndReturnedFalse(bookId, userId);

            if (!hasBorrowedBook) {
                log.warn("User {} has not borrowed book {} or has already returned it. Signed URL generation denied.", userId, bookId);
                return null;
            }

            // Get book content path
            String contentPath = getBookContentPath(bookId);

            if (contentPath == null) {
                log.warn("No content file found for book ID: {}", bookId);
                return null;
            }

            log.info("Generating signed URL for book {} and user: {}", bookId, userId);

            // Generate signed URL using private bucket
            String signedUrl = supabaseService.generateSignedUrl(
                    getPrivateBucketName(),
                    contentPath,
                    expiresInSeconds
            );

            if (signedUrl != null) {
                log.info("Signed URL generated successfully for book {} and user: {}", bookId, userId);
            }

            return signedUrl;

        } catch (Exception e) {
            log.error("Error generating signed URL for book ID {} and user ID {}: {}", bookId, userId, e.getMessage(), e);
            return null;
        }
    }

    private String getPrivateBucketName() {
        return privateBucketName;
    }
    private String getBookContentPath(Integer bookId) {
        try {
            Optional<Book> bookOptional = bookRepository.findById(bookId);
            if (bookOptional.isPresent()) {
                Book book = bookOptional.get();
                String contentPath = book.getContentFilePath();
                if (contentPath != null && !contentPath.isEmpty()) {
                    return contentPath;
                }
            }
            log.warn("No content file path found for book ID: {}", bookId);
            return null;
        } catch (Exception e) {
            log.error("Error getting content path for book ID {}: {}", bookId, e.getMessage(), e);
            return null;
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    private boolean isPdfFile(MultipartFile file) {
        if (file.getContentType() != null) {
            return file.getContentType().equals("application/pdf");
        }

        String fileName = file.getOriginalFilename();
        if (fileName != null) {
            return fileName.toLowerCase().endsWith(".pdf");
        }

        return false;
    }
    private void updateBookWithContentPath(Integer bookId, String contentPath) {
        try {
            Optional<Book> bookOptional = bookRepository.findById(bookId);
            if (bookOptional.isPresent()) {
                Book book = bookOptional.get();
                // Assuming you add contentFilePath field to Book entity
                book.setContentFilePath(contentPath);
                bookRepository.save(book);
                log.info("Updated book {} with content file path: {}", bookId, contentPath);
            }
        } catch (Exception e) {
            log.error("Failed to update book {} with content path: {}", bookId, e.getMessage(), e);
        }
    }

    public boolean canUserDownloadBook(@Nonnull Integer bookId, @Nonnull Integer userId) {
        try {
            return bookTransactionHistoryRepository
                    .existsByBookIdAndUserIdAndReturnedFalse(bookId, userId);
        } catch (Exception e) {
            log.error("Error checking if user {} can download book {}: {}", userId, bookId, e.getMessage(), e);
            return false;
        }
    }
}
