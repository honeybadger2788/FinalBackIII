package com.dh.msusers.repository.feign;

import com.dh.msusers.model.Bill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BillsFeignRepository {

    private final BillsFeignClient feignClient;

    public List<Bill> getByUserId(String id){
        return feignClient.getByUserId(id);
    }

}
