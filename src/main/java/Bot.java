import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    List<Double> operands = new ArrayList<>();
    List<String> operand1Numbers = new ArrayList<>();
    List<String> operand2Numbers = new ArrayList<>();
    boolean isOperand1 = false;
    boolean isOperand2 = false;
    boolean isOperation = false;
    StringBuilder operand1 = new StringBuilder();
    StringBuilder operand2 = new StringBuilder();
    StringBuilder numberBuilder = new StringBuilder();


    boolean manualCalculator = true;

    public void isAutoCalc(List<String> numbers, List<Double> operands) {
        for (int i = numbers.size(); i > 0; i--) {
            numberBuilder.append(numbers.get(i--));
        }
        operands.add(Double.parseDouble(numberBuilder.toString()));
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start":
                    sendMessage(chatId, "Привет! Я калькулятор-бот. Какой калькулятор хотите?" +
                            "\n/manualCalculator - ручной ввод" +
                            "\n/autoCalculator - автоматический ввод с помощью клавиатуры", manualCalculator);
                    break;
                case "/manualCalculator":
                    sendMessage(chatId, "/operand1 и /operand2 - это команды для ввода первого и " +
                            "второго чисел соответственно\n" +
                            "/operation - команда для ввода знака операции\n" +
                            "Используйте эти команды в любом порядке", manualCalculator);
                    break;
                case "/operand1":
                    isOperand1 = true;
                    isOperand2 = false;
                    isOperation = false;
                    sendMessage(chatId, "Введите первое число", manualCalculator);
                    break;
                case "/operand2":
                    isOperand1 = false;
                    isOperand2 = true;
                    isOperation = false;
                    sendMessage(chatId, "Введите второе число", manualCalculator);
                    break;
                case "/operation":
                    isOperand1 = false;
                    isOperand2 = false;
                    isOperation = true;
                    if (manualCalculator) {
                        sendMessage(chatId, "/add - сложить\n" +
                                "/subtract - вычесть\n" +
                                "/multiply - умножить\n" +
                                "/divide - разделить", true);
                    } else sendMessage(chatId, "Введите знак арифметического действия", false);
                    break;

                case "/add":
                    sendMessage(chatId, String.valueOf(operands.get(0) + operands.get(1)), manualCalculator);
                    break;
                case "/subtract":
                    sendMessage(chatId, String.valueOf(operands.get(0) - operands.get(1)), manualCalculator);
                    break;
                case "/multiply":
                    sendMessage(chatId, String.valueOf(operands.get(0) * operands.get(1)), manualCalculator);
                    break;
                case "/divide":
                    if (operands.get(1) != 0) {
                        sendMessage(chatId, String.valueOf(operands.get(0) / operands.get(1)), manualCalculator);
                    } else {
                        sendMessage(chatId, "На ноль делить нельзя", manualCalculator);
                    }
                    break;
                case "/autoCalculator":
                    manualCalculator = false;
                    sendMessage(chatId, "Используйте специальную клавиатуру.\n\n" +
                            "/operand1 - команда для ввода первого числа\n" +
                            "/operand2 - команда для ввода второго числа\n" +
                            "/operation - команда для ввода знака операции\n" +
                            "Команда операции должна быть последней", true);
                    break;
                default:
                    if (manualCalculator) {
                        operands.add(Double.parseDouble(messageText));
                    } else {
                        if (isOperand1) {
                            addToOperandNumbers(messageText, operand1Numbers);
                        } else if (isOperand2) {
                            addToOperandNumbers(messageText, operand2Numbers);
                        } else if (isOperation) {
                            int i = 0;
                            while (i < operand1Numbers.size()) {
                                operand1.append(operand1Numbers.get(i));
                                i++;
                            }
                            operands.add(Double.parseDouble(operand1.toString()));
                            i = 0;
                            while (i < operand2Numbers.size()) {
                                operand2.append(operand2Numbers.get(i));
                                i++;
                            }
                            switch (messageText){
                                case "+":
                                    sendMessage(chatId, String.valueOf(operands.get(0) + operands.get(1)), manualCalculator);
                                case "-":
                                    sendMessage(chatId, String.valueOf(operands.get(0) - operands.get(1)), manualCalculator);
                                case "*":
                                    sendMessage(chatId, String.valueOf(operands.get(0) * operands.get(1)), manualCalculator);
                                case "/":
                                    if (operands.get(1) != 0) {
                                        sendMessage(chatId, String.valueOf(operands.get(0) / operands.get(1)), manualCalculator);
                                    } else {
                                        sendMessage(chatId, "На ноль делить нельзя", manualCalculator);
                                    }
                                default:
                                    sendMessage(chatId, "Введите корректный знак арифметического действия", manualCalculator);
                            }
                            operands.add(Double.parseDouble(operand2.toString()));
                            //calculate(messageText, chatId);
                        }

                    }
                    break;
            }
        }
    }

    private void addToOperandNumbers(String messageText, List<String> operandNumbers) {
        operandNumbers.add(messageText);
        isAutoCalc(operandNumbers, operands);
        if (Byte.parseByte(messageText) == Byte.parseByte(operandNumbers.get(operandNumbers.size() - 1))) {
            isAutoCalc(operandNumbers, operands);
        }
    }

    private void calculate(String operation, long chatId) {
        sendMessage(chatId, "Результат вычислений: ", manualCalculator);

        switch (operation) {
            case "+":
                sendMessage(chatId, String.valueOf(operands.get(0) + operands.get(1)), manualCalculator);
            case "-":
                sendMessage(chatId, String.valueOf(operands.get(0) - operands.get(1)), manualCalculator);
            case "*":
                sendMessage(chatId, String.valueOf(operands.get(0) * operands.get(1)), manualCalculator);
            case "/":
                if (operands.get(1) != 0) {
                    sendMessage(chatId, String.valueOf(operands.get(0) / operands.get(1)), manualCalculator);
                } else {
                    sendMessage(chatId, "На ноль делить нельзя", manualCalculator);
                }
            default:
                sendMessage(chatId, "Введите корректный знак арифметического действия", manualCalculator);
        }


    }

    private ReplyKeyboardMarkup setButtons() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);

        KeyboardRow row = new KeyboardRow();
        row.add("C");
        row.add("+/-");
        row.add("%");
        row.add("/");

        KeyboardRow row1 = new KeyboardRow();
        row1.add("7");
        row1.add("8");
        row1.add("9");
        row1.add("*");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("4");
        row2.add("5");
        row2.add("6");
        row2.add("-");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("1");
        row3.add("2");
        row3.add("3");
        row3.add("+");

        KeyboardRow row4 = new KeyboardRow();
        row4.add("0");
        row4.add(".");
        row4.add("=");

        replyKeyboardMarkup.setKeyboard(List.of(row, row1, row2, row3, row4));
        return replyKeyboardMarkup;
    }

    private void sendMessage(long chatId, String text, boolean manualCalculator) {
        SendMessage sendMessage = new SendMessage();
        if (manualCalculator) {
            sendMessage.setReplyMarkup(setButtons());
        }
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return "Calculator_Of_Mirtov_bot";
    }

    @Override
    public String getBotToken() {
        return "7116466798:AAHaoZgP-nzAl9jCN8Dm2em7duymYSBR1NA";
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }
}

