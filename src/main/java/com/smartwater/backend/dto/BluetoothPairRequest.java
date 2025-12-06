package com.smartwater.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BluetoothPairRequest {

    @NotBlank
    private String deviceName;

    @NotBlank
    private String macAddress;
}
