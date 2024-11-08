import org.telegram.telegrambots.api.methods.send.SendMessage;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TheBot extends TelegramLongPollingBot {

    private static final String BOT_USERNAME = "IgnatMirtov_bot";
    private static final String BOT_TOKEN = "7738195018:AAEnyGAzuiWLc1lY0Pv-Wd34C5gcEdYlBxg";

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.equals("/start")) {
                sendMessage(chatId, "Привет! Я калькулятор-бот. \n/calculator - команда для вызова калькулятора.");
            } else if (messageText.equals("/calculator")) {
                sendMessage(chatId, "Введите арифметическую операцию");
            } else {
                try {
                    double result = calculate(messageText);
                    sendMessage(chatId, "Результат: " + result);
                } catch (IllegalArgumentException e) {
                    sendMessage(chatId, "Ошибка: " + e.getMessage());
                }
            }
        }
    }

    private double calculate(String expression) {
        Pattern pattern = Pattern.compile("\\s*(-?\\d+(?:\\,\\d+)?)(\\s*[+\\-*/]\\s*)(-?\\d+(?:\\,\\d+)?)\\s*");
        Matcher matcher = pattern.matcher(expression);

        if (matcher.matches()) {
            double num1 = Double.parseDouble(matcher.group(1).replace(",", "."));
            String operator = matcher.group(2).trim();
            double num2 = Double.parseDouble(matcher.group(3).replace(",", "."));

            switch (operator) {
                case "+":
                    return num1 + num2;
                case "-":
                    return num1 - num2;
                case "*":
                    return num1 * num2;
                case "/":
                    if (num2 == 0) {
                        throw new IllegalArgumentException("Деление на ноль!");
                    }
                    return num1 / num2;
                default:
                    throw new IllegalArgumentException("Неверный оператор!");
            }
        } else {
            throw new IllegalArgumentException("Неверный формат выражения!");
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}