package lk.ijse.etechbackend.repository;


import lk.ijse.etechbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsernameAndPassword(String username, String password);

    Optional<User> findByUsername(String username);

    @Query(value = "SELECT u FROM User u WHERE u.name LIKE %:name%")
    List<User> findByName(String name);


}
