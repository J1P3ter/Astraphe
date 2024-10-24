package com.j1p3ter.productserver.application.client;

import com.j1p3ter.productserver.application.dto.scheduler.StartAllowRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "queue-server")
public interface QueueSchedulerClient {
    @PostMapping("/api/schedulingQueue/{productId}/startAllow")
    String startScheduling(@RequestHeader(name = "X-USER-ID") Long userId,
                           @PathVariable Long productId, @RequestBody StartAllowRequestDto requestDto);
}