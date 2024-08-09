package cm.twentysix.user.domain.repository;

import cm.twentysix.user.domain.model.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {
    void deleteByUserId(Long userId);
}
