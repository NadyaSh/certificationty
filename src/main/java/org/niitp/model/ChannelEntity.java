package org.niitp.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "channels")
public class ChannelEntity implements Serializable {
    @Id
    private Long id;
    private String vehicle;
    @Column(name = "channel_num")
    private Integer channelNum;
    private String color;

}
