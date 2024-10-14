package com.j1p3ter.queueserver.presentation.controller;

import com.j1p3ter.queueserver.application.client.ProductClient;
import com.j1p3ter.queueserver.application.dto.RankResponseDto;
import com.j1p3ter.queueserver.application.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/waitingRoom")
@Slf4j(topic = "WaitingRoom Controller")
public class WaitingRoomContoller {
    private final QueueService queueService;
    // rank 리턴
    @GetMapping("{productId}")
    public Mono<?> getRank(@RequestHeader(name = "X-USER-ID") Long userId,
                                 @PathVariable Long productId){
        return queueService.getRank(userId, productId)
                .map(rank -> new RankResponseDto(userId,rank));
    }
}
