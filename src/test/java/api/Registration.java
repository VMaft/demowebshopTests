package api;

import dto.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.UserRegistrationPage;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Registration {
    private static final Logger log = LoggerFactory.getLogger(UserRegistrationPage.class);

    private static final String BASE_URL = "https://demowebshop.tricentis.com";
    private static final String REGISTER_ENDPOINT = "/register";
    private static final String REGISTER_PAGE_URL = BASE_URL + REGISTER_ENDPOINT;

    private String csrfToken;
    private RequestSpecBuilder requestSpecBuilder;

    private Map<String, String> getDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();

        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        headers.put("Accept-Encoding", "gzip, deflate, br, zstd");
        headers.put("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");
        headers.put("Cache-Control", "max-age=0");
        headers.put("Connection", "keep-alive");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Host", "demowebshop.tricentis.com");
        headers.put("Origin", BASE_URL);
        headers.put("Referer", REGISTER_PAGE_URL);
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36");

        return headers;
    }

    private void initSession() {
        Response initRequestResponse = sendGetRequest();
        csrfToken = getCSRFTokenFrom(initRequestResponse);

        if (log.isInfoEnabled()) {
            log.info("Получен CSRF-токен. Token:{}", csrfToken);
        }

        requestSpecBuilder = new RequestSpecBuilder()
                .setBaseUri(REGISTER_PAGE_URL)
                .addCookies(initRequestResponse.getCookies())
                .addHeaders(getDefaultHeaders());

        log.info("Установлены Cookie сессии. Cookies: {}", initRequestResponse.getCookies());
    }

    private Response sendGetRequest() {
        return sendGetRequest(REGISTER_PAGE_URL, new HashMap<>());
    }

    private Response sendGetRequest(String url, Map<String, String> cookies) {
        return given()
                .cookies(cookies != null ? cookies : new HashMap<>())
                .baseUri(url)
                .get()
                .then()
                .statusCode(200)
                .extract().response();
    }

    public Response sendRegisterUserRequest(User user) {
        if (requestSpecBuilder == null) {
            initSession();
        }

        Response response = given()
                .spec(requestSpecBuilder.build())
                .formParam("__RequestVerificationToken", csrfToken)
                .formParam("FirstName", user.getFirstName())
                .formParam("LastName", user.getLastName())
                .formParam("Email", user.getEmail())
                .formParam("Password", user.getPassword())
                .formParam("ConfirmPassword", user.getPassword())
                .formParam("register-button", "Register")
                .when()
                .post()
                .then()
                .extract().response();
        assertNotNull(response);

        if (response.statusCode() == 200) {
            log.info("Получен статус код {}", response.statusCode());
            log.info("Проверяем наличие ошибок. Validation summary errors: {}", getRegisterValidationError(response));

            logResponseInfo(response);
            return response;
        }

        if (response.statusCode() == 302) {
            String location = response.getHeader("Location");
            System.out.println("Редирект на: " + location);  // /registerresult/1

            if (location != null) {
                return sendGetRequest(BASE_URL + location, response.getCookies());
            } else {
                log.warn("Не получена редирект-ссылка.");
                logResponseInfo(response);
            }
        } else {
            log.info("Получен статус код {}", response.statusCode());
            log.info("С телом ответа. Response body:{}", response.getBody().asString());
        }

        return response;
    }

    private void logResponseInfo(Response response) {
        log.info("Received user session Cookies: \n{}\n", response.getCookies());
        log.info("Response Headers: \n{}\n", response.getHeaders());
        log.info("Response Body: \n{}\n", response.getBody().asString());
    }

    private String getRegisterValidationError(Response response) {
        Document pageDocument = Jsoup.parse(response.getBody().asString());
        Element errorBlock = pageDocument.select(".validation-summary-errors").first();

        if (errorBlock != null) {
            return errorBlock.text();
        }

        return null;
    }

    // Токен CSRF генерируется сервером автоматически и отправляется в ответ на Get запрос в виде HTML-кода.
    private String getCSRFTokenFrom(Response response) {
        // Извлекаем HTML из респонса
        String htmlResponse = response.getBody().asString();
        Document pageDocument = Jsoup.parse(htmlResponse);

        Element __requestVerificationToken = pageDocument
                .select("input[name='__RequestVerificationToken']").first();

        assertNotNull(__requestVerificationToken);
        return __requestVerificationToken.attr("value");
    }

    @Test
    void sniffing() {
        User paposhkaUser = User.Builder.with()
                .firstName("Paposhka")
                .lastName("Gjordich")
                .email("paposhka_gjordich8@autotests.user")
                .password("Gjordich123")
                .build();

        Response response = sendRegisterUserRequest(paposhkaUser);

        assertThat(getRegisterValidationError(response)).isNull();
        assertThat(response.getBody().asString()).contains("Your registration completed");
    }
}
