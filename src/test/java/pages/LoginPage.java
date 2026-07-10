package pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static io.qameta.allure.Allure.step;

public class LoginPage {
    final String ENDPOINT = "/login";
    final String LOGIN_PAGE_SELECTOR = ".page.login-page";
    final String HEADER_EXPECTED_TEXT = "Welcome, Please Sign In!";
    final String REGISTER_BLOCK_SELECTOR = ".new-wrapper.register-block";
    final String LOGIN_BLOCK_SELECTOR = ".returning-wrapper";
    final String CHECKBOX_AREA_SELECTOR = ".inputs.reversed";
    final String CHECKBOX_LABEL_EXPECTED_TEXT = "Remember me?";
    final String EMAIL_FIELD_SELECTOR = "#Email";
    final String EMAIL_PASSWORD_SELECTOR = "#Password";

    final SelenideElement header = $(LOGIN_PAGE_SELECTOR).$("h1");
    final SelenideElement registerButton = $("input[value = 'Register']");
    final SelenideElement loginButton = $("input[value = 'Log in']");
    final SelenideElement emailInputField = $(EMAIL_FIELD_SELECTOR);
    final SelenideElement passwordInputField = $(EMAIL_PASSWORD_SELECTOR);
    final SelenideElement checkboxInput = $(CHECKBOX_AREA_SELECTOR).$("input#RememberMe");
    final SelenideElement checkboxLabel = $(CHECKBOX_AREA_SELECTOR).$("label[for='RememberMe']");
    final SelenideElement forgotPassword = $(Selectors.byLinkText("Forgot password?"));


    public LoginPage open() {
        Selenide.open(ENDPOINT);
        return this;
    }

    public LoginPage shouldBeValid() {
        step("Проверяем что форма открылась и отображается", () -> {
            $(LOGIN_PAGE_SELECTOR).shouldBe(visible);
        });
        step("Заголовок страницы должен быть: " + HEADER_EXPECTED_TEXT, () -> {
            header.shouldHave(text(HEADER_EXPECTED_TEXT));
        });
        step("Кнопки регистрации и авторизации отображаются и доступны " + HEADER_EXPECTED_TEXT, () -> {
            registerButton.shouldBe(enabled, clickable);
            loginButton.shouldBe(enabled, clickable);
        });
        step("Есть поля ввода логина и пароля в блоке 'Returning Customer'", () -> {
            emailInputField.shouldBe(visible, enabled, clickable);
            passwordInputField.shouldBe(visible, enabled, clickable);
        });
        step("Чек-бокс " + CHECKBOX_LABEL_EXPECTED_TEXT + "отображается и доступен", () -> {
            checkboxInput.shouldBe(enabled, clickable);
            checkboxLabel.shouldBe(visible).shouldHave(text(CHECKBOX_LABEL_EXPECTED_TEXT));
        });
        return this;
    }

    public LoginPage clickRegisterButton() {
        registerButton.click();
        return this;
    }

    public LoginPage clickLoginButton() {
        loginButton.click();
        return this;
    }

    public LoginPage clickForgotPassword(){
        forgotPassword.click();
        return this;
    }

    public LoginPage setRememberMeCheckbox(){
        checkboxInput.click();
        return this;
    }

    public LoginPage loginWithCredentials(String email, String password){
        if(email.isBlank() || password.isBlank()){
            throw new IllegalArgumentException(
                    String.format("Не переданы значения значения email или password.\nemail:[%s], password:[%s]"
                    , email, password)
            );
        }
        emailInputField.setValue(email);
        passwordInputField.setValue(password);
        clickLoginButton();
        return this;
    }
}
