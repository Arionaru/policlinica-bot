package com.example.botpoliclinica.repository;

import com.example.botpoliclinica.domain.SearchRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchRequestRepository extends JpaRepository<SearchRequest, Integer> {
    List<SearchRequest> findByChatId(Long chatId);
}
