package com.codeflow.toolflow.persistence.user.repository;

import com.codeflow.toolflow.persistence.user.entity.User;
import com.codeflow.toolflow.persistence.user.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByToolflowUser(User user);

}
