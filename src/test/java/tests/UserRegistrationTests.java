package tests;

import dto.DefaultUserProvider;
import dto.RandomUserProvider;
import dto.User;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pages.UserRegistrationPage;
import utils.Attachments;

import static io.qameta.allure.Allure.step;

@DisplayName("Проверки регистрации пользователей на сайте")
@Feature("Проверки регистрации пользователей через UI")
public class UserRegistrationTests extends BaseTest {

    UserRegistrationPage page = new UserRegistrationPage();

    @Test()
    @DisplayName("Пользователь может пройти регистрацию на сайте")
    @Tags({@Tag("UI tests"), @Tag("RegisterForm"), @Tag("Positive tests")})
    void userCanBeRegister() {
        User user = RandomUserProvider.getRandomUser();
        Attachments.saveTestUserDataToParameters(user);

        step("Открываем форму регистрации", page::open);
        step("Проверяем что форма регистрации пользователя валидна", page::validateRegistrationForm);
        step("Заполняем поля формы регистрации данными пользователя", () -> {
            page.fillRegistrationFormWithUserData(user);
        });
        step("Нажимаем кнопку регистрации", page::clickRegisterButton);
        step("Проверяем сообщение об успехе", page::verifySuccessRegistration);
        step("Нажимаем продолжить", page::clickContinue);
        step("Проверяем наличие ссылки на профиль пользователя", () -> {
            page.shouldContainsProfileLinkWithEmail(user.getEmail());
        });
        step("Переходим в профиль пользователя по ссылке", page::clickAccountLink);
        step("Проверяем что в профиле пользователя отображаются валидные данные", () -> {
            page.verifyUserProfileContainsValidUserData(user);
        });
    }

    @Test
    @DisplayName("Проверка обязательных полей формы регистрации пользователей")
    @Tags({@Tag("UI tests"), @Tag("RegisterForm"), @Tag("Negative tests")})
    void checkingRequiredFieldsOfRegistrationForm() {
        User user = DefaultUserProvider.getDefaultUser();
        Attachments.saveTestUserDataToParameters(user);

        step("Открываем форму регистрации", page::open);
        step("Проверяем что форма регистрации пользователя валидна", page::validateRegistrationForm);
        step("Нажимаем кнопку регистрации", page::clickRegisterButton);
        step("Проверяем сообщение об обязательном заполнении полей формы", () -> {
            page.verifyRequiredFieldsMessagesAppear();
        });
    }

    @MethodSource("tests.data.sources.UserRegistrationTestDataProvider#invalidUserDataAndExpectedRequireMessages")
    @Tags({@Tag("UI tests"), @Tag("RegisterForm"), @Tag("Negative tests")})
    @DisplayName("Валидация полей.")
    @ParameterizedTest(name = "{index}. Пользователь {0} не может быть зарегистрирован.")
    void userCanNotBeRegisteredWithoutRequiredData(String caseName, String requiredDataMessage, User user) {
        step("Открываем форму регистрации", page::open);
        step("Заполняем поля формы регистрации данными пользователя", () -> {
            page.fillRegistrationFormWithUserData(user);
        });
        step("Нажимаем кнопку регистрации", page::clickRegisterButton);
        step("Проверяем наличие ошибки с текстом: " + requiredDataMessage, () -> {
            page.verifyRequireMessageAppearWithText(requiredDataMessage);
        });
    }

    @Test
    @DisplayName("Пользователь не может быть зарегистрирован без подтверждения пароля")
    @Tags({@Tag("UI tests"), @Tag("RegisterForm"), @Tag("Negative tests")})
    void userCanNotBeRegisteredWithoutPasswordConfirm() {
        User user = DefaultUserProvider.getDefaultUser();
        Attachments.saveTestUserDataToParameters(user);

        step("Открываем форму регистрации", page::open);
        step("Заполняем поля формы регистрации данными пользователя", () -> {
            page.setFirstName(user.getFirstName())
                    .setLastName(user.getLastName())
                    .setEmail(user.getEmail())
                    .setPassword(user.getPassword());
        });
        step("Вводим email пользователя вместо пароля в поле подтверждения пароля", () -> {
            page.confirmPasswordWith(user.getEmail());
        });
        step("Нажимаем клавишу Tab для смены фокуса", page::clickTabKey);
        step("Проверяем сообщение о несовпадении паролей", page::verifyPasswordMatchingErrorMessageAppear);
        step("Нажимаем кнопку регистрации", page::clickRegisterButton);
        step("Сообщение о несовпадении паролей все еще отображается",
                page::verifyPasswordMatchingErrorMessageAppear);
    }
}
