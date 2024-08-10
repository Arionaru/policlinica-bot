package com.example.botpoliclinica.repository;

import com.example.botpoliclinica.AbstractIntegrationTest;
import com.example.botpoliclinica.domain.District;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DistrictRepositoryTest extends AbstractIntegrationTest {
    @Autowired
    DistrictRepository districtRepository;

    @Test
    public void simpleSaveTest() {
        District testDistrict = District.builder()
                .id(55)
                .name("testDistrict")
                .build();
        districtRepository.save(testDistrict);
        List<District> all = districtRepository.findAll();
        assertThat(all).hasSize(19);
    }
}