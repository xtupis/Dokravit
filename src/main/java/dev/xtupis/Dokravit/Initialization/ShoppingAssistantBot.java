package dev.xtupis.Dokravit.Initialization;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ShoppingAssistantBot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "Докравит";
    }

    @Override
    public String getBotToken() {
        return "8053237627:AAHl7anm2lJi-93vy7ZnAgcZW-GLvvSsbpQ";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.equals("/start")) {
                StringBuilder response = new StringBuilder();
                response.append("Привет! ");
                response.append("Я — Докравит. ");
                response.append("Чем могу помочь?");

                SendMessage message = new SendMessage();
                message.setChatId(String.valueOf(chatId));
                message.setText(response.toString());

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
