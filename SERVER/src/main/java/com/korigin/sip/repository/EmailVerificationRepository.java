package com.korigin.sip.repository;

import com.korigin.sip.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findTopByEmailOrderByExpiresAtDesc(String email);

    @Modifying
    @Query("DELETE FROM EmailVerification ev WHERE ev.email = :email")
    void deleteByEmail(String email);
}
