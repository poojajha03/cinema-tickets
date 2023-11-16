package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

/**
 * Implementation of the Ticket Service
 */

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */
    final static Logger log =
            LoggerFactory.getLogger(TicketServiceImpl.class);
    /**
     * Only a maximum of 20 tickets that can be purchased at a time.
     */
    private final static int MAX_TICKET_ALLOWED   = 20;

    private final TicketPaymentService   paymentService;
    private final SeatReservationService reservationService;

    /**
     * The default constructor which initializes third-party service instances.
     */
    public TicketServiceImpl(TicketPaymentService paymentService, SeatReservationService reservationService) {
        this.paymentService = paymentService;
        this.reservationService = reservationService;
    }

    @Override
    public void purchaseTickets(final Long accountId, final TicketTypeRequest... ticketTypeRequests)
            throws InvalidPurchaseException {

        if (Objects.isNull(ticketTypeRequests)){
            log.error("Ticket payment request is null:" + accountId);
            throw new InvalidPurchaseException("Valid ticket information is not provided.");
        }

        // Validated Account
        if (!isValidAccount(accountId)) {
            log.error(ErrorMessages.INVALID_ACCOUNT + accountId);
            throw new InvalidAccountException();
        }

        // Only a maximum of 20 tickets that can be purchased at a time.
        if (!maxTicketBookingValidation(ticketTypeRequests)) {
            log.error(ErrorMessages.MAX_TICKET_PURCHASE_ALLOWED);
            throw new MaxTicketPurchaseException();
        }

        // Child and Infant tickets cannot be purchased without purchasing an Adult ticket.
        if (!onlyChildInfantBookingRequest(ticketTypeRequests)) {
            log.error(ErrorMessages.MANDATORY_ADULT_TICKET_PURCHASE_REQUIRED);
            throw new MandatoryAdultTicketPurchaseRequiredException();
        }

        try {
            final double finalBookingAmount = getTotalBookingAmount(ticketTypeRequests);
            paymentService.makePayment(accountId, (int) finalBookingAmount);
            log.debug("Total booking amount paid successful.");


            final int totalSeats = calTotalSeats(ticketTypeRequests);
            reservationService.reserveSeat(accountId, totalSeats);
            log.debug("Seat reservation completed.");

        } catch (final Throwable e) {
            log.error("Error while finalizing booking.", e);
        }

        log.info("Booking reservation complete.");
    }

    /**
     * Validates if account is a valid account.
     * All accounts with an id greater than zero are valid.
     *
     * @param  accountId    The Account ID
     * @return  True if the account is valid, false otherwise
     */
    private boolean isValidAccount(final Long accountId) {
        return accountId > 0;
    }

    /**
     * Validates if account is a valid account.
     * All accounts with an id greater than zero are valid.
     *
     * @return  True if the account is valid, false otherwise
     */
    private boolean maxTicketBookingValidation(final TicketTypeRequest... ticketTypeRequests) {
        int totalTicketCount = 0;

        for (final TicketTypeRequest request : ticketTypeRequests) {
            if (!Objects.isNull(request)) {
                int requestedTicketCount = request.getNoOfTickets();
                totalTicketCount += requestedTicketCount;
            }
        }
        // Check if max ticket count has exceeded
        return totalTicketCount <= MAX_TICKET_ALLOWED;

    }

    /**
     * Validates booking request .
     * Child and Infant tickets cannot be purchased without purchasing an Adult ticket
     *
     * @param   ticketTypeRequests Ticket purchase requests
     *
     * @return  True if the booking request is valid, false otherwise
     */
    private boolean onlyChildInfantBookingRequest(final TicketTypeRequest... ticketTypeRequests) {
        boolean adultTicketRequest = false;
        if(ticketTypeRequests != null) {
            adultTicketRequest =
                    Arrays.stream(ticketTypeRequests).anyMatch(p -> !Objects.isNull(p) && p.getTicketType().isAdult());
        }
        return adultTicketRequest;
    }

    /**
     * Calculates the total booking amount.
     *
     * @param  ticketTypeRequests The ticket purchase requests
     *
     * @return  The total booking amount
     */
    private double getTotalBookingAmount(final TicketTypeRequest... ticketTypeRequests) {
        double totalBookingAmount = 0;
        try {
            for (final TicketTypeRequest bookingRequest : ticketTypeRequests) {
                totalBookingAmount = Arrays.stream(ticketTypeRequests)
                        .mapToDouble(TicketTypeRequest::calculateCost)
                        .sum();
            }
        } catch(InvalidPurchaseException e) {
            log.error("Problem in calculating total cost towards booking");
            throw e;
        }
        return totalBookingAmount;
    }

    /**
     * Calculates the total seats to be reserved on behalf of the account ID.
     *
     * @param  ticketTypeRequests The ticket purchase requests
     *
     * @return Total number of seat allocated
     */
    private int calTotalSeats(final TicketTypeRequest... ticketTypeRequests) {
        int totalSeatAllocation = 0;
        try {
            totalSeatAllocation = Arrays.stream(ticketTypeRequests)
                    .filter(request -> request.getTicketType().isSeatRequired())
                    .mapToInt(TicketTypeRequest::getNoOfTickets)
                    .sum();
        } catch(InvalidPurchaseException e) {
            log.error("Problem in allocating seat");
            throw e;
        }
        return totalSeatAllocation;
    }
}