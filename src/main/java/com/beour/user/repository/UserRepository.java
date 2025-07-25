package com.beour.user.repository;

import com.beour.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByLoginIdAndDeletedAtIsNull(String loginId);
    Boolean existsByNicknameAndDeletedAtIsNull(String nickName);

    Optional<User> findByLoginId(String loginId);
    Optional<User> findByLoginIdAndDeletedAtIsNull(String loginId);

    Optional<User> findByNameAndPhoneAndEmailAndDeletedAtIsNull(String name, String phone, String email);
    Optional<User> findByLoginIdAndNameAndPhoneAndEmailAndDeletedAtIsNull(String loginId, String name, String phone, String email);

    @Modifying
    @Query("UPDATE User u SET u.password = :password, u.updatedAt = CURRENT_TIMESTAMP WHERE u.loginId = :loginId")
    void updatePasswordByLoginId(@Param("loginId") String loginId,
        @Param("password") String password);

}
