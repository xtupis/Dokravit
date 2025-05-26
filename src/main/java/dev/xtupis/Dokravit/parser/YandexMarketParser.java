package dev.xtupis.Dokravit.parser;

import com.microsoft.playwright.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;

import static com.microsoft.playwright.BrowserType.*;

public class YandexMarketParser {

    public static List<String> searchYandexMarket(String query) {
        List<String> results = new ArrayList<>();

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new LaunchOptions()
                    .setHeadless(true));
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"));
            Page page = context.newPage();

            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://market.yandex.ru/search?text=" + encodedQuery;

            page.navigate(url);

            page.waitForSelector("span[data-auto='snippet-title']", new Page.WaitForSelectorOptions().setTimeout(15000));

            List<ElementHandle> elements = page.querySelectorAll("span[data-auto='snippet-title']");

            for (int i = 0; i < Math.min(5, elements.size()); i++) {
                results.add(elements.get(i).innerText().trim());
            }

            browser.close();
        } catch (Exception e) {
            e.printStackTrace();
            results.add("Ошибка при поиске: " + e.getMessage());
        }

        return results;
    }

    public static void main(String[] args) {
        List<String> laptops = searchYandexMarket("ноутбук");
        laptops.forEach(System.out::println);
    }

}
