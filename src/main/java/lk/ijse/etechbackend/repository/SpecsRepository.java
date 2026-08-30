package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.Product;
import lk.ijse.etechbackend.entity.Specs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpecsRepository extends JpaRepository<Specs, Long> {

    List<Specs> findByProduct(Product product);
}
