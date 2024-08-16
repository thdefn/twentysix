package cm.twentysix.user.controller;

import cm.twentysix.user.dto.AddressItem;
import cm.twentysix.user.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/users/addresses")
public class AddressController {
    private final AddressService addressService;

    @GetMapping("/default")
    private ResponseEntity<AddressItem> retrieveDefaultAddress(@RequestHeader(value = "X-USER-ID") Long userId) {
        return ResponseEntity.ok(addressService.retrieveDefaultAddress(userId));
    }

    @GetMapping
    private ResponseEntity<List<AddressItem>> retrieveAllAddress(@RequestHeader(value = "X-USER-ID") Long userId) {
        return ResponseEntity.ok(addressService.retrieveAllAddress(userId));
    }


    @DeleteMapping("/{addressId}")
    private ResponseEntity<Void> deleteAddress(@PathVariable Long addressId,
                                               @RequestHeader(value = "X-USER-ID") Long userId) {
        addressService.deleteAddress(addressId, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{addressId}")
    private ResponseEntity<Void> changeDefaultAddress(@PathVariable Long addressId,
                                                      @RequestHeader(value = "X-USER-ID") Long userId) {
        addressService.changeDefaultAddress(addressId, userId);
        return ResponseEntity.ok().build();
    }


}
