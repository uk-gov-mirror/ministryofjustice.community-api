package uk.gov.justice.digital.delius.data.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Custody {

    @ApiModelProperty(value = "Human readable id of the prison booking, AKA book number", example = "V74111")
    private String bookingNumber;
    @ApiModelProperty(value = "Institution where the offender currently resides")
    private Institution institution;
    @ApiModelProperty(value = "Key sentence dates of particular interest to custody")
    private CustodyRelatedKeyDates keyDates;
    @ApiModelProperty(value = "Email address of prison offender manager", example = "john.smith@justice.gov.uk")
    private String prisonEmail;
    @ApiModelProperty(value = "Telephone number of prison offender manager", example = "0114 555 5555")
    private String prisonTelephone;
}
