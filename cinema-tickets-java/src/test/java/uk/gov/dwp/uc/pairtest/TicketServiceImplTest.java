package uk.gov.dwp.uc.pairtest;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidAccountException;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.exception.MandatoryAdultTicketPurchaseRequiredException;
import uk.gov.dwp.uc.pairtest.exception.MaxTicketPurchaseException;

/**
 * Tests to verify success and failure scenarios of TicketServiceImpl.PurchaseTicket
 */

public class TicketServiceImplTest {

    private final Long VALID_ACCOUNT_ID = 1000L;

    private final SeatReservationServiceHelper seatReservationServiceHelper = new SeatReservationServiceHelper();
    private final TicketPaymentServiceHelper ticketPaymentServiceHelper = new TicketPaymentServiceHelper();
    private final TicketService ticketService =
            new TicketServiceImpl(ticketPaymentServiceHelper.getPaymentServiceInstance(),
                    seatReservationServiceHelper.getReservationServiceInstance());

    /**
     * Success test to verify the booking
     * When a valid Adult, Child, Infant
     * ticket information is provided
     * <p>
     *  Test failure scenario - Valid Adult, Child and Infant ticket information is provided.
     * </p>
     */
    @Test
    public void testValidAdultChildInfantTicketInformationIsProvided() {
        // 2 valid Adult Ticket
        final TicketTypeRequest adultTicketRequest = createTicketRequest(Type.ADULT, 2);
        // 1 valid  Child Ticket
        final TicketTypeRequest childTicketRequest = createTicketRequest(Type.CHILD, 1);
        // 1 va;id Infant Ticket
        final TicketTypeRequest infantTicketRequest = createTicketRequest(Type.INFANT, 1);
        ticketService.purchaseTickets(VALID_ACCOUNT_ID, adultTicketRequest, childTicketRequest, infantTicketRequest);
        assertTrue("Booking Confirmed", true);
    }

    /**
     * Success test to verify the booking when a valid Adult ticket information is provided.
     * <p>
     *  Test failure scenario - A valid  Adult only ticket information is provided.
     * </p>
     */
    @Test
    public void testWhenValidAdultOnlyTicketInformationIsProvided() {
        final TicketTypeRequest adultTicketRequest = createTicketRequest(Type.ADULT, 1);
        ticketService.purchaseTickets(VALID_ACCOUNT_ID, adultTicketRequest);
        assertTrue("Purchase success!", true);
    }

    /**
     * Success test to verify the booking confirmed when valid max ticket count is provided.
     * <p>
     * Test failure scenario - Verify ticket count and amount.
     * </p>
     */
    @Test
    public void testVerifyValidTicketCountAndTotalPayableAmount() {
        // When
        final TicketTypeRequest adultTicketTypeRequest = createTicketRequest(Type.ADULT, 4);
        final TicketTypeRequest childTicketTypeRequest = createTicketRequest(Type.CHILD, 2);
        final TicketTypeRequest infantTicketTypeRequest = createTicketRequest(Type.INFANT, 2);

        final TicketPaymentService mockPaymentService = Mockito.mock(TicketPaymentServiceImpl.class);
        Mockito.doCallRealMethod()
                .when(mockPaymentService)
                .makePayment(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt());

        final SeatReservationService mockReservationService = Mockito.mock(SeatReservationServiceImpl.class);
        Mockito.doCallRealMethod()
                .when(mockReservationService)
                .reserveSeat(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt());

        // Given
        final TicketService verifyTicketService = Mockito.spy(new TicketServiceImpl(mockPaymentService, mockReservationService));
        verifyTicketService.purchaseTickets(VALID_ACCOUNT_ID, adultTicketTypeRequest, childTicketTypeRequest, infantTicketTypeRequest);

        // Then
        Mockito.verify(verifyTicketService, Mockito.times(1))
                .purchaseTickets(VALID_ACCOUNT_ID, adultTicketTypeRequest, childTicketTypeRequest, infantTicketTypeRequest);
        Mockito.verify(mockPaymentService, Mockito.times(1))
                .makePayment(VALID_ACCOUNT_ID, 100);
        Mockito.verify(mockReservationService, Mockito.times(1))
                .reserveSeat(VALID_ACCOUNT_ID, 6);
    }

