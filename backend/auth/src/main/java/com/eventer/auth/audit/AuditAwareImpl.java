package com.eventer.auth.audit;

import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditAwareImpl")
public class AuditAwareImpl implements AuditorAware<String> {
    /**
     * This method returns the current auditor for auditing purposes.
     * In this case, it returns a fixed string "AUTH_MS" to indicate that
     * the auditing is being done by the authentication microservice.
     *
     * @return An Optional containing the current auditor's identifier.
     */
    @NonNull
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("AUTH_MS");
    }
}
