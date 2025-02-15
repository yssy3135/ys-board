package ys.board.common.outboxmessagerelay;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class AssignedShardTest {


    @Test
    public void ofTest() {

        int shardCount = 64 ;
        List<String> appList = List.of("appId1", "appId2", "appId3");

        AssignedShard assignedShard1 = AssignedShard.of("appId1", appList, shardCount);
        AssignedShard assignedShard2 = AssignedShard.of("appId2", appList, shardCount);
        AssignedShard assignedShard3 = AssignedShard.of("appId3", appList, shardCount);
        AssignedShard assignedShard4 = AssignedShard.of("appId4", appList, shardCount);

        List<Long> result = Stream.of(
                        assignedShard1.getShards(),
                        assignedShard2.getShards(),
                        assignedShard3.getShards(),
                        assignedShard4.getShards()
                )
                .flatMap(List::stream)
                .toList();

        assertThat(result.size()).isEqualTo(shardCount);


        for (int i = 0; i < shardCount; i++) {
            assertThat(result.get(i)).isEqualTo(i);
        }

        assertThat(assignedShard4.getShards()).isEmpty();
    }

}
