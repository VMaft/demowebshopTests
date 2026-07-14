package dto.enums;

import java.util.Random;

public enum Gender {
    MALE("M"), FEMALE("F");

    private final static Random RANDOM = new Random();
    private final String apiValue;

    Gender(String apiValue) {
        this.apiValue = apiValue;
    }

    public String getApiValue() {
        return apiValue;
    }

    public static Gender getRandomGender(){
        return RANDOM.nextInt(2) == 0 ? Gender.MALE : Gender.FEMALE;
    }
}
