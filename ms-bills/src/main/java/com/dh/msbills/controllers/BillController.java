package com.dh.msbills.controllers;

import com.dh.msbills.models.Bill;
import com.dh.msbills.services.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
@EnableDiscoveryClient
public class BillController {

    private final BillService service;

    @GetMapping("/all")
    public ResponseEntity<List<Bill>> getAll() {
        return ResponseEntity.ok().body(service.getAllBill());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Bill>> getByUserId(@PathVariable String userId) {
        return ResponseEntity.ok().body(service.getByUserId(userId));
    }

    @PostMapping("/create")
    public ResponseEntity<Bill> addBill(@RequestBody Bill bill) {
        return ResponseEntity.ok().body(service.addBill(bill));
    }

}
