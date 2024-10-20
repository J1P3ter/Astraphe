package com.j1p3ter.queueserver.config;

import com.j1p3ter.common.exception.ApiException;
import com.j1p3ter.queueserver.application.client.ProductClient;
import com.j1p3ter.queueserver.application.dto.RankResponseDto;
import com.j1p3ter.queueserver.application.service.QueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Component
@Slf4j
public class QueueScheduler {
    private final ThreadPoolTaskScheduler taskScheduler;
    // 스케줄링 작업의 상태를 나타내는 목록
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new HashMap<>();
    private final QueueService queueService;
    private final ProductClient productClient;

    public QueueScheduler(QueueService queueService, ProductClient productClient) {
        this.queueService = queueService;
        this.productClient = productClient;
        this.taskScheduler = new ThreadPoolTaskScheduler();
        this.taskScheduler.initialize();
    }

    // 스케줄링 시 수행할 작업
    private Runnable getRunnable(Long productId,Long count) {
        return ()-> {
            queueService.allowUser(productId,count).subscribe();
            // waiting 목록에 값이 없으면 스케줄링 종료
            queueService.getWaitingUsers(productId)
                    .flatMap(waitingCount -> {
                        if (waitingCount == 0) {
                            stopTask(productId);
                        }
                        return Mono.empty();
                    }).subscribe();
        };
    }

    public void scheduleTask(Long productId, Long count, long delay) {
        // product에 지정된 startTime에 스케줄링 시작
        LocalDateTime startTime = productClient.getProduct(productId).getData().getSaleStartTime();

        // 테스트 시에는 아래 시간으로 확인 가능 (바로 위 주석 처리 후 아래 주석 풀기)
//        LocalDateTime startTime = LocalDateTime.now().plusMinutes(1);

        Runnable task = getRunnable(productId, count);

        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(task, triggerContext -> {
            if (triggerContext.lastCompletionTime() == null) { // 작업이 실행된 적 없을 경우
                log.info("product id " + productId + " / Scheduled to start at " + startTime);
                return startTime.atZone(ZoneId.systemDefault()).toInstant();
            } else {
                log.info("product id " + productId + " / rescheduled with delay of " + delay + " ms");
                return triggerContext.lastCompletionTime().toInstant().plusMillis(delay);
            }
        });
        scheduledTasks.put(productId, scheduledFuture); // 스케줄링 된 작업 저장
    }

    public void stopTask(Long productId) {
        ScheduledFuture<?> future = scheduledTasks.get(productId);
        if (future != null) {
            future.cancel(true);
            scheduledTasks.remove(productId); // 취소 후 제거
            log.info("Task for productId " + productId + " is stopped.");
        } else {
            throw new ApiException(HttpStatus.BAD_REQUEST, "해당 product의 Task가 존재하지 않습니다.", "No task found for productId " + productId);
        }
    }
}

