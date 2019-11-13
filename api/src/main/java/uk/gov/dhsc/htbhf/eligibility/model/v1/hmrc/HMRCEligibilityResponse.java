package uk.gov.dhsc.htbhf.eligibility.model.v1.hmrc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;
import uk.gov.dhsc.htbhf.eligibility.model.v1.ChildDTO;

import java.util.List;

@Data
@Builder
@AllArgsConstructor(onConstructor_ = {@JsonCreator})
public class HMRCEligibilityResponse {

    @JsonProperty("eligibilityStatus")
    private EligibilityStatus eligibilityStatus;

    @JsonProperty("householdIdentifier")
    private String householdIdentifier;

    @JsonProperty("numberOfChildrenUnderOne")
    private final Integer numberOfChildrenUnderOne;

    @JsonProperty("numberOfChildrenUnderFour")
    private final Integer numberOfChildrenUnderFour;

    @JsonProperty("children")
    private final List<ChildDTO> children;
}