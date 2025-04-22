package com.example.botpoliclinica.service;

import com.example.botpoliclinica.client.GorzdravFeignClient;
import com.example.botpoliclinica.domain.Lpus;
import com.example.botpoliclinica.domain.SearchRequest;
import com.example.botpoliclinica.dto.DoctorDto;
import com.example.botpoliclinica.dto.SpecialityDto;
import com.example.botpoliclinica.repository.SearchRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final GorzdravFeignClient gorzdravFeignClient;
    private final LpusService lpusService;
    private final SearchRequestRepository searchRequestRepository;

    public List<SpecialityDto> getSpecialities(Integer lpusId) {
        return gorzdravFeignClient.getSpecialites(lpusId).getResult();
    }

    public List<Lpus> getLpusByDistrictId(Integer districtId) {
        return lpusService.getLpusByDistrictId(districtId);
    }

    public List<DoctorDto> getDoctors(Integer lpusId, Integer specialityId) {
        return gorzdravFeignClient.getDoctors(lpusId, specialityId).getResult();
    }

    public void saveSearchRequest(SearchRequest searchRequest) {
        searchRequestRepository.save(searchRequest);
    }

    public List<SearchRequest> findSearchByChatId(Long chatId) {
        return searchRequestRepository.findByChatId(chatId);
    }

}
