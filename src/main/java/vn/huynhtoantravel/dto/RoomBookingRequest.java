package vn.huynhtoantravel.dto;
import java.time.LocalDate;
public class RoomBookingRequest {
    private Long roomId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int roomsCount = 1;
    private int guestsCount = 2;
    private String customerNote;
    public Long getRoomId() { return roomId; } public void setRoomId(Long v) { roomId = v; }
    public LocalDate getCheckIn() { return checkIn; } public void setCheckIn(LocalDate v) { checkIn = v; }
    public LocalDate getCheckOut() { return checkOut; } public void setCheckOut(LocalDate v) { checkOut = v; }
    public int getRoomsCount() { return roomsCount; } public void setRoomsCount(int v) { roomsCount = v; }
    public int getGuestsCount() { return guestsCount; } public void setGuestsCount(int v) { guestsCount = v; }
    public String getCustomerNote() { return customerNote; } public void setCustomerNote(String v) { customerNote = v; }
}
