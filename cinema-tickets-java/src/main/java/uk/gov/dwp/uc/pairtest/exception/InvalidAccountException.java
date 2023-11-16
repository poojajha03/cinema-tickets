package uk.gov.dwp.uc.pairtest.exception;

public class InvalidAccountException extends InvalidPurchaseException {
        /**
         * The default constructor for invalid account message
         */
        public InvalidAccountException() {
            super(ErrorMessages.INVALID_ACCOUNT);
        }
}
