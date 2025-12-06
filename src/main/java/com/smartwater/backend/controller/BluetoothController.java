package com.smartwater.backend.controller;

import com.smartwater.backend.dto.BluetoothConnectionStatusRequest;
import com.smartwater.backend.dto.BluetoothDeviceResponse;
import com.smartwater.backend.dto.BluetoothPairRequest;
import com.smartwater.backend.service.BluetoothService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bluetooth")
@CrossOrigin(origins = "*")
public class BluetoothController {

    private final BluetoothService bluetoothService;

    public BluetoothController(BluetoothService bluetoothService) {
        this.bluetoothService = bluetoothService;
    }


    @PostMapping("/pair")
    public BluetoothDeviceResponse pairDevice(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestBody @Valid BluetoothPairRequest req
    ) {
        String email = currentUser.getUsername();
        return bluetoothService.pairDevice(email, req);
    }


    @PostMapping("/status")
    public BluetoothDeviceResponse updateStatus(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestBody @Valid BluetoothConnectionStatusRequest req
    ) {
        String email = currentUser.getUsername();
        return bluetoothService.updateConnectionStatus(email, req);
    }


    @GetMapping("/me/devices")
    public List<BluetoothDeviceResponse> getMyDevices(
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        String email = currentUser.getUsername();
        return bluetoothService.getDevicesForUser(email);
    }
}
