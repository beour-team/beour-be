package com.beour.user.repository;

import com.beour.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

  Boolean existsByLoginId(String loginId);
  Boolean existsByNickname(String nickname);

  Optional<User> findByLoginId(String loginId);
  Optional<User> findByLoginIdAndDeletedAtIsNull(String loginId);
  Optional<User> findByNickname(String loginId);

  Optional<User> findByNameAndPhoneAndEmail(String name, String phone, String email);

  @Modifying
  @Query("UPDATE User u SET u.password = :password, u.updatedAt = CURRENT_TIMESTAMP WHERE u.loginId = :loginId")
  void updatePasswordByLoginId(@Param("loginId") String loginId,
      @Param("password") String password);

}
