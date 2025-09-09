package org.intensiv.userapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic(optional = false)
    private String name;

    @Basic(optional = false)
    @Column(unique = true)
    private String email;

    @Basic(optional = false)
    private Integer age;

    @Basic(optional = false)
    @Column(updatable = false)
    private LocalDateTime created_at;

    public User(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    @PrePersist
    private void onCreate() {
        created_at = LocalDateTime.now();
    }
}
