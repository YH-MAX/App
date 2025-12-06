package com.smartwater.backend.service;

import com.smartwater.backend.dto.BluetoothConnectionStatusRequest;
import com.smartwater.backend.dto.BluetoothDeviceResponse;
import com.smartwater.backend.dto.BluetoothPairRequest;
import com.smartwater.backend.model.BluetoothConnectionStatus;
import com.smartwater.backend.model.BluetoothDevice;
import com.smartwater.backend.model.User;
import com.smartwater.backend.repository.BluetoothDeviceRepository;
import com.smartwater.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BluetoothService {

    private final BluetoothDeviceRepository bluetoothDeviceRepository;
    private final UserRepository userRepository;

    public BluetoothService(BluetoothDeviceRepository bluetoothDeviceRepository,
                            UserRepository userRepository) {
        this.bluetoothDeviceRepository = bluetoothDeviceRepository;
        this.userRepository = userRepository;
    }

    public BluetoothDeviceResponse pairDevice(String email, BluetoothPairRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));


        BluetoothDevice device = bluetoothDeviceRepository
                .findByUser_IdAndMacAddress(user.getId(), req.getMacAddress())
                .orElseGet(BluetoothDevice::new);

        device.setUser(user);
        device.setDeviceName(req.getDeviceName());
        device.setMacAddress(req.getMacAddress());


        device.setLastStatus(BluetoothConnectionStatus.CONNECTED);
        device.setLastStatusMessage("Paired successfully");
        device.setLastConnectedAt(LocalDateTime.now());

        BluetoothDevice saved = bluetoothDeviceRepository.save(device);
        return toResponse(saved);
    }

    public BluetoothDeviceResponse updateConnectionStatus(String email,
                                                          BluetoothConnectionStatusRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        BluetoothDevice device = bluetoothDeviceRepository
                .findByUser_IdAndMacAddress(user.getId(), req.getMacAddress())
                .orElseGet(BluetoothDevice::new);

        device.setUser(user);
        if (device.getDeviceName() == null) {
            device.setDeviceName("Bluetooth device");
        }
        device.setMacAddress(req.getMacAddress());
        device.setLastStatus(req.getStatus());
        device.setLastStatusMessage(req.getMessage());

        if (req.getStatus() == BluetoothConnectionStatus.CONNECTED) {
            device.setLastConnectedAt(LocalDateTime.now());
        }

        BluetoothDevice saved = bluetoothDeviceRepository.save(device);
        return toResponse(saved);
    }

    public List<BluetoothDeviceResponse> getDevicesForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        return bluetoothDeviceRepository.findByUser_Id(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private BluetoothDeviceResponse toResponse(BluetoothDevice device) {
        BluetoothDeviceResponse dto = new BluetoothDeviceResponse();
        dto.setId(device.getId());
        dto.setDeviceName(device.getDeviceName());
        dto.setMacAddress(device.getMacAddress());
        dto.setLastStatus(device.getLastStatus());
        dto.setLastStatusMessage(device.getLastStatusMessage());
        dto.setLastConnectedAt(device.getLastConnectedAt());
        dto.setLastDataReceivedAt(device.getLastDataReceivedAt());
        return dto;
    }
}
