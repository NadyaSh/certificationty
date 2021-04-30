package org.niitp.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;

@Data
@Entity
@Table(name = "people")
public class UserEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "login", insertable = false, updatable = false)
    private String username;
    private String password;
    @Column(name = "last_login")
    private Timestamp lastLogin;
    @Column(name = "created")
    private Instant createdAt;
    private String login;
    private String email;
    @Column(name = "role", updatable = false)
    private String role;
}
