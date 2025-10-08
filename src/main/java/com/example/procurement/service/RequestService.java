package com.example.procurement.service;

import com.example.procurement.domain.Request;
import com.example.procurement.domain.RequestItem;
import com.example.procurement.domain.RequestStatus;
import com.example.procurement.repository.RequestRepository;
import com.example.procurement.repository.UserRepository;
import com.example.procurement.web.dto.CreateRequestDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class RequestService {

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("1000000");

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public RequestService(RequestRepository requestRepository, UserRepository userRepository, AuditService auditService) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional
    public Request create(CreateRequestDto dto, UUID actorId) {
        var applicant = userRepository.findById(UUID.fromString(dto.applicantId()))
                .orElseThrow(() -> new IllegalArgumentException("applicant not found"));
        if (dto.totalAmount().compareTo(MAX_AMOUNT) > 0) {
            throw new IllegalArgumentException("total Amount exceeds limit");
        }

        var req = new Request();
        req.setId(UUID.randomUUID());
        req.setApplicant(applicant);
        req.setStatus(RequestStatus.SUBMITTED);
        req.setTotalAmount(dto.totalAmount());
        req.setSubmittedAt(OffsetDateTime.now());

        for (var it : dto.items()) {
            var item = new RequestItem();
            item.setId(UUID.randomUUID());
            item.setSkuId(it.skuId());
            item.setQty(it.qty());
            item.setPrice(it.price());
            req.addItem(item);
        }

        var saved = requestRepository.save(req);

        auditService.audit(actorId, "CREATE", "requests", saved.getId().toString());

        return saved;
    }
}
