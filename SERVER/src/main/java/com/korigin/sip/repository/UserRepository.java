package com.korigin.sip.repository;

import com.korigin.sip.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findBySocialId(String socialId);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
