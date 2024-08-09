package com.example.botpoliclinica.service;

import com.example.botpoliclinica.dto.Doctor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentService {
    private final TelegramBot telegramBot;

    public void getAppointment() throws IOException, LineUnavailableException {
        //URL url = new URL("https://gorzdrav.spb.ru/_api/api/v2/schedule/lpu/256/doctor/%D0%B473%2e137%D0%B0/appointments");
        URL url = new URL("https://gorzdrav.spb.ru/_api/api/v2/schedule/lpu/256/speciality/50/doctors");
        Scanner scanner = new Scanner((InputStream) url.getContent());
        StringBuilder result = new StringBuilder();
        while (scanner.hasNext()){
            result.append(scanner.nextLine());
        }
        JSONObject object = new JSONObject(result.toString());

        String result1 = object.getJSONArray("result").toString();
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        List<Doctor> appointmentList = mapper.readValue(result1, new TypeReference<>() {});
        Optional<Doctor> first = appointmentList.stream()
                //.filter(appointment -> appointment.getVisitStart().getDayOfMonth() == 26)
                .filter(doctor -> doctor.getFreeTicketCount() > 0)
                .findFirst();
        if (first.isPresent()) {
            telegramBot.sendMessage(459075077L,"Номерок найден");
            log.info("Номерок найден");
        } else {
            log.info("Номерок не найден");
        }

    }
}
