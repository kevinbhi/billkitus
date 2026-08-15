package com.finance.billtick.business.mapper;

import com.finance.billtick.business.dto.BusinessPatchRequest;
import com.finance.billtick.business.dto.BusinessRequest;
import com.finance.billtick.business.dto.BusinessResponse;
import com.finance.billtick.business.model.Business;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BusinessMapper {

    @Mapping(target = "user", ignore = true)
    Business toBusiness(BusinessRequest businessRequest);

    @Mapping(source = "user.id", target = "userId")
    BusinessResponse toBusinessResponse(Business business);

    List<BusinessResponse> toBusinessResponseList(List<Business> businesses);

    @Mapping(target = "user", ignore = true)
    void updateBusiness(BusinessRequest businessRequest, @MappingTarget Business business);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    void patchBusiness(BusinessPatchRequest businessRequest, @MappingTarget Business business);
}
