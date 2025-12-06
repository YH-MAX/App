package com.smartwater.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bluetooth_devices")
public class BluetoothDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String deviceName;


    @Column(nullable = false)
    private String macAddress;

    @Enumerated(EnumType.STRING)
    private BluetoothConnectionStatus lastStatus;

    private String lastStatusMessage;

    private LocalDateTime lastConnectedAt;

    private LocalDateTime lastDataReceivedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
