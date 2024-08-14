package cm.twentysix.product.domain.repository;

import cm.twentysix.product.domain.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByIsDeletedFalseOrderByIdDesc(Pageable pageable);
    Optional<Product> findByIdAndIsDeletedFalse(String productId);
    List<Product> findByIdInAndIsDeletedFalse(Set<String> productIds);
}
