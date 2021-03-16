package uk.gov.justice.digital.delius.data.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
@With
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReferralSentRequest {
    @NotEmpty
    @ApiModelProperty(required = true)
    private String providerCode;

    private String staffCode;

    private String teamCode;

    @NotNull
    @ApiModelProperty(required = true)
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate date;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String nsiType;

    private String nsiSubType;

    @Positive
    @NotNull
    @ApiModelProperty(required = true)
    private Long convictionId;

    @Positive
    private Long requirementId;

    @NotEmpty
    private String nsiStatus;

    private String notes;
}