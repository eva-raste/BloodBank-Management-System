package com.project.BloodBank.Repository;

import com.project.BloodBank.Entities.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipientRepository extends JpaRepository<Recipient, Long> {
    Optional<Recipient> findByUsername(String username);
    Optional<Recipient> findById(Long id);
}