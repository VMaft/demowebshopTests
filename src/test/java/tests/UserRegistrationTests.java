package tests;

import dto.DefaultUserProvider;
import dto.RandomUserProvider;
import dto.User;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
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

    @Test
    @DisplayName("Пользователь не может быть зарегистрирован без пароля")
    @Tags({@Tag("UI tests"), @Tag("RegisterForm"), @Tag("Negative tests")})
    void userCanNotBeRegisteredWithoutPassword() {
        User user = DefaultUserProvider.getUserWithEmptyPassword();
        Attachments.saveTestUserDataToParameters(user);

        step("Открываем форму регистрации", page::open);
        step("Заполняем поля формы регистрации данными пользователя", () -> {
            page.fillRegistrationFormWithUserData(user);
        });
        step("Нажимаем кнопку регистрации", page::clickRegisterButton);
        step("Проверяем сообщение об успехе", page::verifyPasswordRequiredMessageAppear);
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

    @Test
    @DisplayName("Пользователь без Имени и Фамилии не может быть зарегистрирован")
    @Tags({@Tag("UI tests"), @Tag("RegisterForm"), @Tag("Negative tests")})
    @Description("В целях оптимизации проверяем поведение полей Имени и Фамилии одновременно")
    void userWithoutFirstNameAndLastNameCanNotBeRegistered() {
        User user = DefaultUserProvider.getUserWithEmptyFirstAndLastName();
        Attachments.saveTestUserDataToParameters(user);

        step("Открываем форму регистрации", page::open);
        step("Заполняем поля формы регистрации данными тестового пользователя", () -> {
            page.fillRegistrationFormWithUserData(user);
        });
        step("Нажимаем кнопку регистрации", page::clickRegisterButton);
        step("Проверяем сообщение об обязательности полей Имени и Фамилии",
                page::verifyFirstAndLastNameRequireMessageAppear);
    }

    @Test
    @DisplayName("Пользователь без email не может быть зарегистрирован")
    @Tags({@Tag("UI tests"), @Tag("RegisterForm"), @Tag("Negative tests")})
    void userWithoutEmailCanNotBeRegistered() {
        User user = DefaultUserProvider.getUserWithoutEmail();
        Attachments.saveTestUserDataToParameters(user);

        step("Открываем форму регистрации", page::open);
        step("Заполняем поля формы регистрации данными тестового пользователя", () -> {
            page.fillRegistrationFormWithUserData(user);
        });
        step("Нажимаем кнопку регистрации", page::clickRegisterButton);
        step("Проверяем сообщение об обязательности Email", page::verifyEmailRequireMessageAppear);
    }

    @Test
    @DisplayName("Пользователь c невалидным email не может быть зарегистрирован")
    @Tags({@Tag("UI tests"), @Tag("RegisterForm"), @Tag("Negative tests")})
    void userWithInvalidEmailCanNotBeRegistered() {
        User user = DefaultUserProvider.getUserWithInvalidEmail();
        Attachments.saveTestUserDataToParameters(user);

        step("Открываем форму регистрации", page::open);
        step("Заполняем поля формы регистрации данными тестового пользователя", () -> {
            page.fillRegistrationFormWithUserData(user);
        });
        step("Нажимаем клавишу Tab для смены фокуса", page::clickTabKey);
        step("Проверяем сообщение о неправильном email", page::verifyWrongEmailMessageAppear);
        step("Нажимаем кнопку регистрации", page::clickRegisterButton);
        step("Сообщение о неправильном email все еще отображается", page::verifyWrongEmailMessageAppear);
    }

    @Test
    @DisplayName("Пользователь c коротким паролем не может быть зарегистрирован")
    @Tags({@Tag("UI tests"), @Tag("RegisterForm"), @Tag("Negative tests")})
    void userWithShortPasswordCanNotBeRegistered() {
        User user = DefaultUserProvider.getBaseUserWithShortPassword();
        Attachments.saveTestUserDataToParameters(user);

        step("Открываем форму регистрации", page::open);
        step("Заполняем поля формы регистрации данными тестового пользователя", () -> {
            page.fillRegistrationFormWithUserData(user);
        });
        step("Проверяем сообщение о коротком пароле", page::verifyShortPasswordMessageAppear);
        step("Нажимаем кнопку регистрации", page::clickRegisterButton);
        step("Сообщение о коротком пароле все еще отображается", page::verifyShortPasswordMessageAppear);
    }
}
