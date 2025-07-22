package com.dat.book_network.user;

import lombok.*;

/**
 * @author matve
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboardResponse {
    private Stat totalBooks;
    private Stat borrowedBooks;
    private Stat sharedBooks;
    private Stat returnedBooks;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stat {
        private int value;
    }
}
