package org.niitp.service;

import org.niitp.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelServiceImpl implements ChannelService {
    @Autowired
    private ChannelRepository channelRepository;

    @Override
    public String findChannelColor(String vehicle, Integer channel_num) {
        return channelRepository.findChannelColor(vehicle, channel_num);
    }
}
