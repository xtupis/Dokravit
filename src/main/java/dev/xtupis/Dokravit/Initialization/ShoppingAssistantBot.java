package dev.xtupis.Dokravit.Initialization;

import dev.xtupis.Dokravit.parser.YandexMarketParser;
import dev.xtupis.Dokravit.token.TokenProvider;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
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

    private void sendMessageWithButtons(long chatId, String text, List<InlineKeyboardButton> buttons) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setReplyMarkup(new InlineKeyboardMarkup(List.of(buttons))); // одна строка кнопок

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String extractUrlFromText(String text) {
        int index = text.indexOf("https://market.yandex.ru");
        if (index != -1) {
            int end = text.indexOf("\n", index);
            if (end == -1) end = text.length();
            return text.substring(index, end).trim();
        }
        return null;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (text.equals("/help")) {
                sendMessage(chatId,
                        """
                        🛒 Привет! Я — Докравит. 
                        Когда-то я был первым после Император Дефорский Империи, но меня понизили!
                        Моя сила не безгранична, но вот что ты можешь сделать:
                     
                        • /search ноутбук — найти товары на Яндекс Маркете
                        • Нажать кнопку «Открыть в браузере» под товаром
                        • Получить ссылки, отзывы, рейтинг и цену
                        """);
                return;
            }

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
                    for (String result : results) {
                        // Извлекаем ссылку из текста
                        String url = extractUrlFromText(result);
                        List<InlineKeyboardButton> buttons = new ArrayList<>();
                        if (url != null) {
                            buttons.add(InlineKeyboardButton.builder()
                                    .text("🌐 Открыть в браузере")
                                    .url(url)
                                    .build());
                        }
                        buttons.add(InlineKeyboardButton.builder()
                                .text("ℹ Инструкция")
                                .callbackData("show_help")
                                .build());

                        sendMessageWithButtons(chatId, result, buttons);
                    }
                }
            }
        }

        if (update.hasCallbackQuery()) {
            CallbackQuery query = update.getCallbackQuery();
            if ("show_help".equals(query.getData())) {
                long chatId = query.getMessage().getChatId();
                sendMessage(chatId,
                        """
                        ℹ️ Инструкция:
                        
                        1. Используй /search <запрос>, чтобы найти товар.
                        2. Нажми кнопку «Открыть в браузере», чтобы перейти к товару.
                        3. Получай отзывы, рейтинг и цену.
                        """);
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

    private void sendLongMessage(long chatId, String fullText) {
        int maxLength = 4096;
        for (int start = 0; start < fullText.length(); start += maxLength) {
            int end = Math.min(start + maxLength, fullText.length());
            String part = fullText.substring(start, end);
            sendMessage(chatId, part);
        }
    }

}