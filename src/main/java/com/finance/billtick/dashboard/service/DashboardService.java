package com.finance.billtick.dashboard.service;

import com.finance.billtick.business.model.Business;
import com.finance.billtick.business.repository.BusinessRepository;
import com.finance.billtick.dashboard.dto.AgingBucketResponse;
import com.finance.billtick.dashboard.dto.DashboardResponse;
import com.finance.billtick.exception.ResourceNotFoundException;
import com.finance.billtick.invoice.model.Invoice;
import com.finance.billtick.invoice.model.InvoiceStatus;
import com.finance.billtick.invoice.model.PaymentStatus;
import com.finance.billtick.invoice.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    // Upper bound (inclusive) of each overdue bucket, in days past the due date.
    private static final int[] BUCKET_BOUNDS = {30, 60, 90};

    private final InvoiceRepository invoiceRepository;
    private final BusinessRepository businessRepository;

    // Every metric is a function of columns already on the invoice row, so this is one query
    // plus arithmetic rather than a set of JPQL aggregates. findByBusiness is an entity query,
    // so @SQLRestriction excludes soft-deleted invoices for free.
    // Deliberately never touches invoice.getItems() -- doing so would N+1 across the whole
    // invoice history. See the scaling note in the plan before adding fields here.
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(Long businessId) {
        Business business = assertBusiness(businessId);
        LocalDate today = LocalDate.now();
        List<Invoice> invoices = invoiceRepository.findByBusiness(business);

        DashboardResponse response = new DashboardResponse();
        response.setBusinessId(businessId);
        response.setAsOfDate(today);

        BigDecimal totalInvoiced = BigDecimal.ZERO;
        BigDecimal totalCollected = BigDecimal.ZERO;
        BigDecimal totalOutstanding = BigDecimal.ZERO;
        BigDecimal overdueAmount = BigDecimal.ZERO;
        long overdueCount = 0;
        long draftCount = 0;
        long sentCount = 0;
        long voidCount = 0;
        long unpaidCount = 0;
        long partiallyPaidCount = 0;
        long paidCount = 0;

        Map<String, AgingBucketResponse> aging = newAgingBuckets();

        for (Invoice invoice : invoices) {
            if (invoice.getStatus() != InvoiceStatus.VOID) {
                totalCollected = totalCollected.add(invoice.getAmountPaid());
            }
            if (invoice.getStatus() == InvoiceStatus.SENT) {
                totalInvoiced = totalInvoiced.add(invoice.getTotal());
                totalOutstanding = totalOutstanding.add(invoice.getBalanceDue());
                if (invoice.getBalanceDue().signum() > 0) {
                    accumulate(aging.get(bucketFor(invoice, today)), invoice.getBalanceDue());
                }
            }
            if (invoice.isOverdue()) {
                overdueAmount = overdueAmount.add(invoice.getBalanceDue());
                overdueCount++;
            }
            switch (invoice.getStatus()) {
                case DRAFT -> draftCount++;
                case SENT -> sentCount++;
                case VOID -> voidCount++;
                default -> { }
            }
            switch (invoice.getPaymentStatus()) {
                case UNPAID -> unpaidCount++;
                case PARTIALLY_PAID -> partiallyPaidCount++;
                case PAID -> paidCount++;
            }
        }

        response.setTotalInvoiced(scale(totalInvoiced));
        response.setTotalCollected(scale(totalCollected));
        response.setTotalOutstanding(scale(totalOutstanding));
        response.setOverdueAmount(scale(overdueAmount));
        response.setOverdueCount(overdueCount);
        response.setDraftCount(draftCount);
        response.setSentCount(sentCount);
        response.setVoidCount(voidCount);
        response.setUnpaidCount(unpaidCount);
        response.setPartiallyPaidCount(partiallyPaidCount);
        response.setPaidCount(paidCount);
        response.setAging(new ArrayList<>(aging.values()));
        return response;
    }

    private Business assertBusiness(Long businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + businessId));
    }

    // CURRENT exists so the buckets sum to totalOutstanding; without it, balances that are
    // not yet due would vanish from the aging total.
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

    // Bucketed on the raw day difference rather than Invoice.getDaysOverdue(), which reports
    // zero for anything not yet overdue and so cannot distinguish CURRENT from due-today.
    private String bucketFor(Invoice invoice, LocalDate today) {
        long daysPastDue = ChronoUnit.DAYS.between(invoice.getDueDate(), today);
        if (daysPastDue <= 0) {
            return BUCKET_CURRENT;
        }
        int lower = 1;
        for (int bound : BUCKET_BOUNDS) {
            if (daysPastDue <= bound) {
                return lower + "-" + bound;
            }
            lower = bound + 1;
        }
        return BUCKET_OVER_90;
    }

    private void accumulate(AgingBucketResponse bucket, BigDecimal amount) {
        bucket.setAmount(bucket.getAmount().add(amount));
        bucket.setCount(bucket.getCount() + 1);
    }

    private BigDecimal scale(BigDecimal amount) {
        return amount.setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
    }

}
