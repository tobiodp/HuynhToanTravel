package vn.huynhtoantravel.controller;
import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.*;
import vn.huynhtoantravel.repository.HotelRepository; import vn.huynhtoantravel.service.RoomAvailabilityService; import java.time.LocalDate; import java.util.Map;
@Controller
public class HotelController {
 private final HotelRepository hotels; private final RoomAvailabilityService availability;
 public HotelController(HotelRepository h,RoomAvailabilityService a){hotels=h;availability=a;}
    @GetMapping("/hotels") 
    String list(Model m){
        m.addAttribute("hotels",hotels.findByActiveTrueOrderByStarRatingDesc());
        return "hotels";
    }

    @GetMapping("/hotels/{id}")
    String detail(@PathVariable Long id, Model m) {
        vn.huynhtoantravel.domain.Hotel hotel = hotels.findByIdWithRooms(id).orElseThrow(() -> new IllegalArgumentException("Khách sạn không tồn tại"));
        m.addAttribute("hotel", hotel);
        return "hotel-detail";
    }

    @GetMapping("/api/rooms/{roomId}/availability") 
    @ResponseBody 
    Map<String,Object> available(@PathVariable Long roomId,@RequestParam LocalDate checkIn,@RequestParam LocalDate checkOut){
        int n=availability.availableInventory(roomId,checkIn,checkOut);
        return Map.of("available",n,"canBook",n>0);
    }
}
