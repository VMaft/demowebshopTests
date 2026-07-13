package tests;

import dto.RandomUserProvider;
import dto.User;
import dto.BaseUserProvider;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pages.UserRegistrationPage;
import utils.Attachments;

import static io.qameta.allure.Allure.step;

@DisplayName("Проверки регистрации пользователей на сайте")
@Feature("Проверки регистрации пользователей через UI")
public class UserRegistrationPageTests extends BaseTest {

    UserRegistrationPage page = new UserRegistrationPage();

    @Test
    @DisplayName("Пользователь может пройти регистрацию на сайте")
    @Tag("RegisterForm")
    void userCanBeRegister() {
        step("Создаем тестового пользователя");
        User user = RandomUserProvider.getRandomUser();
        step("Сохраняем данные пользователя в параметры теста", ()-> {
            Attachments.addTestUserToParameters(user);
            System.out.println(user);
        });
        step("Открываем форму регистрации", page::open);
        step("Проверяем что форма регистрации пользователя валидна", page::validateRegistrationForm);
        step("Заполняем поля формы регистрации данными пользователя", ()-> {
            page.fillRegistrationFormWithUserData(user);
        });
        step("Нажимаем кнопку регистрации", page::clickRegisterButton);
        step("Проверяем сообщение об успехе", page::verifySuccessRegistration);
        step("Нажимаем продолжить", page::clickContinue);
        step("Проверяем наличие ссылки на профиль пользователя", ()-> {
            page.shouldContainsProfileLinkWithEmail(user.getEmail());
        });
        step("Переходим в профиль пользователя по ссылке", page::clickAccountLink);
        step("Проверяем что в профиле пользователя отображаются валидные данные", ()-> {
            page.verifyUserProfileContainsValidUserData(user);
        });
    }
}
