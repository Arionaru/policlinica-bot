package com.example.botpoliclinica.repository;

import com.example.botpoliclinica.domain.Lpus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LpusRepository extends JpaRepository<Lpus, Integer> {
    List<Lpus> findAllByDistrict_Id(Integer districtId);

    void deleteByDistrict_Id(Integer id);

    List<Lpus> findTopById(Integer id);
}
