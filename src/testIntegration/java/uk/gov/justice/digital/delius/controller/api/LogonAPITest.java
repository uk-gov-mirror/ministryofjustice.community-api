package uk.gov.justice.digital.delius.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import uk.gov.justice.digital.delius.jpa.national.entity.User;
import uk.gov.justice.digital.delius.jwt.Jwt;
import uk.gov.justice.digital.delius.service.wrapper.UserRepositoryWrapper;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LogonAPITest {

    @LocalServerPort
    int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Jwt jwt;

    @MockBean
    private UserRepositoryWrapper userRepositoryWrapper;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
        RestAssured.basePath = "/api";
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(new ObjectMapperConfig().jackson2ObjectMapperFactory(
                (aClass, s) -> objectMapper
        ));

        when(userRepositoryWrapper.getUser(anyString())).thenReturn(aUser());
    }

    private User aUser() {
        return User.builder().userId(1l).distinguishedName("oliver.connolly").build();
    }

    @Test
    public void logonWithMissingBodyGivesBadRequest() {
        given()
                .contentType("text/plain")
                .when()
                .post("/logon")
                .then()
                .statusCode(400);
    }

    @Test
    public void logonWithInvalidBodyGivesBadRequest() {
        given()
                .body("some old nonsense")
                .when()
                .post("/logon")
                .then()
                .statusCode(400);
    }

    @Test
    public void logonWithUnknownButOtherwiseValidDistinguishedNameGivesNotFound() {
        given()
                .body("uid=jimmysnozzle,ou=Users,dc=moj,dc=com")
                .when()
                .post("/logon")
                .then()
                .statusCode(404);
    }

    @Test
    public void logonWithKnownDistinguishedNameGivesTokenContainingOracleUser() {
        String token = given()
                .body("cn=jihn,ou=Users,dc=moj,dc=com")
                .when()
                .post("/logon")
                .then()
                .statusCode(200)
                .extract().body().asString();

        assertThat(jwt.parseToken(token).get().get(Jwt.UID)).isEqualTo("Jihndie1");
    }

    @Test
    public void logonWithNationalUserDistinguishedNameGivesTokenContainingNationalUser() {
        String token = given()
                .body("NationalUser")
                .when()
                .post("/logon")
                .then()
                .statusCode(200)
                .extract().body().asString();

        assertThat(jwt.parseToken(token).get().get(Jwt.UID)).isEqualTo("NationalUser");
    }

    @Test
    public void logonWithAPIUserDistinguishedNameGivesTokenContainingAPIUser() {
        String token = given()
                .body("APIUser")
                .when()
                .post("/logon")
                .then()
                .statusCode(200)
                .extract().body().asString();

        assertThat(jwt.parseToken(token).get().get(Jwt.UID)).isEqualTo("APIUser");
    }

}
