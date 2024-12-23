package com.mars.NangPaGo.domain.user.entity;

import com.mars.NangPaGo.domain.user.enums.Gender;
import com.mars.NangPaGo.domain.user.enums.Provider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String nickname;
    private LocalDate birthDay;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String email;
    private String phoneNumber;
    private String profileImageUrl;
    private String role;

    @Enumerated(EnumType.STRING)
    private Provider provider;
    private String providerId;
}
