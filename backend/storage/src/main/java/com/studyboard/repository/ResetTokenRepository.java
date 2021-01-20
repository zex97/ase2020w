package com.studyboard.repository;

import com.studyboard.model.PasswordResetToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {

    /**
     * Find a single password reset token.
     *
     * @param token for password reset
     * @return password reset token
     */
    PasswordResetToken findOneByToken(String token);

    /**
     * Find all tokens belonging to a user.
     *
     * @param userId id of user
     * @return list of password reset tokens
     */
    @Query(value = "SELECT * FROM password_reset_token p JOIN sb_user u ON p.sb_user_id=:userId", nativeQuery = true)
    List<PasswordResetToken> findAllByUserId(@Param("userId") long userId);
}
