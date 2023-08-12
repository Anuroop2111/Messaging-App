package com.Messenger.Backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "users")
public class UserData {

    @Id
    @Column(name = "id")
    private String id;

    private String username;
    private String name;
    private String password;
    private String phone;
    private String email;
}
