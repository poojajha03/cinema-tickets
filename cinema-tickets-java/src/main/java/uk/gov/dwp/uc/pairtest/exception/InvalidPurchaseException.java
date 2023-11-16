package uk.gov.dwp.uc.pairtest.exception;

public class InvalidPurchaseException extends RuntimeException {

    /**
     * The default constructor.
     */
    public InvalidPurchaseException() {
    }

    /**
     * Constructor with a default error message.
     *
     * @param message The error message
     */
    public InvalidPurchaseException(final String message) {
        super(message);
    }
}
