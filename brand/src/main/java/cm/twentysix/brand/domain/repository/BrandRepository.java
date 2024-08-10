package cm.twentysix.brand.domain.repository;

import cm.twentysix.brand.domain.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    @Query(value = "select count(b) FROM Brand b WHERE b.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
}
