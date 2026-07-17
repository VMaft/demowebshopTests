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
    //private static final String REGISTER_RESULT_URL = "https://demowebshop.tricentis.com/registerresult/1";
    private String csrfToken;
    private Map<String, String> cookies = new HashMap<>();

    RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();


    // В ответ на GET запрос возвращается html страница.
    // Результат запроса используется для актуализации и парсинга CSRF токена для последующих вызовов
    private Response sendGetRequest() {
        return given()
                .baseUri(REGISTER_PAGE_URL)
                .get()
                .then()
                .statusCode(200)
                .extract().response();
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

    private void initSessionSpecBuilder() {
        Response getRequestResponse = sendGetRequest();
        requestSpecBuilder.addCookies(getRequestResponse.getCookies());
        log.info("Установлены Cookie сессии. Cookies: {}", getRequestResponse.getCookies());

        requestSpecBuilder.addHeader("ContentType", "application/x-www-form-urlencoded");
        requestSpecBuilder.addHeader("Origin", "https://demowebshop.tricentis.com");
        requestSpecBuilder.addHeader("Referer", "https://demowebshop.tricentis.com/register");

        csrfToken = getCSRFTokenFrom(getRequestResponse);
        log.info("Получен CSRF-токен. Token:{}", csrfToken);
    }

    private void initSessionManual() {
        Response getRequestResponse = sendGetRequest();

        cookies.putAll(getRequestResponse.getCookies());
        log.info("Установлены Cookie сессии. Cookies: {}", cookies);

        csrfToken = getCSRFTokenFrom(getRequestResponse);
        log.info("Получен CSRF-токен. Token:{}", csrfToken);
    }

    public Response sendRegisterUserRequest(User user) {
        initSessionManual();

        Response response = given()
                .baseUri(REGISTER_PAGE_URL)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("Accept-Encoding", "gzip, deflate, br, zstd")
                .header("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                .header("Cache-Control", "max-age=0")
                .header("Connection", "keep-alive")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Host", "demowebshop.tricentis.com")
                .header("Origin", BASE_URL)
                .header("Referer", REGISTER_PAGE_URL)
                .header("Upgrade-Insecure-Requests", "1")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36")
                .cookies(cookies)
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
                .log().headers()
                .log().body()
                .extract().response();

        assertNotNull(response);
        logResponseInfo(response);
        //System.out.println("Усё?!");

        if (response.statusCode() == 200){
            log.info("Получен статус код {}", response.statusCode());
            log.info("Получен результат выполнения запроса. {}", response.statusCode());

            return response;
        }

        String location = response.getHeader("Location");
        System.out.println("Редирект на: " + location);  // /registerresult/1

        // 3. Делаем GET запрос по новому URL
        if (location != null) {
            return given()
                    .cookies(response.getCookies())  // ← Важно: передаем куки!
                    .when()
                    .get(BASE_URL + location)  // ← Используем полный URL
                    .then()
                    .statusCode(200)
                    .extract().response();
        }

        return response;
    }


    private void logResponseInfo(Response response) {
        log.info("Received user session Cookies: \n{}\n", response.getCookies());
        log.info("Response Headers: \n{}\n", response.getHeaders());
        //log.info("Response Body: \n{}\n", response.getBody().asString());
    }

    private String getRegisterValidationError(Response response) {
        Document pageDocument = Jsoup.parse(response.getBody().asString());
        Element errorBlock = pageDocument.select(".validation-summary-errors").first();

        if(errorBlock != null){
            return errorBlock.text();
        }

        return null;
    }


    @Test
    void sniffing() {
        User paposhkaUser = User.Builder.with()
                .firstName("Paposhka")
                .lastName("Gjordich")
                .email("paposhka_gjordich4@autotests.user")
                .password("Gjordich123")
                .build();

        Response response = sendRegisterUserRequest(paposhkaUser);

        assertThat(getRegisterValidationError(response)).isNull();
        assertTrue(response.getBody().asString().contains("Your registration completed"));

        //getRegisterResultRequest(response);
    }
}
