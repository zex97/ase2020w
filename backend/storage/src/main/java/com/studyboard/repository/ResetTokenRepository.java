package com.studyboard.repository;

import com.studyboard.model.PasswordResetToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {

    /**
     * Find a single password reset token.
     *
     * @param token for password reset
     * @return password reset token
     */
    PasswordResetToken findOneByToken(String token);
}
