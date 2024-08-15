package cm.twentysix.brand.domain.repository;

import cm.twentysix.brand.domain.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    @Query(value = "select count(b) FROM Brand b WHERE b.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);

    List<Brand> findByIdIn(List<Long> brandIds);
}
