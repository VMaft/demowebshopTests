package dto;

import dto.enums.Gender;

// Теперь под каждый новый тестовый случай достаточно описать новый метод с редактированием
// одного конкретного параметра под кейс прямо тут!
public class DefaultUserProvider {
    private static final String FIRST_NAME = "Alex";
    private static final String LAST_NAME = "Kromkich";
    private static final String EMAIL = "kromkich@mail.tst";
    private static final String PASSWORD = "simplePass@123!";
    private static final Gender GENDER = Gender.MALE;

    // Вызов базового baseBuilder нужен для чистоты и минимизации копипасты параметров.
    public static User.Builder baseBuilder(){
        return User.Builder.with()
                .firstName(FIRST_NAME).lastName(LAST_NAME).email(EMAIL).password(PASSWORD).gender(GENDER);
    }

    public static User getDefaultUser() {
        return baseBuilder().build();
    }

    public static User getUserWithoutEmail() {
        return baseBuilder().email("").build();
    }

    public static User getUserWithInvalidEmail() {
        return baseBuilder().email("email_with_invalid@domain").build();
    }

    public static User getBaseUserWithShortPassword() {
        return baseBuilder().password("1").build();
    }

    public static User getUserWithEmptyPassword() {
        return baseBuilder().password("").build();
    }

    public static User getUserWithEmptyFirstAndLastName() {
        return baseBuilder().firstName("").lastName("").build();
    }
}