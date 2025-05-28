package dev.xtupis.Dokravit.parser;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class YandexMarketParser {

    public static List<String> searchYandexMarket(String query) {
        List<String> results = new ArrayList<>();

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
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

                // Получаем ссылку
                ElementHandle linkElem = titleElem.evaluateHandle("el => el.closest('a[data-auto=\"snippet-link\"]')").asElement();
                String link = "Ссылка не найдена";
                String fullLink = null;
                if (linkElem != null) {
                    String href = linkElem.getAttribute("href");
                    if (href != null && !href.isEmpty()) {
                        fullLink = "https://market.yandex.ru" + href;
                        link = fullLink;
                    }
                }

                // Отзывы с превью
                ElementHandle itemElem = titleElem.evaluateHandle("el => el.closest('article[data-auto=\"snippet\"]')").asElement();
                String reviewsPreview = "Нет отзывов";
                if (itemElem != null) {
                    ElementHandle reviewsElem = itemElem.querySelector("span[data-auto='rating-count']");
                    if (reviewsElem != null) {
                        reviewsPreview = reviewsElem.innerText().trim();
                    }
                }

                // Значения со страницы товара:
                String price = "Возможно, вам доступна скидка в связи с распродажей или с картой Яндекс Pay";
                String reviewsDetailed = "Не найдены";
                String rating = "Не указан";

                if (fullLink != null) {
                    Page productPage = context.newPage();
                    productPage.navigate(fullLink);
                    productPage.waitForLoadState(LoadState.DOMCONTENTLOADED);

                    // Отзывы со страницы
                    try {
                        productPage.waitForSelector("span[data-auto='ratingCount']", new Page.WaitForSelectorOptions().setTimeout(5000));
                        ElementHandle reviewsElem = productPage.querySelector("span[data-auto='ratingCount']");
                        if (reviewsElem != null) {
                            reviewsDetailed = reviewsElem.innerText().trim();
                        }
                    } catch (Exception ignored) {}

                    // Рейтинг товара
                    try {
                        productPage.waitForSelector("span[data-auto='ratingValue']", new Page.WaitForSelectorOptions().setTimeout(5000));
                        ElementHandle ratingElem = productPage.querySelector("span[data-auto='ratingValue']");
                        if (ratingElem != null) {
                            rating = ratingElem.innerText().trim();
                        }
                    } catch (Exception ignored) {}

                    productPage.close();
                }

                results.add(String.format("%d) %s\nЦена: %s\nРейтинг: %s ⭐️\nОтзывы: %s (в превью: %s)\nСсылка: %s",
                        i + 1, title, price, rating, reviewsDetailed, reviewsPreview, link));
            }

            browser.close();
        } catch (Exception e) {
            e.printStackTrace();
            results.add("Ошибка при поиске: " + e.getMessage());
        }

        return results;
    }
}
