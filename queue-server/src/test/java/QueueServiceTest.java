import com.j1p3ter.queueserver.EmbeddedRedis;
import com.j1p3ter.queueserver.QueueServerApplication;
import com.j1p3ter.queueserver.application.service.QueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;


@SpringBootTest(classes = QueueServerApplication.class)
@Import({EmbeddedRedis.class})
@ActiveProfiles("test")
class QueueServiceTest {

    @Autowired
    private QueueService userQueueService;

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @BeforeEach
    public void beforeEach() {
        ReactiveRedisConnection reactiveConnection = reactiveRedisTemplate.getConnectionFactory().getReactiveConnection();
        reactiveConnection.serverCommands().flushAll().subscribe();
    }

    // 하나의 product에 여러 사용자가 접속 시도할 때 rank 잘 리턴되는지 확인
    @Test
    void registerWaitQueue() {
        StepVerifier.create(userQueueService.registerWaitQueue(1L, 100L))
                .expectNext(1L)
                .verifyComplete();
        StepVerifier.create(userQueueService.registerWaitQueue(2L, 100L))
                .expectNext(2L)
                .verifyComplete();
        StepVerifier.create(userQueueService.registerWaitQueue(3L, 100L))
                .expectNext(3L)
                .verifyComplete();
    }

    // 이미 존재하는 사용자 추가 시 에러 메시지 잘 나오는지 확인
    @Test
    void alreadyRegisterWaitQueue() {
        StepVerifier.create(userQueueService.registerWaitQueue(1L, 100L))
                .expectNext(1L)
                .verifyComplete();
        StepVerifier.create(userQueueService.registerWaitQueue(1L, 100L))
                .expectError(Exception.class)
                .verify();
    }

    // 0명의 유저가 대기 중, 3명 진입 허용 시 0명 진입으로 나오는지 확인
    @Test
    void emptyAllowUser() {
        StepVerifier.create(userQueueService.allowUser(1L, 3L))
                .expectNext(0L)
                .verifyComplete();
    }

    // 3명의 유저가 대기 중, 2명 진입 허용 시 2명 진입으로 나오는지 확인
    @Test
    void allowUser() {
        StepVerifier.create(userQueueService.registerWaitQueue(1L, 100L)
                        .then(userQueueService.registerWaitQueue(2L, 100L))
                        .then(userQueueService.registerWaitQueue(3L, 100L))
                        .then(userQueueService.allowUser(100L, 2L)))
                .expectNext(2L)
                .verifyComplete();
    }

    // 3명의 유저가 대기 중, 5명 진입 허용 시 3명 진입으로 나오는지 확인
    @Test
    void allowUser2() {
        StepVerifier.create(userQueueService.registerWaitQueue(1L, 100L)
                        .then(userQueueService.registerWaitQueue(2L, 100L))
                        .then(userQueueService.registerWaitQueue(3L, 100L))
                        .then(userQueueService.allowUser(100L, 5L)))
                .expectNext(3L)
                .verifyComplete();
    }

    // user 3이 접근 허용 (wait Queue에서 삭제) 후 다시 대기 시도 > 1번으로 줄서게 된다.
    @Test
    void allowUserAfterRegisterWaitQueue() {
        StepVerifier.create(userQueueService.registerWaitQueue(1L, 100L)
                        .then(userQueueService.registerWaitQueue(2L, 100L))
                        .then(userQueueService.registerWaitQueue(3L, 100L))
                        .then(userQueueService.allowUser(100L, 3L)) // 대기 인원 모두 빠짐
                        .then(userQueueService.registerWaitQueue(3L, 100L)))
                .expectNext(1L)
                .verifyComplete();
    }

    // 대기열에 추가되지 않은 유저 접근 여부 확인 > false
    @Test
    void isNotAllowed() {
        StepVerifier.create(userQueueService.isAllowed(1L, 100L))
                .expectNext(false)
                .verifyComplete();
    }

    // product 100에서 접근 허용된 유저가 product 101에서는 접근되지 않은 것으로 나온다.
    @Test
    void isNotAllowed2() {
        StepVerifier.create(userQueueService.registerWaitQueue(1L, 100L)
                        .then(userQueueService.allowUser(100L, 1L))
                        .then(userQueueService.isAllowed(1L, 101L)))
                .expectNext(false)
                .verifyComplete();
    }

//    @Test
//    void isAllowed() {
//        StepVerifier.create(userQueueService.registerWaitQueue(1L, 100L)
//                        .then(userQueueService.allowUser(100L, 3L))
//                        .then(userQueueService.isAllowed(1L, 100L)))
//                .expectNext(true)
//                .verifyComplete();
//    }

    // 3번째로 줄을 선 유저는 rank가 3
    @Test
    void getRank() {
        StepVerifier.create(userQueueService.registerWaitQueue(1L, 100L)
                        .then(userQueueService.registerWaitQueue(2L, 100L))
                        .then(userQueueService.registerWaitQueue(3L, 100L))
                        .then(userQueueService.getRank(3L, 100L)))
                .expectNext(3L)
                .verifyComplete();
    }

    // 줄을 선 멤버가 아니면 rank가 -1
    @Test
    void getRank2() {
        StepVerifier.create(userQueueService.registerWaitQueue(1L, 100L)
                        .then(userQueueService.registerWaitQueue(2L, 100L))
                        .then(userQueueService.registerWaitQueue(3L, 100L))
                        .then(userQueueService.getRank(4L, 100L)))
                .expectNext(-1L)
                .verifyComplete();
    }

    // productId 별 대기 인원 조회
    @Test
    void getWaitingUsers() {
        StepVerifier.create(userQueueService.registerWaitQueue(1L, 100L)
                        .then(userQueueService.registerWaitQueue(2L, 100L))
                        .then(userQueueService.registerWaitQueue(3L, 100L))
                        .then(userQueueService.getWaitingUsers(100L)))
                .expectNext(3L)
                .verifyComplete();
    }

}
