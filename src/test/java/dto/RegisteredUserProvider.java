package dto;

import api.Registration;
import api.RegistrationResponseHandler;
import dto.users.RegisteredUser;
import dto.users.User;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class RegisteredUserProvider {
    private static final Logger log = LoggerFactory.getLogger(RegisteredUserProvider.class);
    private final Registration registration = new Registration();
    private final RegistrationResponseHandler registrationHandler = new RegistrationResponseHandler(registration);

    public User getUser() {
        // Генерируем пользователя со случайными данными
        User randomUser = RandomUserProvider.getRandomUser();

        // Отправляем POST-запрос на создание пользователя
        Response postResponse = registration.sendRegisterUserRequest(randomUser);

        // Валидируем ответ POST-запроса и возвращаем респонс редиректа с результатом регистрации
        Response redirectedResponse = registrationHandler.handleRegistrationResponse(postResponse);

        String registrationResultMessage = registrationHandler.getRegistrationResultMessage(redirectedResponse);
        assertThat(registrationHandler.isRegistrationSuccessful(redirectedResponse)).isTrue();
        log.info("Пользователь успешно зарегистрирован. UserData: {}", randomUser);

        return RegisteredUser.Builder.with()
                .firstName(randomUser.getFirstName())
                .lastName(randomUser.getLastName())
                .email(randomUser.getEmail())
                .password(randomUser.getPassword())
                .cookies(redirectedResponse.getCookies())
                .headers(redirectedResponse.getHeaders())
                .registrationResult(registrationResultMessage)
                .build();
    }

//
//    @Test
//    void sniffing() {
//        User registeredUser = RegisteredUserProvider.getUser();
//        System.out.println(registeredUser);
//    }
}
