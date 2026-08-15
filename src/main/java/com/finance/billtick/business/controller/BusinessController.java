package com.finance.billtick.business.controller;

import com.finance.billtick.business.dto.BusinessPatchRequest;
import com.finance.billtick.business.dto.BusinessRequest;
import com.finance.billtick.business.dto.BusinessResponse;
import com.finance.billtick.business.service.BusinessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    @PostMapping()
    public ResponseEntity<BusinessResponse> createBusiness(@Valid @RequestBody BusinessRequest businessRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(businessService.createBusiness(businessRequest));
    }

    @GetMapping()
    public ResponseEntity<List<BusinessResponse>> getAllBusinesses() {
        return ResponseEntity.ok(businessService.getAllBusinesses());
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<BusinessResponse> updateBusiness(@PathVariable Long id, @Valid @RequestBody BusinessRequest businessRequest) {
        return ResponseEntity.ok(businessService.updateBusiness(id, businessRequest));
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<BusinessResponse> patchBusiness(@PathVariable Long id, @Valid @RequestBody BusinessPatchRequest businessPatchRequest) {
        return ResponseEntity.ok(businessService.patchBusiness(id, businessPatchRequest));
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteBusiness(@RequestParam Long id) {
        businessService.deleteBusiness(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
