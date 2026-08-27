package lk.ijse.etechbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lk.ijse.etechbackend.converter.JsonObjectConverter;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "legal_policies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalPolicy {

    @Id
    @Column(name = "id", length = 50)
    private String id; // e.g. "privacy", "terms", "warranty"

    @Column(name = "title", length = 150, nullable = false)
    private String title;

    @Column(name = "subtitle", length = 255)
    private String subtitle;

    @Column(name = "last_updated", length = 50)
    private String lastUpdated;

    @Convert(converter = JsonObjectConverter.class)
    @Column(name = "sections_json", columnDefinition = "JSON", nullable = false)
    private Object sections;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
