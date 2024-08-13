package cm.twentysix.product.domain.repository;

import cm.twentysix.product.domain.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByOrderByIdDesc(Pageable pageable);
}
