package tests;

import dto.User;
import dto.UserProvider;
import org.junit.jupiter.api.Test;
import pages.UserRegistrationPage;
import utils.Attachments;

import static io.qameta.allure.Allure.step;

public class UserRegistrationPageTests extends BaseTest {

    UserRegistrationPage page = new UserRegistrationPage();

    @Test
    void userCanBeRegister() {
        step("Создаем тестового пользователя");
        User user = UserProvider.getBaseUser();
        step("Сохраняем данные пользователя в параметры теста", ()-> {
            Attachments.addTestUserToParameters(user);
        });
        step("Открываем форму регистрации", page::open);
        step("Проверяем что форма регистрации пользователя валидна", page::validateRegistrationForm);
        step("Заполняем поля формы регистрации данными пользователя", ()-> {
            page.fillRegistrationFormWithUserData(user);
        });
        System.out.println();
        step("Нажимаем кнопку регистрации", page::clickRegisterButton);

        System.out.println();
    }
}
