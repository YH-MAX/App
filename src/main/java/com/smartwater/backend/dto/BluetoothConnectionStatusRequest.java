package com.smartwater.backend.dto;

import com.smartwater.backend.model.BluetoothConnectionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BluetoothConnectionStatusRequest {

    @NotBlank
    private String macAddress;

    @NotNull
    private BluetoothConnectionStatus status;

    private String message;
}
