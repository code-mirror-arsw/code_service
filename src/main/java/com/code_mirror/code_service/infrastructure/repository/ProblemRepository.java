package com.code_mirror.code_service.infrastructure.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemRepository extends CrudRepository<CodingProblem, String> {
}