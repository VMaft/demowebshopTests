package pages;

import com.codeborne.selenide.*;
import dto.User;
import dto.enums.Gender;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tests.data.enums.ValidationErrors;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static tests.data.enums.ValidationErrors.*;

public class UserRegistrationPage {
    private static final Logger log = LoggerFactory.getLogger(UserRegistrationPage.class);

    final String ENDPOINT = "/register";
    final String VALIDATION_ERROR_CLASS = ".field-validation-error";
    final String LABELS_SELECTOR = "label";
    final String[] LABELS_EXPECTED_TEXTS = {
            "Gender:", "First name:", "Last name:", "Email:", "Password:", "Confirm password:", "Male", "Female"
    };

    final String REGISTER_COMPLETED_TEXT = "Your registration completed";

    final String FIRST_NAME_SELECTOR = "#FirstName";
    final String LAST_NAME_SELECTOR = "#LastName";
    final String EMAIL_SELECTOR = "#Email";
    final String PASSWORD_SELECTOR = "#Password";
    final String MALE_GENDER_SELECTOR = "#gender-male";
    final String FEMALE_GENDER_SELECTOR = "#gender-female";

    final SelenideElement genderMale = $(MALE_GENDER_SELECTOR);
    final SelenideElement genderFemale = $(FEMALE_GENDER_SELECTOR);
    final SelenideElement firstName = $(FIRST_NAME_SELECTOR);
    final SelenideElement lastName = $(LAST_NAME_SELECTOR);
    final SelenideElement email = $(EMAIL_SELECTOR);
    final SelenideElement password = $(PASSWORD_SELECTOR);
    final SelenideElement confirmPassword = $("#ConfirmPassword");
    final SelenideElement registerButton = $("#register-button");
    final SelenideElement resultMessage = $(".page-body > .result");
    final SelenideElement registerCompleteMessage = $(withText(REGISTER_COMPLETED_TEXT));
    final SelenideElement continueButton = $(Selectors.byValue("Continue"));
    final SelenideElement headersAccountLink = $(".header-links .account");

    final SelenideElement firstNameErrorLabel = $("span[for = 'FirstName']");
    final SelenideElement lastNameErrorLabel = $("span[for='LastName']");
    final SelenideElement emailErrorLabel = $("span[for='Email']");
    final SelenideElement passwordErrorLabel = $("span[for='Password']");
    final SelenideElement confirmPasswordErrorLabel = $("span[for='ConfirmPassword']");

    WebElementCondition[] valid = {enabled, visible, clickable};

    public UserRegistrationPage open() {
        Selenide.open(ENDPOINT);
        return this;
    }

    public UserRegistrationPage clickRegisterButton() {
        registerButton.click();
        return this;
    }

    public UserRegistrationPage clickTabKey() {
        actions().sendKeys(Keys.TAB).perform();
        return this;
    }

    public UserRegistrationPage validateRegistrationForm() {
        step("Текстовые названия элементов формы отображаются корректно", () -> {
            Assertions.assertThat(
                    $$(LABELS_SELECTOR).texts()
                            .stream()
                            .map(String::trim)
                            .collect(Collectors.toList())
            ).containsExactlyInAnyOrderElementsOf(List.of(LABELS_EXPECTED_TEXTS));
        });
        step("Проверяем что текстовые поля отображаются и доступны", () -> {
            genderMale.shouldBe(valid);
            genderFemale.shouldBe(valid);
            firstName.shouldBe(valid);
            lastName.shouldBe(valid);
            email.shouldBe(valid);
            password.shouldBe(valid);
            confirmPassword.shouldBe(valid);
        });
        step("Кнопка регистрации отображается и доступна", () -> {
            registerButton.shouldBe(valid);
        });
        return this;
    }

    public UserRegistrationPage setGender(Gender gender) {
        if (gender.equals(Gender.MALE)) genderMale.click();
        else genderFemale.click();
        return this;
    }

    public UserRegistrationPage setFirstName(String firstNameValue) {
        setFieldValue(firstName, firstNameValue);
        return this;
    }

    public UserRegistrationPage setLastName(String lastNameValue) {
        setFieldValue(lastName, lastNameValue);
        return this;
    }

    public UserRegistrationPage setEmail(String emailValue) {
        setFieldValue(email, emailValue);
        return this;
    }

