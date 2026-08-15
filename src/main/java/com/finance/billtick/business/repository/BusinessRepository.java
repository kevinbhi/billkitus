package com.finance.billtick.business.repository;


import com.finance.billtick.business.model.Business;
import com.finance.billtick.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {
    List<Business> findByUser(User user);
}
