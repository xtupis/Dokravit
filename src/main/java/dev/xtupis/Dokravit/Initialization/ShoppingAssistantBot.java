package dev.xtupis.Dokravit.Initialization;

import dev.xtupis.Dokravit.parser.YandexMarketParser;
import dev.xtupis.Dokravit.token.TokenProvider;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class ShoppingAssistantBot extends TelegramLongPollingBot {

    private final TokenProvider tokenProvider = new TokenProvider();

    @Override
    public String getBotUsername() {
        return "Докравит";
    }

    @Override
    public String getBotToken() {
        return tokenProvider.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (text.startsWith("/search")) {
                String query = text.replaceFirst("/search", "").trim();
                if (query.isEmpty()) {
                    sendMessage(chatId, "Пожалуйста, укажи, что искать. Пример: /search телевизор");
                    return;
                }

                sendMessage(chatId, "🔍 Ищу товары по запросу: " + query + "...");


                List<String> results = YandexMarketParser.searchYandexMarket(query);

                if (results.isEmpty() || results.stream().allMatch(String::isBlank)) {
                    sendMessage(chatId, "😕 Не удалось найти товары по запросу: " + query);
                } else {
                    String response = String.join("\n\n", results);
                    sendMessage(chatId, response);
                }
            }
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}