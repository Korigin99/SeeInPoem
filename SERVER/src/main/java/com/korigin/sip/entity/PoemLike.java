package com.korigin.sip.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "poem_like", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "poem_id"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PoemLike {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "poem_like_seq")
    @SequenceGenerator(name = "poem_like_seq", sequenceName = "poem_like_sequence", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poem_id", nullable = false)
    private Poem poem;
}
