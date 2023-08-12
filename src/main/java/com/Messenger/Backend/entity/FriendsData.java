//package com.Messenger.Backend.entity;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Data
//@Builder
//@Table(name = "friend_relationships")
//public class FriendsData {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long relation_id;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private UserData user_id;
//
//    @ManyToOne
//    @JoinColumn(name = "friend_id")
//    private UserData friend_id;
//}
