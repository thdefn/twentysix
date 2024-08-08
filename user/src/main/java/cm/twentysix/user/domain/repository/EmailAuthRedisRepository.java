package cm.twentysix.user.domain.repository;

import cm.twentysix.user.domain.model.EmailAuth;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailAuthRedisRepository extends CrudRepository<EmailAuth, String> {
}
