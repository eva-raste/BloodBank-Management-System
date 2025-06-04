package com.project.BloodBank.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.BloodBank.Entities.Authority;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}