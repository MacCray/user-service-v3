package org.intensiv.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Basic(optional = false)
    private String name;

    @NonNull
    @Basic(optional = false)
    @Column(unique = true)
    private String email;

    @NonNull
    @Basic(optional = false)
    private Integer age;

    @Basic(optional = false)
    @Column(updatable = false)
    private LocalDateTime created_at;

    @PrePersist
    private void onCreate() {
        created_at = LocalDateTime.now();
    }
}
