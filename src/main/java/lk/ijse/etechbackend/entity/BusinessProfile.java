package lk.ijse.etechbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "business_profile")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessProfile {

    @Id
    @Column(name = "id")
    @Builder.Default
    private Integer id = 1;

    @Column(name = "store_name", length = 150, nullable = false)
    private String storeName;

    @Column(name = "tagline", length = 255)
    private String tagline;

    @Column(name = "registration_no", length = 100, nullable = false)
    private String registrationNo;

    @Column(name = "tax_id", length = 100, nullable = false)
    private String taxId;

    @Column(name = "iso_cert", length = 150)
    private String isoCert;

    @Column(name = "support_email", length = 150, nullable = false)
    private String supportEmail;

    @Column(name = "hotline", length = 100, nullable = false)
    private String hotline;

    @Column(name = "headquarters", columnDefinition = "TEXT", nullable = false)
    private String headquarters;

    @Column(name = "working_hours", length = 255)
    private String workingHours;

    @Column(name = "mission_statement", columnDefinition = "TEXT")
    private String missionStatement;

    @Column(name = "company_story", columnDefinition = "TEXT")
    private String companyStory;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
