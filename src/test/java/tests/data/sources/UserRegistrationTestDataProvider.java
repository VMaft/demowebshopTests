package tests.data.sources;

import dto.DefaultUserProvider;
import org.junit.jupiter.params.provider.Arguments;
import tests.data.enums.ValidationErrors;
import java.util.stream.Stream;

public class UserRegistrationTestDataProvider {
    private static Stream<Arguments> invalidUserDataAndExpectedRequireMessages() {
        return Stream.of(
                Arguments.of("без Имени", ValidationErrors.FIRST_NAME_REQUIRED.getMessage(),
                        DefaultUserProvider.getUserWithEmptyFirstName()),
                Arguments.of("без Фамилии", ValidationErrors.LAST_NAME_REQUIRED.getMessage(),
                        DefaultUserProvider.getUserWithEmptyLastName()),
                Arguments.of("без Email", ValidationErrors.EMAIL_REQUIRED.getMessage(),
                        DefaultUserProvider.getUserWithoutEmail()),
                Arguments.of("с невалидным Email", ValidationErrors.WRONG_EMAIL.getMessage(),
                        DefaultUserProvider.getUserWithInvalidEmail()),
                Arguments.of("без Пароля", ValidationErrors.PASSWORD_REQUIRED.getMessage(),
                        DefaultUserProvider.getUserWithEmptyPassword()),
                Arguments.of("с невалидным паролем", ValidationErrors.SHORT_PASSWORDS.getMessage()
                        , DefaultUserProvider.getUserWithShortPassword())
        );
    }
}