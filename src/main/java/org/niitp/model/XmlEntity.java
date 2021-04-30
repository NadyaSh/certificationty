package org.niitp.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "xml_certification")
public class XmlEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Timestamp datetime;
    @Column(name = "data")
    private String xml;
    private String vehicle;
    private String filename;
    private String application;
    private String customer;
}
