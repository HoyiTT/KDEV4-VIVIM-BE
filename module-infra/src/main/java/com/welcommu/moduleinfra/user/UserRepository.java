package com.welcommu.moduleinfra.user;

import com.welcommu.moduledomain.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    List<User> findByCompanyId(Long companyId);  // 회사 ID로 직원 목록 조회

    List<User> findByCompanyIdIn(List<Long> adminCompanyIds);
}
