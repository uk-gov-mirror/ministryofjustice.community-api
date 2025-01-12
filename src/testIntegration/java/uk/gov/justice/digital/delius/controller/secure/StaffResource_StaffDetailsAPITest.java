package uk.gov.justice.digital.delius.controller.secure;

import lombok.val;
import org.junit.jupiter.api.Test;
import uk.gov.justice.digital.delius.data.api.StaffDetails;

import java.util.Arrays;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class StaffResource_StaffDetailsAPITest extends IntegrationTestBase {
    @Test
    public void canRetrieveStaffDetailsByStaffIdentifier() {

        val staffDetails = given()
                .auth()
                .oauth2(tokenWithRoleCommunity())
                .contentType(APPLICATION_JSON_VALUE)
                .when()
                .get("staff/staffIdentifier/11")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(StaffDetails.class);

        assertThat(staffDetails).isNotNull();
    }

    @Test
    public void retrievingStaffDetailsByStaffIdentifierReturn404WhenStaffDoesNotExist() {

        given()
                .auth()
                .oauth2(tokenWithRoleCommunity())
                .contentType(APPLICATION_JSON_VALUE)
                .when()
                .get("staff/staffIdentifier/99999")
                .then()
                .statusCode(404);
    }

    @Test
    public void canRetrieveStaffDetailsByUsername() {

        val staffDetails = given()
                .auth()
                .oauth2(tokenWithRoleCommunity())
                .contentType(APPLICATION_JSON_VALUE)
                .when()
                .get("staff/username/SheilaHancockNPS")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(StaffDetails.class);

        assertThat(staffDetails).isNotNull();
    }

    @Test
    public void canRetrieveStaffDetailsByUsernameIgnoresCase() {

        val staffDetails = given()
                .auth()
                .oauth2(tokenWithRoleCommunity())
                .contentType(APPLICATION_JSON_VALUE)
                .when()
                .get("staff/username/sheilahancocknps")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(StaffDetails.class);

        assertThat(staffDetails).isNotNull();
    }

    @Test
    public void retrievingStaffDetailsByUsernameReturn404WhenUserExistsButStaffDoesNot() {

        given()
                .auth()
                .oauth2(tokenWithRoleCommunity())
                .contentType(APPLICATION_JSON_VALUE)
                .when()
                .get("staff/username/NoStaffUserNPS")
                .then()
                .statusCode(404);
    }

    @Test
    public void retrieveStaffDetailsForMultipleUsers() {

        val staffDetails = given()
                .auth()
                .oauth2(tokenWithRoleCommunity())
                .contentType(APPLICATION_JSON_VALUE)
                .body(getUsernames(Set.of("sheilahancocknps", "JimSnowLdap")))
                .when()
                .post("staff/list")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(StaffDetails[].class);

        var jimSnowUserDetails = Arrays.stream(staffDetails).filter(s -> s.getUsername().equals("JimSnowLdap")).findFirst().get();
        var sheilaHancockUserDetails = Arrays.stream(staffDetails).filter(s -> s.getUsername().equals("SheilaHancockNPS")).findFirst().get();

        assertThat(staffDetails.length).isEqualTo(2);

        assertThat(jimSnowUserDetails.getEmail()).isEqualTo("jim.snow@justice.gov.uk");
        assertThat(jimSnowUserDetails.getStaff().getForenames()).isEqualTo("JIM");
        assertThat(jimSnowUserDetails.getStaff().getSurname()).isEqualTo("SNOW");

        assertThat(sheilaHancockUserDetails.getEmail()).isEqualTo("sheila.hancock@justice.gov.uk");
        assertThat(sheilaHancockUserDetails.getStaff().getForenames()).isEqualTo("SHEILA LINDA");
        assertThat(sheilaHancockUserDetails.getStaff().getSurname()).isEqualTo("HANCOCK");
    }

    @Test
    public void retrieveDetailsWhenUsersDoNotExist() {

        val staffDetails = given()
                .auth()
                .oauth2(tokenWithRoleCommunity())
                .contentType(APPLICATION_JSON_VALUE)
                .body(getUsernames(Set.of("xxxppp1ps", "dddiiiyyyLdap")))
                .when()
                .post("staff/list")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(StaffDetails[].class);

        assertThat(staffDetails).isEmpty();
    }

    @Test
    public void retrieveMultipleUserDetailsWithNoBodyContentReturn400() {

        given()
                .auth()
                .oauth2(tokenWithRoleCommunity())
                .contentType(APPLICATION_JSON_VALUE)
                .when()
                .post("staff/list")
                .then()
                .statusCode(400);
    }

    private String getUsernames(Set <String> usernames) {
        return writeValueAsString(usernames);
    }
}
