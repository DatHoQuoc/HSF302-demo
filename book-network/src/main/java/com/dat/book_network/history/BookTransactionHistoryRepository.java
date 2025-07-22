package com.dat.book_network.history;

import com.dat.book_network.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> {

    @Query("""
        SELECT history
        FROM BookTransactionHistory history
        WHERE history.book.owner.id = :userId
          AND (:bookId IS NULL OR history.book.id = :bookId)
          AND (:keyword IS NULL OR
               LOWER(history.book.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(history.book.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(history.book.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
          AND (:returnApproved IS NULL OR history.returnedApproved = :returnApproved)
        """)
    Page<BookTransactionHistory> findAllReturnedBooks(
            Pageable pageable,
            @Param("userId") Integer userId,
            @Param("bookId") Integer bookId,
            @Param("keyword") String keyword,
            @Param("returnApproved") Boolean returnApproved
    );

    @Query("""
        SELECT history
                FROM BookTransactionHistory history
                        WHERE history.user.id = :userId
        """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);

    @Query("""
        SELECT history
                FROM BookTransactionHistory history
                        WHERE history.book.owner.id = :userId
                """)
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer userId);

    @Query("""
        SELECT (COUNT(*) > 0) AS isBorrowed
                FROM BookTransactionHistory history
                        WHERE history.user.id = :userId
                                AND history.book.id = :bookId
                                        AND history.returnedApproved = false
                """)
    boolean isAlreadyBorrowedByUser(Integer bookId, Integer userId);

    @Query("""
        SELECT transaction
                FROM BookTransactionHistory transaction
                        WHERE transaction.book.id = :bookId
                                AND transaction.user.id = :userId
                                        AND transaction.returnedApproved = false
                                                AND transaction.returned = false
                """)
    Optional<BookTransactionHistory> findByBookIdAndUserId(Integer bookId, Integer userId);

    @Query("""
        SELECT transaction
                FROM BookTransactionHistory transaction
                        WHERE transaction.book.id = :bookId
                                AND transaction.book.owner.id = :userId
                                        AND transaction.returnedApproved = false
                                                AND transaction.returned = true
                """)
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(Integer bookId, Integer userId);
}
