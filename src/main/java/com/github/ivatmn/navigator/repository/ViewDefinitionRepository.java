package com.github.ivatmn.navigator.repository;

import com.github.ivatmn.navigator.entity.ViewDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ViewDefinitionRepository extends JpaRepository<ViewDefinition, Long> {
    Optional<ViewDefinition> findByCode(String code);
}