package com.dat.book_network.book;

import lombok.Getter;
import lombok.Setter;

/**
 * @author matve
 */
@Getter
@Setter

public class BookSearchRequest {
    private Integer id;
    private String keyword;
    private Boolean archived;
    private Boolean shareable;
}
