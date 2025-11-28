package com.ims.inventoryManagementSystem.repository;

import com.ims.inventoryManagementSystem.entity.ActiveSession;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActiveSessionRepository extends JpaRepository<ActiveSession,Long> {
    ActiveSession getActiveSessionByEmailAndSessionId (String email, String sessionId);

    @Transactional
    void deleteByEmail (String email);

    ActiveSession findByEmail (String email);

    ActiveSession getActiveSessionByEmail (String email);

    boolean findActiveSessionBySessionIdAndEmail (String token, String email);

//    ActiveSessionRepository findByToken (String token);

    Object findBySessionId (String token);
}
