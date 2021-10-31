package testbase;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class TestBase {
    @Getter
    private RequestSpecification request;
    @Getter
    private Properties properties;


    /**
     * Test base class witch loads properties and sets up RestAssured client with base URL
     */
    public TestBase() {
        try {
            properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));

            RestAssured.baseURI = properties.getProperty("api.url");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        request = RestAssured.given();
    }
}
