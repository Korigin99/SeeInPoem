package com.korigin.sip.repository;

import com.korigin.sip.entity.PoemLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PoemLikeRepository extends JpaRepository<PoemLike, Long> {
    boolean existsByUserIdAndPoemId(Long userId, Long poemId);
    Optional<PoemLike> findByUserIdAndPoemId(Long userId, Long poemId);
    long countByPoemId(Long poemId);
}
