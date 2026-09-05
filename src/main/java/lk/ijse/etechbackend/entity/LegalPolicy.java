package lk.ijse.etechbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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


    @OneToMany(mappedBy = "legalPolicy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicySections> policySections;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void addPolicySection(PolicySections section) {
        if (policySections == null) {
            policySections = new ArrayList<>();
        }
        policySections.add(section);
        section.setLegalPolicy(this);
    }
}
