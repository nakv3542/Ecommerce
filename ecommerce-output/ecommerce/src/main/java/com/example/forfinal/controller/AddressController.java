package com.example.forfinal.controller;

import com.example.forfinal.dto.AddressRequest;
import com.example.forfinal.dto.AddressResponse;
import com.example.forfinal.entity.User;
import com.example.forfinal.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    public List<AddressResponse> getAddresses(@AuthenticationPrincipal User user) {
        return addressService.getUserAddresses(user);
    }

    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(@AuthenticationPrincipal User user,
                                                       @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.addAddress(user, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(@AuthenticationPrincipal User user,
                                                          @PathVariable Long id,
                                                          @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.updateAddress(user, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@AuthenticationPrincipal User user, @PathVariable Long id) {
        addressService.deleteAddress(user, id);
        return ResponseEntity.ok().build();
    }
}
