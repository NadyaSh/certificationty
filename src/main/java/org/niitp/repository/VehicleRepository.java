package org.niitp.repository;

import org.niitp.model.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<VehicleEntity, String> {
    @Override
    Optional<VehicleEntity> findById(String id);

    Optional<VehicleEntity> findByCode(String code);
}
