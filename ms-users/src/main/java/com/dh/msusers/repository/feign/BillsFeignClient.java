package com.dh.msusers.repository.feign;

import com.dh.msusers.config.feign.AccessTokenInterceptor;
import com.dh.msusers.model.Bill;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "ms-bills", url = "http://localhost:8081/", configuration = AccessTokenInterceptor.class)
public interface BillsFeignClient {

    @GetMapping("/bills/{userId}")
    List<Bill> getByUserId(@PathVariable String userId);

}
