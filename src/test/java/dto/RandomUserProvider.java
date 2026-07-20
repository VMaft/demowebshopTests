package dto;

import dto.enums.Gender;
import dto.users.User;
import net.datafaker.Faker;

import static org.assertj.core.api.Assertions.assertThat;

// Провайдер RandomUser для генерации случайных пользовательских данных для чистой регистрации
// Точная гарантия того что регистрация на сайте пройдет
public class RandomUserProvider {
    private static final Faker faker = new Faker();
    private static final String EMAIL_DOMAIN = "@autotests.user";

    // Каждый вызов = случайные данные
    public static User.Builder baseRandomUserBuilder() {
        Gender randomGender = Gender.getRandomGender();
        String randomFirstName =
                randomGender.equals(Gender.MALE) ? faker.name().maleFirstName() : faker.name().femaleFirstName();
        String randomLastName = faker.funnyName().name();

        String emailName = randomFirstName.toLowerCase() + "." + randomLastName.toLowerCase();
        String randomEmail = getRandomEmail(emailName);

        String randomPassword = faker.credentials()
                .password(
                        12, // Минимальная длина
                        19,     // Максимальная длина
                        true,   // Верхний регистр
                        true,   // Спец.символы
                        true    // Цифры
                );
        return User.Builder.with()
                .firstName(randomFirstName)
                .lastName(randomLastName)
                .email(randomEmail)
                .password(randomPassword)
                .gender(randomGender);
    }

    private static String getRandomEmail(String emailName) {
        // Защита от вероятных спец.символов в имени пользователя
        return (emailName.replaceAll("[^\\p{Alnum}]", "_")
                + EMAIL_DOMAIN).toLowerCase();
    }

    public static User getRandomUser() {
        return baseRandomUserBuilder().build();
    }

    public static User getRandomUserWithoutEmail() {
        return baseRandomUserBuilder().email("").build();
    }

    public static User getRandomUserWithEmail(String email) {
        return baseRandomUserBuilder().email(email).build();
    }

    public static User getRandomUserWithInvalidEmail() {
        return baseRandomUserBuilder().email("email_with_invalid@domain").build();
    }

    public static User getRandomUserWithoutPassword() {
        return baseRandomUserBuilder().password("").build();
    }

    public static User getRandomUserWithShortPassword() {
        return baseRandomUserBuilder().password("1").build();
    }

    // Просто потому что!
    public static User getRandomUserWithLongName() {
        String longFirstName = faker.name().prefix()
                + faker.name().maleFirstName()
                + faker.name().nameWithMiddle()
                + faker.name().maleFirstName()
                + faker.name().title();
        String longEmail = getRandomEmail(longFirstName);

        return baseRandomUserBuilder().firstName(longFirstName).email(longEmail).build();
    }
}