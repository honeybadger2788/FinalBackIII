package com.dh.msbills.services;

import com.dh.msbills.models.Bill;
import com.dh.msbills.repositories.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository repository;

    public List<Bill> getAllBill() {
        return repository.findAll();
    }
    public List<Bill> getByUserId(String userId) {
        return repository.findByUserId(userId);
    }
    public Bill addBill(Bill bill) {
        return repository.save(bill);
    }
}
