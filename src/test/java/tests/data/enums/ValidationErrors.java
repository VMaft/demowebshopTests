package tests.data.enums;

public enum ValidationErrors {
    FIRST_NAME_REQUIRED ("First name is required."),
    LAST_NAME_REQUIRED ("Last name is required."),
    EMAIL_REQUIRED ("Email is required."),
    WRONG_EMAIL ("Wrong email"),
    PASSWORD_REQUIRED ("Password is required."),
    SHORT_PASSWORDS ("The password should have at least 6 characters."),
    PASSWORDS_MATCHING_ERROR ("The password and confirmation password do not match."),
    EMAIL_SPECIFIED_ERROR("The specified email already exists");

    private final String messageText;

    ValidationErrors(String messageText) {
        this.messageText = messageText;
    }

    public String getMessage(){
        return messageText;
    }
}
