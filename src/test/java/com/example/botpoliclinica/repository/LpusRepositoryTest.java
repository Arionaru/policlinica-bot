package com.example.botpoliclinica.repository;

import com.example.botpoliclinica.AbstractIntegrationTest;
import com.example.botpoliclinica.domain.District;
import com.example.botpoliclinica.domain.Lpus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class LpusRepositoryTest extends AbstractIntegrationTest {
    @Autowired
    LpusRepository lpusRepository;
    @Autowired
    DistrictRepository districtRepository;

    @Test
    void simpleSaveTest() {
        District district = districtRepository.findById(1).orElseThrow();
        Lpus lpus = Lpus.builder()
                .district(district)
                .id(555)
                .fullName("Тестовая поликлиника")
                .build();
        lpusRepository.save(lpus);
    }
}