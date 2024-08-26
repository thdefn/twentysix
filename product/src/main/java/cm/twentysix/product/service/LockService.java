package cm.twentysix.product.service;

import cm.twentysix.product.constant.LockDomain;
import cm.twentysix.product.exception.LockAcquisitionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
@Slf4j
public class LockService {
    private final RedissonClient redissonClient;

    public void lock(LockDomain domain, String key) throws Exception {
        String lockKey = getLockKey(domain, key);
        RLock lock = redissonClient.getLock(lockKey);
        log.debug("trying lock for lock key : " + lockKey);
        boolean acquired = lock.tryLock(domain.waitSecond, domain.leaseSecond, TimeUnit.SECONDS);
        if (!acquired) {
            throw new LockAcquisitionException("lock acquisition failed for lock key : " + lockKey);
        }
    }

    private String getLockKey(LockDomain domain, String key) {
        return domain.name() + ":" + key;
    }

    public void unlock(LockDomain domain, String key) {
        String lockKey = getLockKey(domain, key);
        log.debug("unlock for lock key : " + lockKey);
        redissonClient.getLock(lockKey).unlock();
    }
}
