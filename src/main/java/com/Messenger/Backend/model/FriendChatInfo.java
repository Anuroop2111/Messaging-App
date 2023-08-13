package com.Messenger.Backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FriendChatInfo {
    private String friendId;
    private String chatId;
    private String friendName;
}
