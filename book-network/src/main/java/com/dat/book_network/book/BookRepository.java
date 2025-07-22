package com.dat.book_network.book;

import com.dat.book_network.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {


    @Query("""
    SELECT COUNT(b)
    FROM Book b
    WHERE b.owner = :user
      AND b.shareable = true
""")
    int countByOwnerAndShareableTrue(@Param("user") User user);

    @Query("""
    SELECT COUNT(b)
    FROM Book b
    WHERE b.owner = :user
""")
    int countByOwner(@Param("user") User user);
}
