package com.marketplace.entity;

import com.marketplace.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data // Generates Getters, Setters, toString, equals, hashCode, and @RequiredArgsConstructor
@NoArgsConstructor // Generates a public no-args constructor
@AllArgsConstructor // Generates a constructor for all fields
@Builder // Enables the fluent builder pattern API
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password_hash;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @OneToMany(mappedBy = "owner")
    private List<Shop> shops = new ArrayList<>();

}
