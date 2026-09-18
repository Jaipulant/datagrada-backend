package com.datagrada.backend.repository;

import com.datagrada.backend.model.Competicion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompeticionRepository extends JpaRepository<Competicion, Integer> {
}