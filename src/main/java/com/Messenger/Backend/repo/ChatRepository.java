package com.Messenger.Backend.repo;

import com.Messenger.Backend.entity.ChatData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatData, String> {
    @Query("SELECT c FROM ChatData c WHERE c.user1Id = :userId OR c.user2Id = :userId")
    List<ChatData> findByUser1IdOrUser2Id(@Param("userId") String userId);

    ChatData findByChatId(String chatId);

}
