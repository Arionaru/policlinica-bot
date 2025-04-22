package com.example.botpoliclinica.service;

import com.example.botpoliclinica.client.GorzdravFeignClient;
import com.example.botpoliclinica.converter.LpusConverter;
import com.example.botpoliclinica.domain.District;
import com.example.botpoliclinica.domain.Lpus;
import com.example.botpoliclinica.dto.LpusResponseDto;
import com.example.botpoliclinica.repository.DistrictRepository;
import com.example.botpoliclinica.repository.LpusRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LpusService {
    private final GorzdravFeignClient gorzdravFeignClient;
    private final DistrictRepository districtRepository;
    private final LpusRepository lpusRepository;
    private final LpusConverter lpusConverter;

    @PostConstruct
    public void setUp() {
        List<Lpus> topById = lpusRepository.findTopById(1);
        if (topById.isEmpty()) {
            updateLpus();
        }
    }

    public void updateLpus() {
        List<District> districts = districtRepository.findAll();
        districts.forEach(district -> {
            LpusResponseDto responseDto = gorzdravFeignClient.getLpusByDistrict(district.getId());
            Set<Lpus> newLpus = responseDto.getResult().stream()
                    .map(lpusDto -> lpusConverter.convert(lpusDto, district))
                    .collect(Collectors.toSet());
            Set<Lpus> allByDistrict = new HashSet<>(lpusRepository.findAllByDistrict_Id(district.getId()));
            if (!allByDistrict.equals(newLpus)) {
                lpusRepository.deleteByDistrict_Id(district.getId());
                lpusRepository.saveAll(newLpus);
                log.info(String.format("Сохранены поликлиники для %s района", district.getName()));
            }
        });
    }

    public List<Lpus> getLpusByDistrictId(Integer districtId) {
        return lpusRepository.findAllByDistrict_Id(districtId);
    }
}
