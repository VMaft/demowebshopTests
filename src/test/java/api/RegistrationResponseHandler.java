package api;

import io.restassured.response.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistrationResponseHandler {
    private static final Logger log = LoggerFactory.getLogger(RegistrationResponseHandler.class);

    private final Registration registration;

    public RegistrationResponseHandler(Registration registration) {
        this.registration = registration;
    }

    public Response handleRegistrationResponse(Response response) {
        log.info("Получен ответ на запрос регистрации пользователя со статусом: {}", response.statusCode());
        log.info("Инициирована обработка ответа");
        switch (response.statusCode()) {
            case 200: {
                return handleFunctionalError(response);
            }
            case 302: {
                return handleRedirect(response);
            }
            case 400: {
                log.info("Получен Bad Request. Лог ответа сервера:");
                logResponseInfo(response);
                return response;
            }
            default: {
                log.info("Получен непредвиденный ответ от сервера. Status-code:{}", response.getStatusCode());
                logResponseInfo(response);
                return response;
            }
        }
    }

    private Response handleRedirect(Response response) {
        String redirectEndpoint = response.getHeader("Location");
        if (redirectEndpoint == null || redirectEndpoint.isEmpty()) {
            log.warn("Не передана редирект ссылка.");
            return response;
        }
        log.info("Перехожу по редирект-ссылке: /{}", redirectEndpoint);
        return registration.sendGetRequest(redirectEndpoint, response.getCookies());
    }

    // Статус-код 200 на запрос регистрации пользователя возвращает детализированную ошибку регистрации
    public Response handleFunctionalError(Response response) {
        String validationError = searchValidRegisterError(response);
        log.info("Проверяем наличие ошибок. Validation summary errors: \"{}\"", validationError);

        if (validationError == null) {
            log.info("Получена непредвиденная ошибка. Параметры ответа:");
            logResponseInfo(response);
        }
        return response;
    }

    public boolean isRegistrationSuccessful(Response response) {
        if (response.getStatusCode() == 200) {
            String body = response.getBody().asString();
            return body.contains("Your registration completed");
        }
        return false;
    }

    private String searchValidRegisterError(Response response) {
        Document pageDocument = Jsoup.parse(response.getBody().asString());

        // Ищем валидные ошибки в теле ответа запроса.
        Element errorBlock = pageDocument.select(".validation-summary-errors").first();
        if (errorBlock != null) {
            return errorBlock.text();
        } else {
            String errorsTexts = pageDocument.select("[^~=(?i).*error.*]").text();
            if (!errorsTexts.isBlank()) return errorsTexts;
        }
        return null;
    }

    private void logResponseInfo(Response response) {
        log.info("Status: {}", response.getStatusCode());
        log.info("Cookies: {}", response.getCookies());
        log.info("Headers: {}", response.getHeaders());
        log.info("Body preview: {}", response.getBody().asString());
    }

    public String getRegistrationResultMessage(Response response) {
        Document resultDocument = Jsoup.parse(response.getBody().asString());
        return resultDocument.select(".result").text();
    }
}
