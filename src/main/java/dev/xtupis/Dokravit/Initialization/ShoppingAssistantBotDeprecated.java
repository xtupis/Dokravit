package dev.xtupis.Dokravit.Initialization;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class ShoppingAssistantBotDeprecated {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "E:\\ProjectSAUMS\\chromedriver-win64\\chromedriver.exe");

        WebDriver driver = new ChromeDriver();
        try {
            driver.get("https://market.yandex.ru/");

            WebElement searchBox = driver.findElement(By.name("text"));
            searchBox.sendKeys("наушники");
            searchBox.submit();

            Thread.sleep(3000);

            List<WebElement> results = driver.findElements(By.cssSelector(".n-snippet-card2"));
            for (int i = 0; i < Math.min(5, results.size()); i++) {
                String title = results.get(i).findElement(By.cssSelector(".n-snippet-card2__title")).getText();
                String price = results.get(i).findElement(By.cssSelector(".n-snippet-card2__price")).getText();
                System.out.println((i + 1) + ". " + title + " - " + price);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}