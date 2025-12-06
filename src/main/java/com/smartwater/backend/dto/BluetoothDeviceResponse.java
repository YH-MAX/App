package com.smartwater.backend.dto;

import com.smartwater.backend.model.BluetoothConnectionStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BluetoothDeviceResponse {

    private Long id;
    private String deviceName;
    private String macAddress;

    private BluetoothConnectionStatus lastStatus;
    private String lastStatusMessage;

    private LocalDateTime lastConnectedAt;
    private LocalDateTime lastDataReceivedAt;
}
