package com.finance.billtick.business.controller;


import com.finance.billtick.business.model.Business;
import com.finance.billtick.business.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessRepository businessRepository;


    @PostMapping("/")
    public Business createBusiness(@RequestBody Business businessRequest) {
        return businessRepository.save(businessRequest);
    }

    @GetMapping(value = "/")
    public List<Business> getAllBusinesses() {
        return businessRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public Business getBusinessById(@PathVariable Long id) {
        return businessRepository.findById(id).orElse(null);
    }

    @PutMapping(value = "/{id}")
    public Business updateBusiness(@PathVariable Long id, @RequestBody Business businessRequest) {
        return businessRepository.save(businessRequest);
    }

    @DeleteMapping(value = "/")
    public void deleteBusiness(@RequestParam Long id) {
        businessRepository.deleteById(id);
    }

}
