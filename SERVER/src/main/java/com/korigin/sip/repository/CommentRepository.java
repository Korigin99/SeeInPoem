package com.korigin.sip.repository;

import com.korigin.sip.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPoemIdOrderByCreatedAtAsc(Long poemId);
}
