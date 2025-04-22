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
//    private final GorzdravFeignClient gorzdravFeignClient;

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
        Message message = update.getMessage();
        if (message != null) {
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

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            String[] dataParts = data.split("_");
            if (dataParts[0].equals("district")) {
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

            if (dataParts[0].equals("lpus")) {
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

            if (dataParts[0].equals("speciality")) {
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

            if (dataParts[0].equals("doctor")) {
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
        }
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
