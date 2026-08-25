package com.finance.billtick.dashboard.service;

import com.finance.billtick.business.model.Business;
import com.finance.billtick.business.repository.BusinessRepository;
import com.finance.billtick.dashboard.dto.AgingBucketResponse;
import com.finance.billtick.dashboard.dto.DashboardResponse;
import com.finance.billtick.exception.ResourceNotFoundException;
import com.finance.billtick.invoice.model.InvoiceStatus;
import com.finance.billtick.invoice.model.PaymentStatus;
import com.finance.billtick.invoice.repository.AgingTotals;
import com.finance.billtick.invoice.repository.InvoiceRepository;
import com.finance.billtick.invoice.repository.InvoiceTotals;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int AMOUNT_SCALE = 2;
    private static final String BUCKET_CURRENT = "CURRENT";
    private static final String BUCKET_OVER_90 = "90+";
    private static final int[] BUCKET_BOUNDS = {30, 60, 90};

    private final InvoiceRepository invoiceRepository;
    private final BusinessRepository businessRepository;


    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(Long businessId) {
        Business business = assertBusiness(businessId);
        LocalDate today = LocalDate.now();

        InvoiceTotals totals = invoiceRepository.aggregateTotals(business, today,
                InvoiceStatus.DRAFT, InvoiceStatus.SENT, InvoiceStatus.VOID,
                PaymentStatus.UNPAID, PaymentStatus.PARTIALLY_PAID, PaymentStatus.PAID);

        DashboardResponse response = new DashboardResponse();
        response.setBusinessId(businessId);
        response.setAsOfDate(today);
        response.setTotalInvoiced(scale(totals.totalInvoiced()));
        response.setTotalCollected(scale(totals.totalCollected()));
        response.setTotalOutstanding(scale(totals.totalOutstanding()));
        response.setOverdueAmount(scale(totals.overdueAmount()));
        response.setOverdueCount(totals.overdueCount());
        response.setDraftCount(totals.draftCount());
        response.setSentCount(totals.sentCount());
        response.setVoidCount(totals.voidCount());
        response.setUnpaidCount(totals.unpaidCount());
        response.setPartiallyPaidCount(totals.partiallyPaidCount());
        response.setPaidCount(totals.paidCount());

        AgingTotals ageing = invoiceRepository.aggregateAging(business, InvoiceStatus.SENT,
                today, today.minusDays(BUCKET_BOUNDS[0]),
                today.minusDays(BUCKET_BOUNDS[1]), today.minusDays(BUCKET_BOUNDS[2]));

        Map<String, AgingBucketResponse> aging = newAgingBuckets();
        List<AgingBucketResponse> buckets = new ArrayList<>(aging.values());
        applyBucket(buckets.get(0), ageing.currentAmount(), ageing.currentCount());
        applyBucket(buckets.get(1), ageing.days1to30Amount(), ageing.days1to30Count());
        applyBucket(buckets.get(2), ageing.days31to60Amount(), ageing.days31to60Count());
        applyBucket(buckets.get(3), ageing.days61to90Amount(), ageing.days61to90Count());
        applyBucket(buckets.get(4), ageing.over90Amount(), ageing.over90Count());
        response.setAging(buckets);
        return response;
    }

    private Business assertBusiness(Long businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + businessId));
    }

    private Map<String, AgingBucketResponse> newAgingBuckets() {
        Map<String, AgingBucketResponse> buckets = new LinkedHashMap<>();
        buckets.put(BUCKET_CURRENT, newBucket(BUCKET_CURRENT));
        int lower = 1;
        for (int bound : BUCKET_BOUNDS) {
            String label = lower + "-" + bound;
            buckets.put(label, newBucket(label));
            lower = bound + 1;
        }
        buckets.put(BUCKET_OVER_90, newBucket(BUCKET_OVER_90));
        return buckets;
    }

    private AgingBucketResponse newBucket(String label) {
        AgingBucketResponse bucket = new AgingBucketResponse();
        bucket.setBucket(label);
        bucket.setAmount(BigDecimal.ZERO.setScale(AMOUNT_SCALE, RoundingMode.HALF_UP));
        bucket.setCount(0);
        return bucket;
    }

    private void applyBucket(AgingBucketResponse bucket, BigDecimal amount, long count) {
        bucket.setAmount(scale(amount));
        bucket.setCount(count);
    }

    private BigDecimal scale(BigDecimal amount) {
        return (amount == null ? BigDecimal.ZERO : amount).setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
    }

}
