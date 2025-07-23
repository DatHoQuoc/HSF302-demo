package com.dat.book_network.book;

import com.dat.book_network.common.PageResponse;
import com.dat.book_network.file.FileStorageService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Book")
public class BookController {

    private final BookService service;
    private final FileStorageService fileStorageService;
    private final BookRepository bookRepository;


    @PostMapping
    public ResponseEntity<Integer> saveBook(
            @Valid @RequestBody BookRequest request,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.save(request,connectedUser));
    }



    @GetMapping("{book-id}")
    public ResponseEntity<BookResponse> findBookById(
            @PathVariable("book-id") Integer bookId
    ){
        return ResponseEntity.ok(service.findById(bookId));
    }
    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) Boolean archived,
            @RequestParam(required = false) Boolean shareable,
            Authentication connectedUser
    ){
        BookSearchRequest request = new BookSearchRequest();
        request.setId(id);
        request.setKeyword(searchText);
        request.setArchived(archived);
        request.setShareable(shareable);
        return ResponseEntity.ok(service.findAllBooks(page,size, request, connectedUser));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) Boolean archived,
            @RequestParam(required = false) Boolean shareable,
            Authentication connectedUser
    ){
        BookSearchRequest request = new BookSearchRequest();
        request.setId(id);
        request.setKeyword(searchText);
        request.setArchived(archived);
        request.setShareable(shareable);
        return ResponseEntity.ok(service.findAllBooksByOwner(page, size, request, connectedUser));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @RequestParam(required = false) Integer bookId,
            @RequestParam(required = false, defaultValue = "") String keyword,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.findAllBorrowedBooks(page,size, bookId, keyword, connectedUser));
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @RequestParam(name = "id", required = false) Integer bookId,
            @RequestParam(name = "keyword", defaultValue = "",required = false) String keyword,
            @RequestParam(name = "returnApproved", required = false) Boolean returnApproved,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.findAllReturnedBooks(page, size, bookId, keyword, returnApproved, connectedUser));
    }

    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Integer> updateShareableStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.updateShareableStatus(bookId, connectedUser));
    }
    @PatchMapping("/archived/{book-id}")
    public ResponseEntity<Integer> updateArchivedStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.updateArchivedStatus(bookId, connectedUser));
    }

    @PostMapping(value = "/borrow/{book-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> borrowBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.borrowBook(bookId, connectedUser));
    }
    @PatchMapping("/borrow/return/{book-id}")
    public ResponseEntity<Integer> returnBorrowBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.returnBorrowedBook(bookId, connectedUser));
    }

    @PatchMapping("/borrow/return/approve/{book-id}")
    public ResponseEntity<Integer> approveReturnBorrowBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.approveReturnBorrowedBook(bookId, connectedUser));
    }
    @PostMapping(value = "/cover/{book-id}", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadBookCoverPicture(
            @PathVariable("book-id") Integer bookId,
            @Parameter
            @RequestPart("file") MultipartFile file,
            Authentication connectedUser
    ){
        service.uploadBookCoverPicture(file, connectedUser, bookId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/{bookId}/upload-content", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadBookContent(
            @PathVariable Integer bookId,
            @Parameter @RequestPart("file") MultipartFile file,
            @RequestParam("uploadedBy") Integer uploadedByUserId) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "File is empty"));
            }

            BookContentUploadResult result = fileStorageService.uploadBookContent(file, bookId, uploadedByUserId);

            if (result != null) {
                return ResponseEntity.ok(Map.of(
                        "message", "Book content uploaded successfully",
                        "filePath", result.getFilePath(),
                        "signedUrl", result.getSignedUrl(),
                        "bookId", bookId
                ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Failed to upload book content"));
            }

        } catch (Exception e) {
            log.error("Error uploading book content for book ID {}: {}", bookId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while uploading book content"));
        }
    }

    @GetMapping("/{bookId}/download-content")
    public ResponseEntity<?> downloadBookContent(
            @PathVariable Integer bookId,
            @RequestParam Integer userId) {

        try {
            byte[] fileBytes = fileStorageService.downloadBorrowedBookContent(bookId, userId);

            if (fileBytes != null) {
                // Get book details for filename
                Optional<Book> bookOptional = bookRepository.findById(bookId);
                String filename = "book_" + bookId + ".pdf";
                if (bookOptional.isPresent()) {
                    Book book = bookOptional.get();
                    String sanitizedTitle = book.getTitle().replaceAll("[^a-zA-Z0-9\\-_]", "_");
                    filename = sanitizedTitle + "_" + bookId + ".pdf";
                }

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .contentLength(fileBytes.length)
                        .body(fileBytes);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You don't have permission to download this book or the book content is not available"));
            }

        } catch (Exception e) {
            log.error("Error downloading book content for book ID {} and user ID {}: {}", bookId, userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while downloading book content"));
        }
    }

    @GetMapping("/{bookId}/signed-url")
    public ResponseEntity<?> generateBookContentSignedUrl(
            @PathVariable Integer bookId,
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "604800") int expiresInSeconds) { // Default 7 days

        try {
            String signedUrl = fileStorageService.generateBookContentSignedUrl(bookId, userId, expiresInSeconds);

            if (signedUrl != null) {
                return ResponseEntity.ok(Map.of(
                        "signedUrl", signedUrl,
                        "bookId", bookId,
                        "userId", userId,
                        "expiresInSeconds", expiresInSeconds
                ));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You don't have permission to access this book content"));
            }

        } catch (Exception e) {
            log.error("Error generating signed URL for book ID {} and user ID {}: {}", bookId, userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while generating signed URL"));
        }
    }

    @GetMapping("/{bookId}/can-download")
    public ResponseEntity<?> canDownloadBook(
            @PathVariable Integer bookId,
            @RequestParam Integer userId) {

        try {
            // This would use the same logic as in downloadBorrowedBookContent
            // but just return the permission status
            boolean canDownload = fileStorageService.canUserDownloadBook(bookId, userId);

            return ResponseEntity.ok(Map.of(
                    "canDownload", canDownload,
                    "bookId", bookId,
                    "userId", userId
            ));

        } catch (Exception e) {
            log.error("Error checking download permission for book ID {} and user ID {}: {}", bookId, userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while checking download permission"));
        }
    }

}
