package com.korigin.sip.entity;

import com.korigin.sip.entity.enums.Category;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "poem")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Poem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "poem_seq")
    @SequenceGenerator(name = "poem_seq", sequenceName = "poem_sequence", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Builder.Default
    private int views = 0;

    @Builder.Default
    private boolean anonymous = false;

    @OneToMany(mappedBy = "poem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "poem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PoemLike> likes = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void incrementViews() {
        this.views++;
    }
}
