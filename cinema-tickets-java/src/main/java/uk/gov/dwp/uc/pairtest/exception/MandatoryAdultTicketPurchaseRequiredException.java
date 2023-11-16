package uk.gov.dwp.uc.pairtest.exception;

public class MandatoryAdultTicketPurchaseRequiredException extends InvalidPurchaseException {
        /**
         * The default constructor with message that an adult ticket purchase
         * is mandatory for a child or an infant ticket purchase.
         */


        public MandatoryAdultTicketPurchaseRequiredException() {
            super(ErrorMessages.MANDATORY_ADULT_TICKET_PURCHASE_REQUIRED);
        }
}
