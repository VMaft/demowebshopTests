package dto;

import dto.enums.Gender;

public class BaseUserProvider {
    private static final String FIRST_NAME = "Alex";
    private static final String LAST_NAME = "Kromkich";
    private static final String EMAIL = "kromkich@mail.tst";
    private static final String PASSWORD = "simplePass@123!";
    private static final Gender GENDER = Gender.MALE;

    public static User getBaseUser() {
        return new User(
                FIRST_NAME,
                LAST_NAME,
                EMAIL,
                PASSWORD,
                GENDER
        );
    }

    public static User getBaseUserWithoutEmail() {
        return new User(
                FIRST_NAME,
                LAST_NAME,
                "",
                PASSWORD,
                GENDER
        );
    }

    public static User getBaseUserWithInvalidEmail() {
        return new User(
                FIRST_NAME,
                LAST_NAME,
                "invalid@mail",
                PASSWORD,
                GENDER
        );
    }

    public static User getBaseUserWithShortPassword() {
        return new User(
                FIRST_NAME,
                LAST_NAME,
                EMAIL,
                "000",
                GENDER
        );
    }

    public static User getBaseUserWithEmptyPassword() {
        return new User(
                FIRST_NAME,
                LAST_NAME,
                EMAIL,
                "",
                GENDER
        );
    }

    public static User getBaseUserWithoutFullName() {
        return new User(
                "",
                "",
                EMAIL,
                PASSWORD,
                GENDER
        );
    }
}
