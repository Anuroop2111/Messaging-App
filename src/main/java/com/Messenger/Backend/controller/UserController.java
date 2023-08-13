package com.Messenger.Backend.controller;

import com.Messenger.Backend.model.ChatMessage;
import com.Messenger.Backend.model.FriendChatInfo;
import com.Messenger.Backend.repo.UserRepository;
import com.Messenger.Backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/getChatNames/{email}")
    public List<FriendChatInfo> getChatNames(@PathVariable String email) {
        log.info("Username = {}", email);
        String userId = userService.getUserId(email);
        List<FriendChatInfo> chatInfo = userService.getFriendIds(userId);

        return chatInfo;
    }

    @GetMapping("/getMessage/{chatId}")
    public List<ChatMessage> getChatMessages(@PathVariable String chatId) {
        log.info("Chat id = {}", chatId);
        List<ChatMessage> chatMessage = userService.getChatContent(chatId);

        return chatMessage;
    }
}