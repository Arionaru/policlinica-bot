package com.example.botpoliclinica.client;

import com.example.botpoliclinica.dto.LpusResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "gorzdrav", url = "https://gorzdrav.spb.ru/_api/api/v2")
public interface GorzdravFeignClient {

    @GetMapping("/shared/district/{id}/lpus")
    LpusResponseDto getLpusByDistrict(@PathVariable("id") Integer id);

}
