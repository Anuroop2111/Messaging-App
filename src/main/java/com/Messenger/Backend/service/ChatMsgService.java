package com.Messenger.Backend.service;

import com.Messenger.Backend.entity.ChatData;
import com.Messenger.Backend.entity.MessageData;
import com.Messenger.Backend.entity.UserData;
import com.Messenger.Backend.model.ChatMessage;
import com.Messenger.Backend.model.FriendChatInfo;
import com.Messenger.Backend.repo.ChatRepository;
import com.Messenger.Backend.repo.MessageRepository;
import com.Messenger.Backend.repo.UserRepository;
import com.Messenger.Backend.util.TimestampUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.List;


@Service
@Slf4j
public class ChatMsgService {
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final TimestampUtils timestampUtils;


    public ChatMsgService(UserRepository userRepository, ChatRepository chatRepository, MessageRepository messageRepository,TimestampUtils timestampUtils) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.timestampUtils = timestampUtils;

    }

    public String getUsername(String email) {
        UserData userData = userRepository.findByEmail(email);
        return userData.getUsername();
    }

    public String getFriendName(String id) {
        UserData userData = userRepository.findById(id);
        return userData.getUsername();
    }

    public String getUserId(String email) {
        UserData userData = userRepository.findByEmail(email);
        return userData.getId();
    }

    public List<String> getChatIds(String userId) {
        List<ChatData> chatDataList = chatRepository.findByUser1IdOrUser2Id(userId);
        List<String> chatIds = new ArrayList<>();
        for (ChatData chat : chatDataList) {
            chatIds.add(chat.getChatId());
        }
        return chatIds;
    }

    public List<FriendChatInfo> getFriendIds(String userId) {
        List<ChatData> chatDataList = chatRepository.findByUser1IdOrUser2Id(userId);
        List<FriendChatInfo> friendChatInfoList = new ArrayList<>();

        for (ChatData chat : chatDataList) {
            String user1Id = chat.getUser1Id();
            String user2Id = chat.getUser2Id();
            String friendUserId = user1Id.equals(userId) ? user2Id : user1Id;
            String friendUserName = getFriendName(friendUserId);

            friendChatInfoList.add(new FriendChatInfo(friendUserId, chat.getChatId(), friendUserName));

        }

        return friendChatInfoList;

//        List<String> friendIds = new ArrayList<>();
//        for (ChatData chat : chatDataList){
//            if (chat.getUser2Id().equals(userId)){
//                friendIds.add(chat.getUser1Id());
//            } else {
//                friendIds.add(chat.getUser2Id());
//            }
//        }
//        return friendIds;
    }


    // From a list of UserIds, get a corresponding list of User names
    public List<String> getUserNames(List<String> friendIds) {
        List<String> userNames = new ArrayList<>();
        for (String userId : friendIds) {
            UserData userData = userRepository.findById(userId);
            userNames.add(userData.getName());
        }
        return userNames;
    }

    public List<ChatMessage> getChatContent(String chatId) {
//        log.info("chatId = {}",chatId);
        List<ChatMessage> chatMessages = new ArrayList<>();
        List<MessageData> messageDatas = messageRepository.findByChatId(chatId);

        // Create a map to group messages by senderId and receiverId
        Map<String, List<MessageData>> groupedMessages = new HashMap<>();

        for (MessageData msg : messageDatas) {
//            log.info("msg = {}", msg);
            String key = msg.getSenderId() + "-" + msg.getReceiverId();
            groupedMessages.computeIfAbsent(key, k -> new ArrayList<>()).add(msg);
        }

        // Combine messages from sender and receiver into a single list
        groupedMessages.forEach((key, messages) -> {
            messages.sort(Comparator.comparing(MessageData::getTimestamp));
            List<ChatMessage> combinedMessages = new ArrayList<>();
            for (MessageData msg : messages) {
                ChatMessage chatMessage = new ChatMessage(msg.getContent(), msg.getSenderId(), msg.getReceiverId(), msg.getTimestamp());
                combinedMessages.add(chatMessage);
            }
            chatMessages.addAll(combinedMessages);
        });
        Collections.sort(chatMessages, Comparator.comparing(ChatMessage::getTimestamp));
        return chatMessages;
    }

    public ResponseEntity<Void> saveMessage(String chatId, String senderId, String content) {
        // Generate a random message ID
        String messageId = generateRandomMessageId();

        // Create a Timestamp
        LocalDateTime timestamp = LocalDateTime.now();
//        String formattedTimestamp = timestampUtils.formatLocalDateTime(timestamp);
//        LocalDateTime convertedTimestamp = timestampUtils.parseToLocalDateTime(formattedTimestamp);

        ChatData chatData = chatRepository.findByChatId(chatId);

        String receiverId;
        if (chatData.getUser1Id().equals(senderId)){
            receiverId = chatData.getUser2Id();
        } else {
            receiverId = chatData.getUser1Id();
        }

        log.info("Msg id = {}",messageId);
        log.info("SenderId = {}",senderId);
        log.info("ReceiverId = {}",receiverId);
        log.info("Content = {}",content);
        log.info("ChatId = {}",chatId);
        log.info("Timestamp = {}",timestamp);

        // Create a MessageData object
        MessageData messageData = new MessageData();
        messageData.setMsgId(messageId);
        messageData.setChatId(chatId);
        messageData.setSenderId(senderId);
        messageData.setReceiverId(receiverId);
        messageData.setContent(content);
        messageData.setTimestamp(timestamp);

        // Save the message to the repository
        messageRepository.save(messageData);

        log.info("no issues");
        return ResponseEntity.ok().build();
    }

    private String generateRandomMessageId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder messageId = new StringBuilder();
        Random random = new Random();
        int length = 8;

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            messageId.append(characters.charAt(index));
        }
        return messageId.toString();
    }
}