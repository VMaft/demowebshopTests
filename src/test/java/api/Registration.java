package api;

import config.DemoWebShopConfiguration;
import dto.users.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Registration {
    private static final Logger log = LoggerFactory.getLogger(Registration.class);

    private static final String BASE_URL = DemoWebShopConfiguration.BASE_URL;
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
                .setBaseUri(BASE_URL)
                .addCookies(initRequestResponse.getCookies())
                .addHeaders(getDefaultHeaders());

        log.info("Сессия инициализирована. Установлены Cookie: {}", initRequestResponse.getCookies());
    }

    private Response sendGetRequest() {
        return sendGetRequest(REGISTER_ENDPOINT, new HashMap<>());
    }

    public Response sendGetRequest(String endpoint, Map<String, String> cookies) {
        return given()
                .cookies(cookies != null ? cookies : new HashMap<>())
                .baseUri(BASE_URL)
                .get(endpoint)
                .then()
                .statusCode(200)
                .extract().response();
    }

    public Response sendRegisterUserRequest(User user) {
        if (requestSpecBuilder == null) {
            initSession();
        }
        return given()
                .spec(requestSpecBuilder.build())
                .formParam("__RequestVerificationToken", csrfToken)
                .formParam("FirstName", user.getFirstName())
                .formParam("LastName", user.getLastName())
                .formParam("Email", user.getEmail())
                .formParam("Password", user.getPassword())
                .formParam("ConfirmPassword", user.getPassword())
                .formParam("register-button", "Register")
                .when()
                .post(REGISTER_ENDPOINT)
                .then()
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
}