package com.example.botpoliclinica.converter;

import com.example.botpoliclinica.domain.District;
import com.example.botpoliclinica.domain.Lpus;
import com.example.botpoliclinica.dto.LpusDto;
import org.springframework.stereotype.Component;

@Component
public class LpusConverter {

    public Lpus convert(LpusDto source, District district) {
        return Lpus.builder()
                .id(source.getId())
                .fullName(source.getLpuFullName())
                .district(district)
                .address(source.getAddress())
                .type(source.getLpuType())
                .build();
    }
}
