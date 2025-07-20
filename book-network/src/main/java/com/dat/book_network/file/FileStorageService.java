package com.dat.book_network.file;

import com.dat.book_network.book.Book;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {
    private final SupabaseService supabaseService;

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
}
