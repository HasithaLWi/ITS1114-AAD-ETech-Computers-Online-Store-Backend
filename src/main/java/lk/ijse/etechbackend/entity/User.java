package lk.ijse.etechbackend.entity;


import jakarta.persistence.*;
import lk.ijse.etechbackend.enumiration.Status;
import lk.ijse.etechbackend.enumiration.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;
    private String name;
    private String username;
    private String emailAddress;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole userRoles;
    @Enumerated(EnumType.STRING)
    private Status status;
}