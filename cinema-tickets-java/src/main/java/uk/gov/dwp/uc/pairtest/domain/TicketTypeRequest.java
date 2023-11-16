package uk.gov.dwp.uc.pairtest.domain;

/**
 * Immutable Object
 */

public class TicketTypeRequest {

    private int noOfTickets;
    private Type type;

    public TicketTypeRequest(final Type type, final int noOfTickets) {
        this.type = type;
        this.noOfTickets = noOfTickets;
    }

    public int getNoOfTickets() {
        return noOfTickets;
    }

    public Type getTicketType() {
        return type;
    }

    public enum Type {
        ADULT(20.0),
        CHILD(10.0),
        INFANT(0);

        private double ticketPrice;

        /**
         * The constructor to set ticket price.
         *
         * @param ticketPrice Ticket price
         */
        Type(final double ticketPrice) {
            this.ticketPrice = ticketPrice;
        }

        /**
         * Check if the ticket type is an adult.
         *
         * @return True if ticket type is Adult, false otherwise
         */
        public boolean isAdult() {
            return ADULT.equals(this);
        }

        /**
         * Get the ticket perice.
         *
         * @return the ticket price
         */
        public double getPrice() {
            return ticketPrice;
        }

        /**
         * Only adult and child is required to book seat.
         * Infants do not pay for a ticket and are not allocated a seat. They will be sitting on an Adult's lap.
         *
         * @return True if a seat is required, false otherwise
         */
        public boolean isSeatRequired() {
            return INFANT.equals(this) ? false : true;
        }

   }
}