    public UserRegistrationPage setPassword(String passwordValue) {
        setFieldValue(password, passwordValue);
        return this;
    }

    public UserRegistrationPage confirmPasswordWith(String passwordValue) {
        setFieldValue(confirmPassword, passwordValue);
        return this;
    }

    public UserRegistrationPage fillRegistrationFormWithUserData(User user) {
        if (user.getGender() != null) setGender(user.getGender());
        setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .confirmPasswordWith(user.getPassword());
        return this;
    }

    public UserRegistrationPage clickContinue() {
        continueButton.click();
        return this;
    }

    public UserRegistrationPage clickAccountLink() {
        headersAccountLink.click();
        return this;
    }

    public UserRegistrationPage verifySuccessRegistration() {
        registerCompleteMessage.shouldBe(appear);
        continueButton.shouldBe(valid);
        return this;
    }

    private void setFieldValue(SelenideElement field, String fieldValue) {
        String fieldName = field.getAttribute("name");
        if (fieldName == null) fieldName = "unknown";

        if (fieldValue.isBlank()) {
            log.info("{}\nДобавление значения в поле {} пропущено.",
                    String.format("Не передан аргумент для поля '%s'. Получено: {'%s':%s}"
                    , fieldName, fieldName, fieldValue), fieldName);
            return;
        }
        field.setValue(fieldValue);
        field.shouldHave(Condition.value(fieldValue));
    }

    public UserRegistrationPage shouldContainsProfileLinkWithEmail(String email) {
        $(Selectors.byLinkText(email)).shouldBe(valid);
        return this;
    }

    public UserRegistrationPage verifyUserProfileContainsValidUserData(User user) {
        if (user.getGender() != null) {
            if (user.getGender().equals(Gender.MALE)) $(MALE_GENDER_SELECTOR).shouldBe(selected);
            else $(FEMALE_GENDER_SELECTOR).shouldBe(selected);
        } else {
            $(MALE_GENDER_SELECTOR).shouldNotBe(selected);
            $(FEMALE_GENDER_SELECTOR).shouldNotBe(selected);
        }
        $(FIRST_NAME_SELECTOR).shouldHave(value(user.getFirstName()));
        $(LAST_NAME_SELECTOR).shouldHave(value(user.getLastName()));
        $(EMAIL_SELECTOR).shouldHave(value(user.getEmail()));
        return this;
    }

    public UserRegistrationPage verifyRequiredFieldsMessagesAppear() {
        $(firstNameErrorLabel).shouldBe(visible).shouldHave(text(FIRST_NAME_REQUIRED.getMessage()));
        $(lastNameErrorLabel).shouldBe(visible).shouldHave(text(LAST_NAME_REQUIRED.getMessage()));
        $(emailErrorLabel).shouldBe(visible).shouldHave(text(EMAIL_REQUIRED.getMessage()));
        $(passwordErrorLabel).shouldBe(visible).shouldHave(text(PASSWORD_REQUIRED.getMessage()));
        $(confirmPasswordErrorLabel).shouldBe(visible).shouldHave(text(PASSWORD_REQUIRED.getMessage()));
        return this;
    }

    public UserRegistrationPage verifyRequireMessageAppearWithText(String expectedMessage){
        $(withText(expectedMessage)).shouldBe(appear);
        return this;
    }

    public UserRegistrationPage verifyFirstAndLastNameRequireMessageAppear(){
        $(withText(FIRST_NAME_REQUIRED.getMessage())).shouldBe(appear);
        $(withText(LAST_NAME_REQUIRED.getMessage())).shouldBe(appear);
        return this;
    }

    public UserRegistrationPage verifyPasswordRequiredMessageAppear() {
        passwordErrorLabel.shouldBe(visible).shouldHave(text(PASSWORD_REQUIRED.getMessage()));
        confirmPasswordErrorLabel.shouldBe(visible).shouldHave(text(PASSWORD_REQUIRED.getMessage()));
        return this;
    }

    public UserRegistrationPage verifyPasswordMatchingErrorMessageAppear(){
        confirmPasswordErrorLabel.shouldBe(visible).shouldHave(text(PASSWORDS_MATCHING_ERROR.getMessage()));
        return this;
    }

    public UserRegistrationPage verifyEmailRequireMessageAppear(){
        emailErrorLabel.shouldBe(visible).shouldHave(text(EMAIL_REQUIRED.getMessage()));
        return this;
    }
}