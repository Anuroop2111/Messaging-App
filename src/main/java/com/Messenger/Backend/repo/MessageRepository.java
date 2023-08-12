package com.Messenger.Backend.repo;

import com.Messenger.Backend.entity.MessageData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageData,String> {
    List<MessageData> findByChatId(String chatId);
}
