package com.Messenger.Backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "refresh_token") // Refresh Token table name.
public class TokenData {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "ref_series")
    private String refSeries;

    @Column(name = "unique_identifier")
    private String uniqueIdentifier;

    private String username;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "creation_date")
    private Date creationDate;

    private Boolean revoked; // If a Refresh Token is compromised, we can revoke that token by setting 'revoked' = true or delete from database.

}
