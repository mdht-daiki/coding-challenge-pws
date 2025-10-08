package com.example.procurement.aop;

import com.example.procurement.service.AuditService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    private final AuditService auditService;

    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    @AfterReturning(pointcut = "within(@org.springframework.web.bind.annotation.RestController *)")
    public void afterController(JoinPoint jp) {
        // 必要に応じて、メソッド名・パス・引数を抽出して監査
        // 本番はSecurityContextからactorId取得
        // auditService.audit(actorId, action, targetTable, targetId);
    }
}
