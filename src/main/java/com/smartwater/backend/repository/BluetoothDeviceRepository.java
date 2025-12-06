package com.smartwater.backend.repository;

import com.smartwater.backend.model.BluetoothDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BluetoothDeviceRepository extends JpaRepository<BluetoothDevice, Long> {

    List<BluetoothDevice> findByUser_Id(Long userId);

    Optional<BluetoothDevice> findByUser_IdAndMacAddress(Long userId, String macAddress);
}
