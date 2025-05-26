package dev.xtupis.Dokravit;

import dev.xtupis.Dokravit.Initialization.ShoppingAssistantBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new ShoppingAssistantBot());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}