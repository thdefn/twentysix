package cm.twentysix.order.domain.repository;

import cm.twentysix.order.domain.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderId(String orderId);

    Slice<Order> findByUserId(Long userId, Pageable pageable);
}
