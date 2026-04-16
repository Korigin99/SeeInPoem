package com.korigin.sip.repository;

import com.korigin.sip.entity.Poem;
import com.korigin.sip.entity.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PoemRepository extends JpaRepository<Poem, Long> {
    Page<Poem> findAll(Pageable pageable);
    Page<Poem> findByCategory(Category category, Pageable pageable);

    @Query("SELECT p FROM Poem p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    Page<Poem> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<Poem> findByAuthorId(Long authorId, Pageable pageable);
}
