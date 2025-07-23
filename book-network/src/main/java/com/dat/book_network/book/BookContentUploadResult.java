package com.dat.book_network.book;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookContentUploadResult {
    private String filePath;
    private String signedUrl;
}
