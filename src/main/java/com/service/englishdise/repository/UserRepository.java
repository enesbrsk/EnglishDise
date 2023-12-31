package com.service.englishdise.repository;

import com.service.englishdise.model.User;
import nonapi.io.github.classgraph.utils.VersionFinder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
}
