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

    public static User getBaseUser() {
        return baseBuilder().build();
    }

    public static User getBaseUserWithoutEmail() {
        return baseBuilder().email("").build();
    }

    public static User getBaseUserWithInvalidEmail() {
        return baseBuilder().email("email_with_invalid@domain").build();
    }

    public static User getBaseUserWithShortPassword() {
        return baseBuilder().password("1").build();
    }

    public static User getBaseUserWithEmptyPassword() {
        return baseBuilder().password("").build();
    }

    public static User getBaseUserWithEmptyFirstAndLastName() {
        return baseBuilder().firstName("").lastName("").build();
    }
}