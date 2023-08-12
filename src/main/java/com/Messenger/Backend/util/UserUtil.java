//package com.Messenger.Backend.util;
//
//import com.Messenger.Backend.entity.ChatData;
//import com.Messenger.Backend.entity.MessageData;
//import com.Messenger.Backend.entity.UserData;
//import com.Messenger.Backend.repo.ChatRepository;
//import com.Messenger.Backend.repo.MessageRepository;
//import com.Messenger.Backend.repo.UserRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//@Component
//@Slf4j
//public class UserUtil {
//    private final UserRepository userRepository;
//    private final ChatRepository chatRepository;
//    private final MessageRepository messageRepository;
//
//    public UserUtil(UserRepository userRepository, ChatRepository chatRepository, MessageRepository messageRepository) {
//        this.userRepository = userRepository;
//        this.chatRepository = chatRepository;
//        this.messageRepository = messageRepository;
//    }
//
//
//    public String getUsername(String email) {
//        UserData userData = userRepository.findByEmail(email);
//        return userData.getUsername();
//    }
//
//    public String getUserId(String email){
//        UserData userData = userRepository.findByEmail(email);
//        return userData.getId();
//    }
//
//    public List<String> getChatIds(String userId){
//        List<ChatData> chatDataList = chatRepository.findByUser1Id(userId);
//        List<String> chatIds = new ArrayList<>();
//        for (ChatData chat : chatDataList){
//            chatIds.add(chat.getChatId());
//        }
//        return chatIds;
//    }
//
//    public List<String> getFriendIds(String userId){
//        List<ChatData> chatDataList = chatRepository.findByUser1Id(userId);
//        List<String> friendIds = new ArrayList<>();
//        for (ChatData chat : chatDataList){
//            friendIds.add(chat.getUser2Id());
//        }
//        return friendIds;
//    }
//
//    // From a list of UserIds, get a corresponding list of User names
//    public List<String> getUserNames(List<String> userIds){
//        List<String> userNames = new ArrayList<>();
//        for (String userId : userIds){
//            Optional<UserData> userData = userRepository.findById(userId);
//            userNames.add(userData.get().getName());
//        }
//        return userNames;
//    }
//
//    public List<String> getChatContent(String chatId){
//        List<String> chatContent = new ArrayList<>();
//        List<MessageData> messageDatas = messageRepository.findByChatId(chatId);
//        for (MessageData msg : messageDatas){
//            chatContent.add(msg.getContent());
//        }
//        return chatContent;
//    }
//}
