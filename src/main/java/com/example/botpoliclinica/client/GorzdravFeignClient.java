package com.example.botpoliclinica.client;

import com.example.botpoliclinica.dto.AppointmentResponseDto;
import com.example.botpoliclinica.dto.DoctorResponseDto;
import com.example.botpoliclinica.dto.LpusResponseDto;
import com.example.botpoliclinica.dto.SpecialityResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "gorzdrav", url = "https://gorzdrav.spb.ru/_api/api/v2")
public interface GorzdravFeignClient {

    @GetMapping("/shared/district/{id}/lpus")
    LpusResponseDto getLpusByDistrict(@PathVariable("id") Integer districtId);

    @GetMapping("/schedule/lpu/{id}/specialties")
    SpecialityResponseDto getSpecialites(@PathVariable("id") Integer lpuId);

    @GetMapping("/schedule/lpu/{lpuId}/speciality/{specId}/doctors")
    DoctorResponseDto getDoctors(@PathVariable("lpuId") Integer lpuId, @PathVariable("specId") Integer specId);

    @GetMapping("/schedule/lpu/{lpuId}/doctor/{doctorId}/appointments")
    AppointmentResponseDto getAppointment(@PathVariable("lpuId") Long lpuId, @PathVariable("doctorId") String doctorId);
}
