package dev.xtupis.Dokravit.parser;

import com.microsoft.playwright.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class YandexMarketParser {

    public static List<String> searchYandexMarket(String query) {
        List<String> results = new ArrayList<>();

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true));
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"));
            Page page = context.newPage();

            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://market.yandex.ru/search?text=" + encodedQuery;

            page.navigate(url);

            page.waitForSelector("span[data-auto='snippet-title']", new Page.WaitForSelectorOptions().setTimeout(30000));
            List<ElementHandle> titleElements = page.querySelectorAll("span[data-auto='snippet-title']");

            for (int i = 0; i < Math.min(4, titleElements.size()); i++) {
                ElementHandle titleElem = titleElements.get(i);
                String title = titleElem.innerText().trim();

                // Находим родительский контейнер товара
                ElementHandle linkElem = titleElem.evaluateHandle("el => el.closest('a[data-auto=\"snippet-link\"]')").asElement();

                String link = "Ссылка не найдена";
                if (linkElem != null) {
                    String href = linkElem.getAttribute("href");
                    if (href != null && !href.isEmpty()) {
                        link = "https://market.yandex.ru" + href;
                    }
                }

// Родительский контейнер товара
                ElementHandle itemElem = titleElem.evaluateHandle("el => el.closest('article[data-auto=\"snippet\"]')").asElement();

                // Отзывы
                String reviews = "Нет отзывов";
                if (itemElem != null) {
                    ElementHandle reviewsElem = itemElem.querySelector("span[data-auto='rating-count']");
                    if (reviewsElem != null) {
                        reviews = reviewsElem.innerText().trim();
                    }
                }

                results.add(String.format("%d) %s\nЦена: пока пропускаем\nОтзывы: %s\nСсылка: %s",
                        i + 1, title, reviews, link));
            }

            browser.close();
        } catch (Exception e) {
            e.printStackTrace();
            results.add("Ошибка при поиске: " + e.getMessage());
        }

        return results;
    }
}