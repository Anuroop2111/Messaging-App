package com.Messenger.Backend.service;

import com.Messenger.Backend.entity.ChatData;
import com.Messenger.Backend.entity.MessageData;
import com.Messenger.Backend.entity.UserData;
import com.Messenger.Backend.model.ChatMessage;
import com.Messenger.Backend.model.FriendChatInfo;
import com.Messenger.Backend.repo.ChatRepository;
import com.Messenger.Backend.repo.MessageRepository;
import com.Messenger.Backend.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    public UserService(UserRepository userRepository, ChatRepository chatRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
    }

    public String getUsername(String email) {
        UserData userData = userRepository.findByEmail(email);
        return userData.getUsername();
    }

    public String getFriendName(String id){
        Optional<UserData> userData = userRepository.findById(id);
        return userData.get().getUsername();
    }

    public String getUserId(String email){
        UserData userData = userRepository.findByEmail(email);
        return userData.getId();
    }

    public List<String> getChatIds(String userId){
        List<ChatData> chatDataList = chatRepository.findByUser1IdOrUser2Id(userId);
        List<String> chatIds = new ArrayList<>();
        for (ChatData chat : chatDataList){
            chatIds.add(chat.getChatId());
        }
        return chatIds;
    }

    public List<FriendChatInfo> getFriendIds(String userId){
        List<ChatData> chatDataList = chatRepository.findByUser1IdOrUser2Id(userId);
        List<FriendChatInfo> friendChatInfoList = new ArrayList<>();

        for (ChatData chat : chatDataList) {
            String user1Id = chat.getUser1Id();
            String user2Id = chat.getUser2Id();
            String friendUserId = user1Id.equals(userId) ? user2Id:user1Id;
            String friendUserName = getFriendName(friendUserId);

            friendChatInfoList.add(new FriendChatInfo(friendUserId,chat.getChatId(),friendUserName));

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
    public List<String> getUserNames(List<String> friendIds){
        List<String> userNames = new ArrayList<>();
        for (String userId : friendIds){
            Optional<UserData> userData = userRepository.findById(userId);
            userNames.add(userData.get().getName());
        }
        return userNames;
    }

    public List<ChatMessage> getChatContent(String chatId){
        log.info("chatId = {}",chatId);
        List<ChatMessage> chatMessages = new ArrayList<>();
        List<MessageData> messageDatas = messageRepository.findByChatId(chatId);

        // Create a map to group messages by senderId and receiverId
        Map<String,List<MessageData>> groupedMessages = new HashMap<>();

        for (MessageData msg : messageDatas) {
            log.info("msg = {}", msg);
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
}