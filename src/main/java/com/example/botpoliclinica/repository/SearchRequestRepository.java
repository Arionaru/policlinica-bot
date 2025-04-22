package com.example.botpoliclinica.repository;

import com.example.botpoliclinica.domain.SearchRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchRequestRepository extends JpaRepository<SearchRequest, Integer> {
}
