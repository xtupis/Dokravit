package dev.xtupis.Dokravit.token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TokenProvider {

    public String getToken() {
        try {
            // Читаем весь файл token.txt из корня проекта и обрезаем пробелы
            return Files.readString(Path.of("token.txt")).trim();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать токен из файла token.txt", e);
        }
    }
}

