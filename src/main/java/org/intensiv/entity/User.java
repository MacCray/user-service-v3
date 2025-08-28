package org.intensiv.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
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
    private int age;

    @Basic(optional = false)
    private LocalDateTime createdAt;
}
