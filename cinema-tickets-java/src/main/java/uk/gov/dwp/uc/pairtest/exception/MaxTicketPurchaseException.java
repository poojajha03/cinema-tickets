package uk.gov.dwp.uc.pairtest.exception;

public class MaxTicketPurchaseException extends InvalidPurchaseException {


        /**
         * The default constructor with message that only maximum of 20 tickets can be purchased at one time.
         */

        public MaxTicketPurchaseException() {
            super(ErrorMessages.MAX_TICKET_PURCHASE_ALLOWED);
        }
}
