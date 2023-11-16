package uk.gov.dwp.uc.pairtest;

import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;

/**
 * Helper class to instantiate SeatReservationService
 */
public class SeatReservationServiceHelper {
   private SeatReservationService seatReservationService;

    /**
     * Create an instance of {@link SeatReservationService}.
     *
     * @return A {@link SeatReservationService}.
     */
    public SeatReservationService getReservationServiceInstance() {
        if (seatReservationService == null) {
            seatReservationService = new SeatReservationServiceImpl();
        }
        return seatReservationService;
    }
}
