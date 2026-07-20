package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import utils.Attachments;

public class BaseTest {

    @BeforeAll
    static void beforeAll() {
        Configuration.baseUrl = "https://demowebshop.tricentis.com";
        SelenideLogger.addListener("AllureListener", new AllureSelenide());
    }

    @AfterEach
    void tearDown() {
        Attachments.addScreenshot();
    }
}
