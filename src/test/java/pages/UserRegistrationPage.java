package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import dto.User;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;

public class UserRegistrationPage {
    final String ENDPOINT = "/register";
    final String VALIDATION_ERROR_CLASS = ".field-validation-error";
    final String LABELS_SELECTOR = "label";
    final String[] LABELS_EXPECTED_TEXTS = {
            "Gender:", "First name:", "Last name:", "Email:", "Password:", "Confirm password:", "Male", "Female"
    };

    final SelenideElement genderMale = $("#gender-male");
    final SelenideElement genderFemale = $("#gender-female");
    final SelenideElement firstName = $("#FirstName");
    final SelenideElement lastName = $("#LastName");
    final SelenideElement email = $("#Email");
    final SelenideElement password = $("#Password");
    final SelenideElement confirmPassword = $("#ConfirmPassword");
    final SelenideElement registerButton = $("#register-button");

    final SelenideElement firstNameErrorLabel = $(VALIDATION_ERROR_CLASS).$("[for='FirstName']");
    final SelenideElement lastNameErrorLabel = $(VALIDATION_ERROR_CLASS).$("[for='LastName']");
    final SelenideElement emailErrorLabel = $(VALIDATION_ERROR_CLASS).$("[for='Email']");
    final SelenideElement passwordErrorLabel = $(VALIDATION_ERROR_CLASS).$("[for='Password']");
    final SelenideElement confirmPasswordErrorLabel = $(VALIDATION_ERROR_CLASS).$("[for='ConfirmPassword']");

    WebElementCondition[] valid = {enabled, visible, clickable};

    public UserRegistrationPage open() {
        Selenide.open(ENDPOINT);
        return this;
    }

    public UserRegistrationPage clickRegisterButton() {
        registerButton.click();
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

    public UserRegistrationPage setGender(String gender) {
        if (!gender.contains("M") || !gender.contains("F")) {
            System.err.printf(
                    String.format("Не передано значение аргумента для поля gender. Получено значение: 'gender':[%s]",
                            gender
                    )
            );
        }
        if (gender.isBlank()) {
            System.out.println("WARN: Не передано значение gender. Выбор gender будет пропущен.");
            return this;
        }

        if (gender.contains("M")) genderMale.click();
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

    public UserRegistrationPage confirmPassword(String passwordValue) {
        setFieldValue(confirmPassword, passwordValue);
        return this;
    }

    public UserRegistrationPage fillRegistrationFormWithUserData(User user) {
        if (user.getGender() != null) setGender(user.getGender().toString());
        setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .confirmPassword(user.getPassword());
        return this;
    }

    private void setFieldValue(SelenideElement field, String fieldValue) {
        String fieldName = field.getAttribute("name");
        if (fieldName == null) fieldName = "unknown";

        if (fieldValue.isBlank()) {
            throw new IllegalArgumentException(
                    String.format("Не передано значение аргумента для поля. Получено значение: '%s':[%s]"
                            , fieldName, fieldValue
                    )
            );
        }
        field.setValue(fieldValue);
        field.shouldHave(Condition.value(fieldValue));
    }

}