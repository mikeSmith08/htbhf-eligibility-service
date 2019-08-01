package uk.gov.dhsc.htbhf.eligibility.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.dhsc.htbhf.eligibility.factory.EligibilityResponseFactory;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.PersonDTO;
import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.hmrc.HMRCEligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.hmrc.HMRCEligibilityResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class EligibilityService {

    private final DWPClient dwpClient;
    private final HMRCClient hmrcClient;
    private final BigDecimal ucMonthlyIncomeThreshold;
    private final Integer eligibilityCheckPeriodLength;
    private final BigDecimal ctcAnnualIncomeThreshold;
    private final EligibilityResponseFactory responseFactory;


    public EligibilityService(@Value("${eligibility-check-period-length}") Integer eligibilityCheckPeriodLength,
                              @Value("${dwp.uc-monthly-income-threshold}") BigDecimal ucMonthlyIncomeThreshold,
                              @Value("${hmrc.ctc-annual-income-threshold}") BigDecimal ctcAnnualIncomeThreshold,
                              DWPClient dwpClient,
                              HMRCClient hmrcClient,
                              EligibilityResponseFactory responseFactory) {
        this.dwpClient = dwpClient;
        this.ucMonthlyIncomeThreshold = ucMonthlyIncomeThreshold;
        this.eligibilityCheckPeriodLength = eligibilityCheckPeriodLength;
        this.hmrcClient = hmrcClient;
        this.ctcAnnualIncomeThreshold = ctcAnnualIncomeThreshold;
        this.responseFactory = responseFactory;
    }


    /**
     * Checks the eligibility of a given person.
     * Build the DWP Eligibility Request and send to to DWP as an Async call.
     *
     * @param person The person to check
     * @return The eligibility response
     * @throws ExecutionException   If problems with parallel calls to DWP and HMRC to check eligibility
     * @throws InterruptedException If problems with parallel calls to DWP and HMRC to check eligibility
     */
    public EligibilityResponse checkEligibility(PersonDTO person) throws ExecutionException, InterruptedException {
        LocalDate currentDate = LocalDate.now();

        log.debug("Checking eligibility");
        DWPEligibilityRequest dwpEligibilityRequest = createDWPRequest(person, currentDate);
        HMRCEligibilityRequest hmrcEligibilityRequest = createHMRCRequest(person, currentDate);

        CompletableFuture<DWPEligibilityResponse> dwpEligibilityResponse = dwpClient.checkEligibility(dwpEligibilityRequest);
        CompletableFuture<HMRCEligibilityResponse> hmrcEligibilityResponse = hmrcClient.checkEligibility(hmrcEligibilityRequest);

        CompletableFuture.allOf(dwpEligibilityResponse, hmrcEligibilityResponse).join();

        return responseFactory.createEligibilityResponse(dwpEligibilityResponse.get(), hmrcEligibilityResponse.get());
    }

    private DWPEligibilityRequest createDWPRequest(PersonDTO person, LocalDate currentDate) {
        return DWPEligibilityRequest.builder()
                .person(person)
                .eligibleEndDate(currentDate)
                .eligibleStartDate(currentDate.minusDays(eligibilityCheckPeriodLength))
                .ucMonthlyIncomeThreshold(ucMonthlyIncomeThreshold)
                .build();
    }

    private HMRCEligibilityRequest createHMRCRequest(PersonDTO person, LocalDate currentDate) {
        return HMRCEligibilityRequest.builder()
                .person(person)
                .eligibleEndDate(currentDate)
                .eligibleStartDate(currentDate.minusDays(eligibilityCheckPeriodLength))
                .ctcAnnualIncomeThreshold(ctcAnnualIncomeThreshold)
                .build();
    }
}