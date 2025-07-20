package com.dat.book_network.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.Map;

@Service
public class SupabaseService {
    private final RestClient restClient;
    private final String supabaseUrl;
    private final String supabaseServiceRoleKey;
    private final String supabasePublicBucketName;
    private final String supabasePrivateBucketName;

    public SupabaseService(RestClient.Builder restClientBuilder,
                           @Value("${supabase.url}") String supabaseUrl,
                           @Value("${supabase.service-role-key}") String supabaseServiceRoleKey,
                           @Value("${supabase.public-bucket.name}") String supabasePublicBucketName,
                           @Value("${supabase.private-bucket.name}") String supabasePrivateBucketName

    ) {
        this.supabaseUrl = supabaseUrl;
        this.supabaseServiceRoleKey = supabaseServiceRoleKey;
        this.supabasePublicBucketName = supabasePublicBucketName;
        this.supabasePrivateBucketName = supabasePrivateBucketName;
        // Base URL cho API Supabase Storage Object, không bao gồm bucket name
        this.restClient = restClientBuilder.baseUrl(supabaseUrl + "/storage/v1/object/")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseServiceRoleKey)
                .defaultHeader("apikey", supabaseServiceRoleKey)
                .build();
    }

    public String uploadFile(byte[] fileBytes, String pathWithinBucket, boolean isPublic) {
        String targetBucketName = isPublic ? supabasePublicBucketName : supabasePrivateBucketName;
        String fullApiUploadPath = targetBucketName + "/" + pathWithinBucket;


        restClient.post()
                .uri(fullApiUploadPath)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileBytes)
                .retrieve()
                // onStatus() không cần thiết ở đây vì defaultStatusHandler đã xử lý lỗi
                .toBodilessEntity(); // Supabase trả về body rỗng nếu thành công (200 OK)

        if (isPublic) {
            return supabaseUrl + "/storage/v1/object/public/" + fullApiUploadPath;
        } else {
            return generateSignedUrl(targetBucketName, pathWithinBucket, 604800);
        }
    }

    public byte[] downloadFile(String bucketName, String pathWithinBucket) {
        String fullDownloadPath = bucketName + "/" + pathWithinBucket;
        byte[] downloadedBytes = restClient.get()
                .uri(fullDownloadPath)
                .retrieve()
                // onStatus() không cần thiết ở đây
                .body(byte[].class);
        return downloadedBytes;
    }

    public String generateSignedUrl(String bucketName, String pathWithinBucket, int expiresInSeconds) {
        // URL đầy đủ đến endpoint tạo Signed URL của Supabase
        // Cần phải có /storage/v1/object/sign/ ở đây
        String fullApiSignedUrlEndpoint = supabaseUrl + "/storage/v1/object/sign/" + bucketName + "/" + pathWithinBucket;

        Map<String, Integer> body = Collections.singletonMap("expiresIn", expiresInSeconds);

        String rawResponse = null; // Khởi tạo để có thể truy cập ngoài khối try
        try {
            rawResponse = restClient.post()
                    .uri(fullApiSignedUrlEndpoint) // Sử dụng URI đầy đủ
                    .headers(headers -> headers.setBearerAuth(supabaseServiceRoleKey)) // Đảm bảo bạn gửi Authorization header
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);



            ObjectMapper objectMapper = new ObjectMapper();
            SignedUrlResponse response = objectMapper.readValue(rawResponse, SignedUrlResponse.class);

            if (response == null || response.getSignedUrl() == null || response.getSignedUrl().isEmpty()) {
                throw new RestClientException("Supabase did not return a valid Signed URL in the response body.");
            }

            // --- ĐIỂM QUAN TRỌNG NHẤT: Thêm tiền tố /storage/v1/ vào Signed URL ---
            // response.getSignedUrl() sẽ trả về chuỗi dạng "/object/sign/bucket-name/path..."
            // Bạn cần ghép nó với baseUrl + "/storage/v1"
            // base URl của bạn là: https://cabihztuvxlsaapmsxis.supabase.co
            return supabaseUrl + "/storage/v1" + response.getSignedUrl();

        } catch (RestClientException e) {
            System.err.println("Error calling Supabase API for Signed URL: " + e.getMessage());
            // In ra rawResponse nếu có lỗi HTTP
            if (rawResponse != null) {
                System.err.println("Supabase API Raw Response (on error): " + rawResponse);
            }
            throw e; // Ném lại ngoại lệ sau khi log
        } catch (Exception e) { // Bắt các lỗi parsing JSON hoặc lỗi khác
            System.err.println("Error processing Supabase response for Signed URL: " + e.getMessage());
            if (rawResponse != null) {
                System.err.println("Supabase API Raw Response (on error): " + rawResponse);
            }
            throw new RestClientException("Error processing Supabase response for Signed URL.", e);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class SignedUrlResponse {
        @JsonProperty("signedURL")
        private String signedUrl;
    }
}
