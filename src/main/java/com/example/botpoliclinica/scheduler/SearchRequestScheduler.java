package com.example.botpoliclinica.scheduler;

import com.example.botpoliclinica.domain.SearchRequest;
import com.example.botpoliclinica.service.AppointmentService;
import com.example.botpoliclinica.service.TelegramBot;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchRequestScheduler {
    private final AppointmentService appointmentService;
    private final TelegramBot telegramBot;

    @Scheduled(cron = "0 0 18 * * *") // каждый день в 18:00 TODO вынести в конфиг
    @Transactional
    public void markOldRequestsAsCompleted() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        List<SearchRequest> expiredRequests = appointmentService.findAllByCompletedFalseAndCreatedDatetimeBefore(oneWeekAgo);

        for (SearchRequest request : expiredRequests) {
            telegramBot.sendMessage(request.getChatId(), "Заявка устарела и была завершена.", null);
            request.setCompleted(true);
            appointmentService.saveSearchRequest(request);
        }
        log.info("Завершено {} заявок", expiredRequests.size());
    }

    @Scheduled(cron = "0 */5 * * * *") // каждые 5 минут TODO вынести в конфиг
    @Transactional
    public void checkAppointments() {
        List<SearchRequest> searchRequests = appointmentService.findAllByCompletedFalse();
        Map<String, List<SearchRequest>> searchRequestsByDoctor = searchRequests.stream()
                .collect(Collectors.groupingBy(SearchRequest::getDoctorId));
        searchRequestsByDoctor.forEach((doctorId, doctorRequests) -> {
            Long lpuId = doctorRequests.get(0).getLpuId();
            boolean hasAppointment = appointmentService.hasAppointment(lpuId, doctorId);
            if (hasAppointment) {
                String message = "Номерок найден. Для записи перейдите на сайт горздрав."; //TODO добавить ссылку
                doctorRequests.forEach(doctorRequest -> {
                    doctorRequest.setCompleted(true);
                    doctorRequest.setFound(true);
                    appointmentService.saveSearchRequest(doctorRequest);
                    telegramBot.sendMessage(doctorRequest.getChatId(), message, null);
                });
            }
        });
        log.info("Поиск номерков завершен");
    }
}
