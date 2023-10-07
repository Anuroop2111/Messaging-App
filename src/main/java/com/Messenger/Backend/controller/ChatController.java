package com.Messenger.Backend.controller;

import com.Messenger.Backend.entity.ChatData;
import com.Messenger.Backend.model.ReceivedMsg;
import com.Messenger.Backend.repo.ChatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class ChatController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ChatRepository chatRepository;

    @MessageMapping("/private-message")
    private String receivePrivate(@Payload ReceivedMsg message){
        // Timestamp not updated, so currently null


        log.info("private-message called");
        log.info("message = {}",message);


        // Create dynamic topics
        String chatId = message.getChatId();
        ChatData chatData = chatRepository.findByChatId(chatId);

        String receiverId;
        if (message.getSenderId().equals(chatData.getUser1Id())){
            receiverId = chatData.getUser2Id();
        } else {
            receiverId = chatData.getUser1Id();
        }

        String privateChannel = "/user/" + receiverId + "/private/"+ message.getChatId();
        log.info(privateChannel);

        simpMessagingTemplate.convertAndSend(privateChannel,message.getChatId()); // The receiverId will be the prefix; /private is the destination
        // If the user needs to listen to this particular topic he need to listen at topic Name: /user/{ReceiverName}/private
        // Here, Receiver is the current user when he wants him to receive his message.
//        return message;
        log.info("Message Broadcasted");

        return message.getChatId();
    }

}
