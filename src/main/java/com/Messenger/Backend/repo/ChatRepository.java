package com.Messenger.Backend.repo;

import com.Messenger.Backend.entity.ChatData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatData, String> {
    List<ChatData> findByUser1Id(String userId);
}
