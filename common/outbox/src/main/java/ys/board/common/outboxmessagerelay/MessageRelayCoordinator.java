package ys.board.common.outboxmessagerelay;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class MessageRelayCoordinator {

    private final StringRedisTemplate redisTemplate;

    @Value("${spring.application.name}")
    private String applicationName;

    private final String APP_ID = UUID.randomUUID().toString();

    // 3초 마다 작업 수행
    private final int PING_INTERVAL_SECONDS = 3;
    // 3번 연속 실패하면 죽은 서버로 판단
    private final int PING_FAILURE_THRESHOLD = 3;


    private String generateKey() {
        return "message-relay::coordinator::app-list::%s".formatted(applicationName);
    }

    public AssignedShard assignShards() {
        // redis 에서 app list 전체 조회
        return AssignedShard.of(APP_ID, findAppIds(), MessageRelayConstants.SHARD_COUNT);
    }

    private List<String> findAppIds() {
        // start - , end : -1 은 전체조회를 의미
        return Objects.requireNonNull(redisTemplate.opsForZSet().reverseRange(generateKey(), 0, -1)).stream()
                .sorted()
                .toList();
    }

    @Scheduled(fixedDelay = PING_INTERVAL_SECONDS, timeUnit = TimeUnit.SECONDS)
    public void ping() {

        //executePipelined 은 여러개의 명령을 한번에 실행할 수 있게 해준다.
        redisTemplate.executePipelined((RedisCallback<?>) action -> {
            StringRedisConnection conn = (StringRedisConnection) action;
            String key = generateKey();

            conn.zAdd(key, Instant.now().toEpochMilli(), APP_ID);

            // -inf ~ 현재시간 - 3 * 3초 이전의 데이터 삭제 ( 3번 연속 실패한 것으로 판단 )
            conn.zRemRangeByScore(
                    key,
                    Double.NEGATIVE_INFINITY,
                    Instant.now().minusSeconds(PING_INTERVAL_SECONDS * PING_FAILURE_THRESHOLD).toEpochMilli()
            );


            return null;
        });
    }

    @PreDestroy
    public void leave() {
        redisTemplate.opsForZSet().remove(generateKey(), APP_ID);
    }





}
