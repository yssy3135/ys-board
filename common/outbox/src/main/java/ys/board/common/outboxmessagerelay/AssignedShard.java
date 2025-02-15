package ys.board.common.outboxmessagerelay;

import lombok.Getter;

import java.util.List;
import java.util.stream.LongStream;

@Getter
public class AssignedShard {
    private List<Long> shards;

    public static AssignedShard of(String appId, List<String> appIds, long shardCount) {
        AssignedShard assignedShard = new AssignedShard();
        assignedShard.shards  = assign(appId, appIds, shardCount);
        return assignedShard;
    }

    private static List<Long> assign(String appId, List<String> appIds, long shardCount) {
        int appIndex = findAppIndex(appId, appIds);

        // 할당할 샤드 없음
        if(appIndex == -1) {
            return List.of();
        }

        // 이 범위의 인덱스가 애플리케이션이 할당된 샤드
        long start = appIndex * shardCount / appIds.size();
        long end = (appIndex + 1 ) * shardCount / appIds.size() - 1;

        return LongStream.rangeClosed(start, end).boxed().toList();
    }

    private static int findAppIndex(String appId, List<String> appIds) {
        // appIds는 실행중인 appId들이 정렬된상태로들어온다
        // 현재 app이 몇번째인지 index반환
        for(int i = 0; i < appIds.size(); i++) {
            if(appIds.get(i).equals(appId)) {
                return i;
            }
        }
        return -1;
    }


}
