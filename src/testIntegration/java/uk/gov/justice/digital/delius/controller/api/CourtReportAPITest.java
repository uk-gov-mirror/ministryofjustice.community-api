package uk.gov.justice.digital.delius.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.digital.delius.data.api.CourtReport;
import uk.gov.justice.digital.delius.jwt.Jwt;
import uk.gov.justice.digital.delius.user.UserData;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev-seed")
public class CourtReportAPITest {

    @LocalServerPort
    int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Jwt jwt;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
        RestAssured.basePath = "/api";
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(new ObjectMapperConfig().jackson2ObjectMapperFactory(
                (aClass, s) -> objectMapper
        ));
    }

    @Test
    public void canGetAllReportsForOffenderByCrn() {

        CourtReport[] courtReports = given()
            .header("Authorization", aValidToken())
            .when()
            .get("offenders/crn/X320741/courtReports")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(CourtReport[].class);

        assertThat(courtReports).hasSizeGreaterThan(1);
    }

    @Test
    public void cannotGetReportForOffenderByOffenderIdAndReportIdWithoutJwtAuthorizationHeader() {
        RestAssured.when()
                .get("offenders/offenderId/1/courtReports/4")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void cannotGetReportsForOffenderByOffenderIdWithoutJwtAuthorizationHeader() {
        RestAssured.when()
                .get("offenders/offenderId/1/courtReports")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void cannotGetReportForOffenderByCrnAndReportIdWithoutJwtAuthorizationHeader() {
        RestAssured.when()
                .get("offenders/crn/CRN1/courtReports/4")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void cannotGetReportsForOffenderByCrnWithoutJwtAuthorizationHeader() {
        RestAssured.when()
                .get("offenders/crn/CRN1/courtReports")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }


    @Test
    public void cannotGetReportForOffenderByNomsNumberAndReportIdWithoutJwtAuthorizationHeader() {
        RestAssured.when()
                .get("offenders/nomsNumber/NOMS1/courtReports/4")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void cannotGetReportsForOffenderByNomsNumberWithoutJwtAuthorizationHeader() {
        RestAssured.when()
                .get("offenders/nomsNumber/NOMS1/courtReports")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    private String aValidToken() {
        return aValidTokenFor(UUID.randomUUID().toString());
    }

    private String aValidTokenFor(String distinguishedName) {
        return "Bearer " + jwt.buildToken(UserData.builder()
                .distinguishedName(distinguishedName)
                .uid("bobby.davro").build());
    }
}
