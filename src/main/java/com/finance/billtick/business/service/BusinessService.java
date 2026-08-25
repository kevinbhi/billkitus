package com.finance.billtick.business.service;

import com.finance.billtick.business.dto.BusinessPatchRequest;
import com.finance.billtick.business.dto.BusinessRequest;
import com.finance.billtick.business.dto.BusinessResponse;
import com.finance.billtick.business.mapper.BusinessMapper;
import com.finance.billtick.business.model.Business;
import com.finance.billtick.business.repository.BusinessRepository;
import com.finance.billtick.exception.ResourceNotFoundException;
import com.finance.billtick.user.model.User;
import com.finance.billtick.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final BusinessMapper businessMapper;

    @Transactional
    public BusinessResponse createBusiness(BusinessRequest businessRequest) {
        Business business = businessMapper.toBusiness(businessRequest);
        business.setUser(assertUser(businessRequest.getUserId()));
        return businessMapper.toBusinessResponse(businessRepository.save(business));
    }

    @Transactional(readOnly = true)
    public List<BusinessResponse> getAllBusinesses() {
        return businessMapper.toBusinessResponseList(businessRepository.findAll());
    }

    @Transactional
    public BusinessResponse updateBusiness(Long id, BusinessRequest businessRequest) {
        Business business = assertBusiness(id);
        businessMapper.updateBusiness(businessRequest, business);
        business.setUser(assertUser(businessRequest.getUserId()));
        return businessMapper.toBusinessResponse(businessRepository.save(business));
    }

    @Transactional
    public BusinessResponse patchBusiness(Long id, BusinessPatchRequest businessPatchRequest) {
        Business business = assertBusiness(id);
        businessMapper.patchBusiness(businessPatchRequest, business);
        return businessMapper.toBusinessResponse(businessRepository.save(business));
    }

    @Transactional
    public void deleteBusiness(Long id) {
        Business business = assertBusiness(id);
        business.setActive(false);
        businessRepository.save(business);
    }

    private Business assertBusiness(Long id) {
        return businessRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + id));
    }

    private User assertUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    @Transactional(readOnly = true)
    public List<BusinessResponse> getAllBusinessesForUser(Long userId) {
        return businessMapper.toBusinessResponseList(businessRepository.findByUser(assertUser(userId)));
    }
}
