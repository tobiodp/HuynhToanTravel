package vn.huynhtoantravel.dto;

import java.time.LocalDate;
import java.util.List;

public class TicketBookingRequest {
    private String customerNote;
    private List<TicketItemRequest> items;

    public String getCustomerNote() { return customerNote; }
    public void setCustomerNote(String customerNote) { this.customerNote = customerNote; }

    public List<TicketItemRequest> getItems() { return items; }
    public void setItems(List<TicketItemRequest> items) { this.items = items; }

    public static class TicketItemRequest {
        private String ticketType;
        private int quantity;
        private boolean localResident;
        private LocalDate serviceDate;

        public String getTicketType() { return ticketType; }
        public void setTicketType(String ticketType) { this.ticketType = ticketType; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public boolean isLocalResident() { return localResident; }
        public void setLocalResident(boolean localResident) { this.localResident = localResident; }

        public LocalDate getServiceDate() { return serviceDate; }
        public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }
    }
}
