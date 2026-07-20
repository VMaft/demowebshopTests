package dto.users;

import dto.enums.Gender;
import io.restassured.http.Headers;

import java.util.Map;

public class RegisteredUser extends User {
    private final Map<String, String> cookies;
    private final Headers headers;
    private final String registrationResult;

    public RegisteredUser(Builder builder){
        super(builder);
        this.cookies = builder.cookies;
        this.headers = builder.headers;
        this.registrationResult = builder.registrationResult;
    }

    public RegisteredUser(
            String firstName,
            String lastName,
            String email,
            String password,
            Gender gender,
            Map<String, String> cookies,
            Headers headers,
            String registrationResult
    ) {
        super(firstName, lastName, email, password, gender);
        this.cookies = cookies;
        this.headers = headers;
        this.registrationResult = registrationResult;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getRegistrationResult() {
        return registrationResult;
    }

    @Override
    public String toString() {
        return "{ RegisteredUser = [Gender: " + getGender() +
                ", FirstName: \"" + getFirstName() +
                "\", LastName: \"" + getLastName() +
                "\", Email: \"" + getEmail() +
                "\", Password: \"" + getPassword() +
                "\", Cookies: \"" + cookies +
                "\", Response Headers: \"" + headers +
                "\", Registration result: \"" + registrationResult +
                "\"] }";
    }

    public static class Builder extends User.Builder {
        private Map<String, String> cookies;
        private Headers headers;
        private String registrationResult;

        public static Builder with(){
            return new Builder();
        }

        public Builder firstName(String firstName) {
            super.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            super.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            super.email = email;
            return this;
        }

        public Builder password(String password) {
            super.password = password;
            return this;
        }

        public Builder gender(Gender gender) {
            super.gender = gender;
            return this;
        }

        public Builder cookies(Map<String, String> cookies) {
            this.cookies = cookies;
            return this;
        }

        public Builder headers(Headers responseHeaders) {
            this.headers = headers;
            return this;
        }

        public Builder registrationResult(String registrationResult) {
            this.registrationResult = registrationResult;
            return this;
        }

        public RegisteredUser build(){
            return new RegisteredUser(this);
        }
    }
}
