package com.example.botpoliclinica.service;

import com.example.botpoliclinica.client.GorzdravFeignClient;
import com.example.botpoliclinica.config.TelegramBotConfiguration;
import com.example.botpoliclinica.domain.District;
import com.example.botpoliclinica.domain.Lpus;
import com.example.botpoliclinica.domain.SearchRequest;
import com.example.botpoliclinica.dto.DoctorDto;
import com.example.botpoliclinica.dto.SpecialityDto;
import com.example.botpoliclinica.repository.DistrictRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final TelegramBotConfiguration telegramBotConfiguration;
    private final DistrictRepository districtRepository;
    private final AppointmentService appointmentService;

    @Override
    public String getBotUsername() {
        return telegramBotConfiguration.getName();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfiguration.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage() != null && update.getMessage().getChatId() != null
                ? update.getMessage().getChatId()
                : update.getCallbackQuery().getMessage().getChatId();

        List<SearchRequest> searchByChatId = appointmentService.findSearchByChatId(chatId);
        if (searchByChatId.size() > 1) {
            sendMessage(chatId, "У вас есть 2 активных поиска. Новые будут доступны после их завершения.", null);
        } else {
            Message message = update.getMessage();
            if (message != null) {
                sendDistrictMessage(update);
            }

            if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                String data = callbackQuery.getData();
                String[] dataParts = data.split("_");
                if (dataParts[0].equals("district")) {
                    sendLpusMessage(dataParts, callbackQuery);
                }

                if (dataParts[0].equals("lpus")) {
                    sendSpecialityMessage(dataParts, callbackQuery);
                }

                if (dataParts[0].equals("speciality")) {
                    sendDoctorsMessage(dataParts, callbackQuery);
                }

                if (dataParts[0].equals("doctor")) {
                    createResultMessage(dataParts, callbackQuery);
                }
            }
        }
    }

    private void createResultMessage(String[] dataParts, CallbackQuery callbackQuery) {
        if (Integer.parseInt(dataParts[3]) > 0) {
            sendMessage(callbackQuery.getMessage().getChatId(), "Свободные номерки доступны на сайте горздрав", null); //TODO добавить кликабельную ссылку на врача
        } else {
            SearchRequest searchRequest = SearchRequest.builder()
                    .chatId(callbackQuery.getMessage().getChatId())
                    .lpuId(Long.valueOf(dataParts[1]))
                    .doctorId(dataParts[2])
                    .build();
            appointmentService.saveSearchRequest(searchRequest);
            sendMessage(callbackQuery.getMessage().getChatId(), "Запрос на поиск сформирован", null);
        }
    }

    private void sendDoctorsMessage(String[] dataParts, CallbackQuery callbackQuery) {
        List<DoctorDto> doctorDtos = appointmentService.getDoctors(Integer.valueOf(dataParts[1]), Integer.valueOf(dataParts[2]));
        List<InlineButtonMessageDto> lpusMessageDtos = doctorDtos.stream()
                .map(doctorDto -> new InlineButtonMessageDto(
                        String.format("%s (%s номерков свободно)", doctorDto.getName(), doctorDto.getFreeTicketCount()),
                        String.format("doctor_%s_%s_%s", dataParts[1], doctorDto.getId(), doctorDto.getFreeTicketCount())
                ))
                .toList();
        InlineKeyboardMarkup markup = getKeyBoardRows(lpusMessageDtos, 1);
        sendMessage(callbackQuery.getMessage().getChatId(), "Выберите врача:", markup);
    }

    private void sendSpecialityMessage(String[] dataParts, CallbackQuery callbackQuery) {
        Integer lpuId = Integer.valueOf(dataParts[1]);
        List<SpecialityDto> specialities = appointmentService.getSpecialities(lpuId);
        List<InlineButtonMessageDto> lpusMessageDtos = specialities.stream()
                .map(specialityDto -> new InlineButtonMessageDto(
                        specialityDto.getName(),
                        String.format("speciality_%s_%s", lpuId, specialityDto.getId().toString())
                ))
                .toList();
        InlineKeyboardMarkup markup = getKeyBoardRows(lpusMessageDtos, 1);
        sendMessage(callbackQuery.getMessage().getChatId(), "Выберите специальность:", markup);
    }

    private void sendLpusMessage(String[] dataParts, CallbackQuery callbackQuery) {
        List<Lpus> lpusByDistrictId = appointmentService.getLpusByDistrictId(Integer.valueOf(dataParts[1]));
        List<InlineButtonMessageDto> lpusMessageDtos = lpusByDistrictId.stream()
                .map(lpus -> new InlineButtonMessageDto(
                        lpus.getFullName(),
                        String.format("lpus_%s", lpus.getId().toString())
                ))
                .toList();
        InlineKeyboardMarkup markup = getKeyBoardRows(lpusMessageDtos, 1);
        sendMessage(callbackQuery.getMessage().getChatId(), "Выберите поликлинику:", markup);
    }

    private void sendDistrictMessage(Update update) {
        List<District> districts = districtRepository.findAll();
        List<InlineButtonMessageDto> districtsMessageDtos = districts.stream()
                .map(district -> new InlineButtonMessageDto(
                        district.getName(),
                        String.format("district_%s", district.getId().toString()))
                )
                .toList();
        InlineKeyboardMarkup markup = getKeyBoardRows(districtsMessageDtos, 3);
        sendMessage(update.getMessage().getChatId(), "Бот предназначен для поиска номерков в поликлиниках СПб. " +
                "Выберите врача и бот уведомит вас, когда номерок появится. Срок поиска - 1 неделя.", null);
        sendMessage(update.getMessage().getChatId(), "Выберите район:", markup);
    }

    private InlineKeyboardMarkup getKeyBoardRows(List<InlineButtonMessageDto> values, int countByRow) {
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons = values.stream()
                .map(value -> createButton(
                        value.getText(),
                        value.getCallbackData()))
                .toList();

        for (int i = 0; i < values.size(); i += countByRow) {
            int end = Math.min(i + countByRow, values.size());
            List<InlineKeyboardButton> subList = inlineKeyboardButtons.subList(i, end);
            keyboardRows.add(subList);
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboardRows);
        return markup;
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    public void sendMessage(Long chatId,
                            String textToSend,
                            InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Data
    @AllArgsConstructor
    public static class InlineButtonMessageDto {
        private String text;
        private String callbackData;
    }
}
