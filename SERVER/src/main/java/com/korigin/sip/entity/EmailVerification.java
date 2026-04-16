package com.korigin.sip.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_verifications")
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_ver_seq")
    @SequenceGenerator(name = "email_ver_seq", sequenceName = "email_verification_sequence", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
