package com.dat.book_network.book;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;


public class BookSpecification {
    public static Specification<Book>  withOwnerId(Integer ownerId) {
        return (root, query, cb) -> cb.equal(root.get("owner").get("id"), ownerId);
    }

    public static Specification<Book> withoutOwnerId(Integer ownerId) {
        return (root, query, cb) -> cb.notEqual(root.get("owner").get("id"), ownerId);
    }

    public static Specification<Book> build(BookSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

//            predicates.add(cb.equal(root.get("archived"), false));
//            predicates.add(cb.equal(root.get("shareable"), true));

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            // Lọc theo keyword (áp dụng cho title, author, isbn, synopsis)
            if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
                String keyword = "%" + request.getKeyword().toLowerCase() + "%";
                Predicate titlePredicate = cb.like(cb.lower(root.get("title")), keyword);
                Predicate authorPredicate = cb.like(cb.lower(root.get("author")), keyword);
                Predicate isbnPredicate = cb.like(cb.lower(root.get("isbn")), keyword);
                Predicate synopsisPredicate = cb.like(cb.lower(root.get("synopsis")), keyword);
                predicates.add(cb.or(titlePredicate, authorPredicate, isbnPredicate, synopsisPredicate));
            }

            if (request.getArchived() != null) {
                predicates.add(cb.equal(root.get("archived"), request.getArchived()));
            }

            if (request.getShareable() != null) {
                predicates.add(cb.equal(root.get("shareable"), request.getShareable()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Book> buildAndExcludeOwner(BookSearchRequest request, Integer ownerIdToExclude) {
        Specification<Book> baseSpec = build(request);

        if (ownerIdToExclude != null) {
            return baseSpec.and(withoutOwnerId(ownerIdToExclude));
        }
        return baseSpec;
    }

}
