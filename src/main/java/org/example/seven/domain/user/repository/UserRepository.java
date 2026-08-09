package org.example.seven.domain.user.repository;

import org.example.seven.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByName(String username);

    Optional<UserEntity> findByName(String username);
}
