package com.Messenger.Backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatMessage {
    private String content;
    private String senderId;
    private String receiverId;
    private LocalDateTime timestamp;
}
