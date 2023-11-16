package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;


/**
 * Helper class to instantiate SeatReservationService
 */
public class TicketPaymentServiceHelper {

    private TicketPaymentService ticketPaymentService;


    /**
     * Create an instance of {@link TicketPaymentService}.
     *
     * @return A {@link TicketPaymentService}.
     */
    public TicketPaymentService getPaymentServiceInstance() {
        if (ticketPaymentService == null) {
            ticketPaymentService = new TicketPaymentServiceImpl();
        }
        return ticketPaymentService;
    }

}
