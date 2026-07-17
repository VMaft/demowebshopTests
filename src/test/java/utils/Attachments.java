package utils;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import dto.User;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static io.qameta.allure.Allure.step;

public class Attachments {
    private static final Logger log = LoggerFactory.getLogger(Attachments.class);

    /**
     * Создаёт скриншот текущей страницы и прикрепляет его к Allure-отчёту.
     * <p>
     * Если WebDriver не активен или произошла ошибка при создании скриншота,
     * возвращает PNG-изображение с текстом ошибки.
     * </p>
     *
     * @return массив байтов скриншота в формате PNG или изображение с текстом ошибки
     *
     * @see io.qameta.allure.Attachment
     */
    @Attachment(value = "Screenshot", type = "image/png", fileExtension = "png")
    public static byte[] addScreenshot() {
        boolean hasWebDriver = WebDriverRunner.hasWebDriverStarted();

        if (hasWebDriver) {
            try {
                System.out.println("Скриншот успешно добавлен!");
                return ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES);
            } catch (Exception e) {
                System.err.println("Ошибка при создании скриншота: " + e.getMessage());
            }
        } else {
            System.out.println("WebDriver не активен, скриншот пропущен.");
        }
        return createErrorImage("WebDriver is not active!\nScreenshot cannot be taken.");
    }


    // Будущее постоянное решение на логирование параметров пользователя в тесте.
    public static void addTestUserToParameters(User user) throws IllegalAccessException {
        Class<?> userClass = user.getClass();

        for (Field field : userClass.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Allure.parameter(
                        field.getName(),
                        String.valueOf(field.get(user))
                );
            }catch (IllegalAccessException e) {
                System.err.println("Не удалось прочитать поле: " + field.getName());
            }
        }
    }

    // Топорное решение по добавлению тестовых данных пользователя в параметры теста
    public static void saveTestUserDataToParameters(User user) {
        if(user == null) {
            log.warn("Передан пустой объект user.");
        }
        step("Сохраняем данные тестового пользователя в параметры теста", () -> {
            Allure.parameter("First Name", user.getFirstName());
            Allure.parameter("Last Name", user.getLastName());
            Allure.parameter("Email", user.getEmail());
            Allure.parameter("Gender", user.getGender());
            Allure.parameter("Password", user.getPassword());
        });
        log.info("Test Data:{\n\t{}\n}", user);
    }

    public static void attachRequiredFieldsTestsParameters(String caseName, String expectedMessageText, User userData){
        Allure.parameter("Данные пользователя", userData);
        Allure.parameter("Ожидаемое сообщение", expectedMessageText);
        Allure.parameter("Кейс", caseName);
    }

    /**
     * Создает PNG-изображение с визуализацией сообщения об ошибке.
     *
     * @param errorMessage сообщение об ошибке (поддерживает перенос строк \n)
     * @return байты PNG-изображения или пустой массив при ошибке
     */
    private static byte[] createErrorImage(String errorMessage) {
        try {
            int width = 600;
            int height = 200;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();

            // Белый фон
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            // Красная рамка
            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(3));
            g.drawRect(5, 5, width - 10, height - 10);

            // Текст ошибки
            g.setColor(Color.RED);
            g.setFont(new Font("Monospaced", Font.BOLD, 14));

            // Поддержка многострочного текста
            String[] lines = errorMessage.split("\n");
            int y = 50;
            for (String line : lines) {
                g.drawString(line, 20, y);
                y += 25;
            }

            // Добавляем timestamp для контекста
            g.setFont(new Font("Monospaced", Font.PLAIN, 11));
            g.setColor(Color.GRAY);
            g.drawString(new java.util.Date().toString(), 20, height - 20);

            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();

        } catch (Exception e) {
            System.err.println("Не удалось создать PNG с ошибкой: " + e.getMessage());
            return new byte[0];  // fallback
        }
    }

    /**
     * Добавить текст с логированием (и в консоль, и в AllureReport)
     * @param name название вложения
     * @param content текст
     */
    public static void attachTextAndLog(String name, String content) {
        System.out.println(String.format("[ATTACH] %s: %s", name, content));
        Allure.addAttachment(name, "text/plain", content);
    }

    /**
     * Добавить текст с автоматическим добавлением времени
     * @param name название вложения
     * @param content текст
     */
    public static void attachTextWithTimestamp(String name, String content) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        String fullName = String.format("[%s] %s", timestamp, name);
        Allure.addAttachment(fullName, "text/plain", content);
    }

    /**
     * Добавить текстовое вложение в AllureReport
     * @param name название вложения
     * @param content текст
     */
    public static void attachText(String name, String content) {
        Allure.addAttachment(name, "text/plain", content);
    }


    /**
     * Классическое решение по добавлению текста в AllureReport
     */
    @Attachment(value = "{attachmentName}", type = "text/plain")
    public static String attachTextToAllure(String attachmentName, String content) {
        return content;
    }

    @Attachment(value = "Table Row", type = "text/html;charset=UTF-8")
    public static String attachRowAsHtml(SelenideElement tableRow, ElementsCollection tableHeaders) {
        StringBuilder html = new StringBuilder();
        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        html.append("<tr style='background-color: #f2f2f2;'>");

        // Можно добавить заголовки, если знаешь
        // html.append("<th>Column 1</th><th>Column 2</th><th>Column 3</th>");

        //tableColumns
        html.append("<tr>");
        for (SelenideElement header : tableHeaders) {
            html.append("<th>").append(header.text()).append("</th>");
        }
        html.append("</tr>");

        for (SelenideElement cell : tableRow.$$("td")) {
            html.append("<td>").append(cell.text()).append("</td>");
        }

        html.append("</tr></table>");
        return html.toString();
    }
}