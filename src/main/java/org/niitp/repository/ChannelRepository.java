package org.niitp.repository;

import org.niitp.model.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChannelRepository extends JpaRepository<ChannelEntity, Long> {
    @Query(value = "select color from channels where vehicle = ?1 and channel_num = ?2",
            nativeQuery = true)
    String findChannelColor(String vehicle, Integer channel_num);
}
