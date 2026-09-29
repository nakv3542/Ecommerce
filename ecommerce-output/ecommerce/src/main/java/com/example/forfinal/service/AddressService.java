package com.example.forfinal.service;

import com.example.forfinal.dto.AddressRequest;
import com.example.forfinal.dto.AddressResponse;
import com.example.forfinal.entity.Address;
import com.example.forfinal.entity.User;
import com.example.forfinal.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    public List<AddressResponse> getUserAddresses(User user) {
        return addressRepository.findByUser(user).stream()
                .map(AddressResponse::new).collect(Collectors.toList());
    }

    public AddressResponse addAddress(User user, AddressRequest req) {
        Address address = new Address();
        applyRequest(address, req);
        address.setUser(user);
        if (req.isDefault() || addressRepository.findByUser(user).isEmpty()) {
            setAsDefault(user, address);
        }
        return new AddressResponse(addressRepository.save(address));
    }

    public AddressResponse updateAddress(User user, Long addressId, AddressRequest req) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        applyRequest(address, req);
        if (req.isDefault()) {
            setAsDefault(user, address);
        }
        return new AddressResponse(addressRepository.save(address));
    }

    public void deleteAddress(User user, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        addressRepository.delete(address);
    }

    public Address getById(Long id) {
        return addressRepository.findById(id).orElseThrow(() -> new RuntimeException("Address not found"));
    }

    private void setAsDefault(User user, Address newDefault) {
        for (Address a : addressRepository.findByUser(user)) {
            a.setDefault(false);
            addressRepository.save(a);
        }
        newDefault.setDefault(true);
    }

    private void applyRequest(Address address, AddressRequest req) {
        address.setFullName(req.getFullName());
        address.setPhone(req.getPhone());
        address.setStreet(req.getStreet());
        address.setCity(req.getCity());
        address.setState(req.getState());
        address.setZipCode(req.getZipCode());
        address.setCountry(req.getCountry());
    }
}
