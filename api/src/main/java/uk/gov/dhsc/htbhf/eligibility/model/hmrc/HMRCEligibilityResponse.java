package uk.gov.dhsc.htbhf.eligibility.model.hmrc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;

@Data
@Builder
@AllArgsConstructor(onConstructor_ = {@JsonCreator})
public class HMRCEligibilityResponse {

    @JsonProperty("eligibilityStatus")
    private EligibilityStatus eligibilityStatus;

    @JsonProperty("householdIdentifier")
    private String householdIdentifier;

}
