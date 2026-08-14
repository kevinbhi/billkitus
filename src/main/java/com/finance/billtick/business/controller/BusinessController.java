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
    public Business createBusiness(@RequestBody Business businessrequest) {
        Business business = new Business();
        business.setBusinessName(businessrequest.getBusinessName());
        business.setCity(businessrequest.getCity());
        business.setState(businessrequest.getState());
        business.setZipCode(businessrequest.getZipCode());
        business.setInvoicePrefix(businessrequest.getInvoicePrefix());
        business.setDefaultTerms(businessrequest.getDefaultTerms());
        business.setSalesTaxRate(businessrequest.getSalesTaxRate());
        business.setLogo(businessrequest.getLogo());
        businessRepository.save(business);
        return business;
    }

    @GetMapping(value = "/")
    public List<Business> getAllBusinesses() {
        return businessRepository.findAll();
    }

    @GetMapping(value = "/{Id}")
    public Business getBusinessById(@PathVariable Integer Id) {
        return businessRepository.findById(Long.valueOf(Id))
                .orElseThrow(() -> new RuntimeException("Business not found"));
    }

    @PutMapping(value = "/{Id}")
    public Business updateBusiness(@PathVariable Integer Id, @RequestBody Business businessrequest) {
        Business business = businessRepository.findById(Long.valueOf(Id))
                .orElseThrow(() -> new RuntimeException("Business not found"));
        business.setBusinessName(businessrequest.getBusinessName());
        business.setCity(businessrequest.getCity());
        business.setState(businessrequest.getState());
        business.setZipCode(businessrequest.getZipCode());
        business.setInvoicePrefix(businessrequest.getInvoicePrefix());
        business.setDefaultTerms(businessrequest.getDefaultTerms());
        business.setSalesTaxRate(businessrequest.getSalesTaxRate());
        business.setLogo(businessrequest.getLogo());
        businessRepository.save(business);
        return business;
    }

    @DeleteMapping(value = "/{Id}")
    public void deleteBusiness(@PathVariable Integer Id) {
        businessRepository.deleteById(Long.valueOf(Id));
    }
}