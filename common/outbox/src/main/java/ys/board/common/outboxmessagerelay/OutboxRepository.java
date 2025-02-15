package ys.board.common.outboxmessagerelay;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, Long> {
    // 이벤트가 아직 전송되지 않은 것들을 polling 해서 보내는데 사용
    List<Outbox> findAllByShardKeyAndCreatedAtLessThanEqualOrderByCreatedAtAsc(
            Long shardKey,
            LocalDateTime from,
            Pageable pageable
    );


}
