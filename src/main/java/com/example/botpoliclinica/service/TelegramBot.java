package com.example.botpoliclinica.service;

import com.example.botpoliclinica.config.TelegramBotConfiguration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final TelegramBotConfiguration telegramBotConfiguration;

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
        System.out.println(update);
        Message message = update.getMessage();
        sendMessage(message.getChatId(), String.format("Привет, %s", message.getFrom().getFirstName()));
//        ResponseDto responseDto = new ResponseDto();
//        String currency = "";
//
//        if(update.hasMessage() && update.getMessage().hasText()){
//            String messageText = update.getMessage().getText();
//            long chatId = update.getMessage().getChatId();
//
//            switch (messageText){
//                case "/start":
//                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
//                    break;
//                default:
//                    currency = "hi";
//                    sendMessage(chatId, currency);
//            }
//        }

    }

    public void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
