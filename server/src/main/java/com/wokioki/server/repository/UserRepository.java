package com.wokioki.server.repository;

import com.wokioki.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmailIgnoreCase( String email);

    Optional<User> findByUsername( String username);

    boolean existsByEmailIgnoreCase( String email);

    boolean existsByUsername( String username);

}
