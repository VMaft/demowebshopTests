package dto;

import dto.enums.Gender;
import net.datafaker.Faker;

public class RandomUserProvider {
    private static final Faker faker = new Faker();
    private static final String EMAIL_DOMAIN = "@autotests.user";

    public static User getRandomUser() {
        Gender gender = Gender.getRandomGender();
        String firstName = gender.equals(Gender.MALE) ? faker.name().maleFirstName() : faker.name().femaleFirstName();
        String lastName = faker.funnyName().name();
        String emailName = firstName + "." + lastName;
        String email = emailName.replace(" ", "_") + EMAIL_DOMAIN;
        String password = faker.credentials()
                .password(
                        12,
                        19,
                        true,
                        true,
                        true
                );
        return new User(firstName, lastName, email, password, gender);
    }

    public static User getRandomUserWithoutEmail() {
        User randomUser = getRandomUser();
        return new User(
                randomUser.getFirstName(),
                randomUser.getLastName(),
                "",
                randomUser.getPassword(),
                randomUser.getGender()
        );
    }

    public static User getRandomUserWithoutPassword() {
        User randomUser = getRandomUser();
        return new User(
                randomUser.getFirstName(),
                randomUser.getLastName(),
                randomUser.getEmail(),
                "",
                randomUser.getGender()
        );
    }

    public static User getRandomUserWithShortPassword() {
        User randomUser = getRandomUser();
        return new User(
                randomUser.getFirstName(),
                randomUser.getLastName(),
                randomUser.getEmail(),
                "1",
                randomUser.getGender()
        );
    }
}