    /**
     * Success test to verify the booking the purchase ticket method invocation.
     * <p>
     *  Test failure scenario - Test invocations.
     * </p>
     */
    @Test
    public void testVerifyPurchaseTicketsMethodInvocations() {
        final Long accountId = VALID_ACCOUNT_ID;
        final TicketTypeRequest adultTicketRequest = createTicketRequest(Type.ADULT, 1);
        final TicketService verifyTicketService = Mockito.spy(ticketService);
        verifyTicketService.purchaseTickets(accountId, adultTicketRequest);
        Mockito.verify(verifyTicketService, Mockito.times(1))
                .purchaseTickets(accountId, adultTicketRequest);
    }

    /**
     * Success test to verify the third party service error.
     * <p>
     *  Test failure scenario - Third party service error.
     * </p>
     */
    @Test
    public void testVerifyThirdPartyServiceError() {
        // When
        final TicketTypeRequest adultTicketRequest = createTicketRequest(Type.ADULT, 1);

        final TicketPaymentService mockPaymentService = Mockito.mock(TicketPaymentServiceImpl.class);
                Mockito.doThrow(RuntimeException.class)
                        .when(mockPaymentService)
                        .makePayment(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt());

        final SeatReservationService mockReservationService = Mockito.mock(SeatReservationServiceImpl.class);
                Mockito.doCallRealMethod()
                        .when(mockReservationService)
                        .reserveSeat(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt());

        // Given
        final TicketService spyTicketService = Mockito.spy(new TicketServiceImpl(mockPaymentService, mockReservationService));
                 spyTicketService.purchaseTickets(VALID_ACCOUNT_ID, adultTicketRequest);

        // Then
        Mockito.verify(spyTicketService, Mockito.times(1)).purchaseTickets(VALID_ACCOUNT_ID, adultTicketRequest);
        Mockito.verify(mockPaymentService, Mockito.times(1)).makePayment(VALID_ACCOUNT_ID, 20);
    }



    /**
     * Failure test to verify that booking is not confirmed,
     * when a valid Infant ticket information is provided
     * But a Valid Adult ticket information is not provided.
     * <p>
     *  Test failure scenario - Only a valid child ticket request with No Adult ticket request.
     * </p>
     */
    @Test(expected = MandatoryAdultTicketPurchaseRequiredException.class)
    public void testMandatoryAdultTicketPurchaseRequired() {
        final TicketTypeRequest childTicketRequest = createTicketRequest(Type.CHILD, 1);
        ticketService.purchaseTickets(VALID_ACCOUNT_ID,  childTicketRequest);

    }

    /**
     * Failure test to verify that booking is not confirmed,
     * when Exceed max ticket count.
     * <p>
     *  Test failure scenario - Max ticket purchase allowed.
     * </p>
     */
    @Test(expected = MaxTicketPurchaseException.class)
    public void testMaxTicketPurchaseAllowed() {
        final TicketTypeRequest adultTicketRequest = createTicketRequest(Type.ADULT, 15);
        final TicketTypeRequest childTicketRequest = createTicketRequest(Type.CHILD, 6);
        final TicketTypeRequest infantTicketRequest = createTicketRequest(Type.INFANT, 3);
        ticketService.purchaseTickets(VALID_ACCOUNT_ID, adultTicketRequest, childTicketRequest, infantTicketRequest);
    }

    /**
     * Failure test to verify that booking is not confirmed,
     * when an invalid account number is provided
     * <p>
     *  Test failure scenario - Invalid account.
     * </p>
     */
    @Test(expected = InvalidAccountException.class)
    public void testInvalidAccount() {
        final TicketTypeRequest adultTicketRequest = createTicketRequest(Type.ADULT, 1);
        Long INVALID_ACCOUNT_ID = -1000L;
        ticketService.purchaseTickets(INVALID_ACCOUNT_ID, adultTicketRequest);
    }

    /**
     * Failure test to verify that booking is not confirmed,
     * when no ticket information is provided
     * <p>
     *  Test failure scenario - Empty ticket type requests.
     * </p>
     */
    @Test(expected = InvalidPurchaseException.class)
    public void testNoTicketInformationIsProvided() {
        TicketTypeRequest ticketTypeRequest = null;
        ticketService.purchaseTickets(VALID_ACCOUNT_ID, ticketTypeRequest);
    }

    /**
     * Crate a sample ticket type request.
     *
     * @param  ticketType The ticket type
     * @param  quantity        Number of tickets required
     *
     * @return            A sample ticket type request
     */
    private TicketTypeRequest createTicketRequest(final Type ticketType, final int quantity) {
        return new TicketTypeRequest(ticketType, quantity);
    }

}