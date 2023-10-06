package com.Messenger.Backend.controller;

import com.Messenger.Backend.entity.ChatData;
import com.Messenger.Backend.model.ChatMessage;
import com.Messenger.Backend.model.FriendChatInfo;
import com.Messenger.Backend.model.ReceivedMsg;
import com.Messenger.Backend.repo.ChatRepository;
import com.Messenger.Backend.repo.UserRepository;
import com.Messenger.Backend.service.ChatMsgService;
import com.Messenger.Backend.util.TimestampUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api")
public class ChatMsgController {

//    @Autowired
//    SimpMessagingTemplate template;

    private final ChatMsgService chatMsgService;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    public ChatMsgController(ChatMsgService chatMsgService, UserRepository userRepository, ChatRepository chatRepository) {
        this.chatMsgService = chatMsgService;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

//    Get the List of chats the user have
    @GetMapping("/getChatNames/{email}")
    public List<FriendChatInfo> getChatNames(@PathVariable String email) {
        log.info("Username = {}", email);
        String userId = chatMsgService.getUserId(email);
        List<FriendChatInfo> chatInfo = chatMsgService.getFriendIds(userId);

        return chatInfo;
    }

    @GetMapping("/getMessage/{chatId}")
    public List<ChatMessage> getChatMessages(@PathVariable String chatId) {
//        log.info("Chat id = {}", chatId);
        List<ChatMessage> chatMessage = chatMsgService.getChatContent(chatId);

        return chatMessage;
    }

    @PostMapping("/saveMessage")
    public ResponseEntity<Void> saveMessage(@RequestBody ReceivedMsg receivedMsg){

        return chatMsgService.saveMessage(receivedMsg.getChatId(), receivedMsg.getSenderId(), receivedMsg.getContent());

    }

//    @PostMapping("/send")
//    public  ResponseEntity<Void> sendMessage(@RequestBody ReceivedMsg receivedMsg){
//        template.convertAndSend("/topic/message",receivedMsg);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    // broadcastMessage method just return payload received from “/send” POST request. Returned value is received by clients register at “/topic/message”.
//    @SendTo("/topic/message")
//    public ReceivedMsg broadcastMessage(@Payload ReceivedMsg receivedMsg) {
//        return receivedMsg;
//    }
//
//    @MessageMapping("/sendMessage")
//    public void receiveMessage(@Payload ReceivedMsg receivedMsg) {
//        // receive message from client
//    }


}