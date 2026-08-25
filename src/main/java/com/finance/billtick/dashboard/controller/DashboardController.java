package com.finance.billtick.dashboard.controller;

import com.finance.billtick.dashboard.dto.DashboardResponse;
import com.finance.billtick.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;


    @GetMapping()
    public ResponseEntity<DashboardResponse> getDashboard(@RequestParam Long businessId) {
        return ResponseEntity.ok(dashboardService.getDashboard(businessId));
    }
}
