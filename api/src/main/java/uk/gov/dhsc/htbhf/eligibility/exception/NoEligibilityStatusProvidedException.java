package uk.gov.dhsc.htbhf.eligibility.exception;

public class NoEligibilityStatusProvidedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NoEligibilityStatusProvidedException(String message) {
        super(message);
    }
}
