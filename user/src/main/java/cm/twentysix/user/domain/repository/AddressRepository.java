package cm.twentysix.user.domain.repository;

import cm.twentysix.user.domain.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);

    List<Address> findByUserIdOrderByIsDefaultDesc(Long userId);
}
