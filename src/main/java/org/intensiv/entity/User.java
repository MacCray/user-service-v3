package org.intensiv.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@RequiredArgsConstructor
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
    private LocalDateTime createdAt;
}
