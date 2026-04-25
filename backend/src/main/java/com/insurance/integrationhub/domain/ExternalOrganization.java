package com.insurance.integrationhub.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "organizations",
        indexes = {
                @Index(name = "idx_organization_name", columnList = "name")
        }
)
public class ExternalOrganization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String managerName;

    private String managerEmail;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public ExternalOrganization(String name, String managerName, String managerEmail) {
        this.name = name;
        this.managerName = managerName;
        this.managerEmail = managerEmail;
        this.createdAt = LocalDateTime.now();
    }
}