package org.niitp.service;

import org.niitp.exception.VehicleBadRequestException;
import org.niitp.model.VehicleEntity;
import org.niitp.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleServiceImpl implements VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public VehicleEntity findVehicle(String id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleBadRequestException("VehicleEntity", "vehicle", id));
    }
}
