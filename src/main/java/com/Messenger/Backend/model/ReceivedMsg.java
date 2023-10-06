package com.Messenger.Backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReceivedMsg {
    private String senderId;
    private String chatId;
    private String content;
    private LocalDateTime timestamp;



}
