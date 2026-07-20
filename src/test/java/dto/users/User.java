package dto.users;

import dto.enums.Gender;

public class User {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private final Gender gender;

    protected User(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.password = builder.password;
        this.gender = builder.gender;
    }

    // Оставляю на случай создания пользователя динамически без Builder
    public User(String firstName, String lastName, String email, String password, Gender gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public Gender getGender() {
        return gender;
    }

    @Override
    public String toString(){
        return  "{ User = [Gender: " + gender +
                ",  FirstName: \"" + firstName +
                "\", LastName: \"" + lastName +
                "\", Email: \"" + email +
                "\", Password: \"" + password + // Намеренно: пароль в логах — только тестовое окружение!
                "\"] }";
    }

    // Изначально Builder был отдельным классом UserBuilder.
    // После переноса внутри User вызов превратился в: User.UserBuilder.builder.
    // Теперь цепочка вызовов стала интуитивнее: User.Builder.with().*.build().
    public static class Builder {
        protected String firstName;
        protected String lastName;
        protected String email;
        protected String password;
        protected Gender gender;

        public static Builder with() {
            return new Builder();
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public User build(){
            return new User(this);
        }
    }
}