package dto;

import dto.enums.Gender;
import net.datafaker.Faker;

public class UserProvider {
    public static User createUserWith(String firstName, String lastName, String email, String password, Gender gender) {
        return new User(firstName, lastName, email, password, gender);
    }

    public static User createUserWith(String firstName, String lastName, String email, String password) {
        return new User(firstName, lastName, email, password);
    }

    public static User getBaseUser() {
        return createUserWith(
                "Alex",
                "Kromkich",
                "kromkich@mail.tst",
                "simplePass@123!",
                Gender.MALE);
    }

    public static User getBaseUserWithoutEmail() {
        User invalidUser = getBaseUser();
        invalidUser.setEmail("");
        return invalidUser;
    }

    public static User getBaseUserWithInvalidEmail() {
        User invalidUser = getBaseUser();
        invalidUser.setEmail("invalid@mail");
        return invalidUser;
    }

    public static User getBaseUserWithShortPassword() {
        User invalidUser = getBaseUser();
        invalidUser.setPassword("000");
        return invalidUser;
    }

    public static User getBaseUserWithoutPassword() {
        User invalidUser = getBaseUser();
        invalidUser.setPassword("");
        return invalidUser;
    }

    public static User getBaseUserWithoutFullName() {
        User invalidUser = getBaseUser();
        invalidUser.setFirstName("");
        invalidUser.setLastName("");
        return invalidUser;
    }

    public static User getRandomUser() {
        Faker faker = new Faker();
        return createUserWith(
                faker.name().firstName(),
                faker.funnyName().name(),
                faker.internet().emailAddress(),
                faker.credentials()
                        .password(
                                10,
                                15,
                                true,
                                true,
                                true
                        )

        );
    }

    public static User getRandomUserWithoutEmail(){
        User user = getRandomUser();
        user.setEmail("");
        return user;
    }

    public static User getRandomUserWithoutPassword(){
        User user = getRandomUser();
        user.setPassword("");
        return user;
    }

    public static User getRandomUserWithShortPassword(){
        User user = getRandomUser();
        user.setPassword(".");
        return user;
    }
}
