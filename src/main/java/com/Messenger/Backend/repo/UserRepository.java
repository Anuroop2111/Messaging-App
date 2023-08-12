package com.Messenger.Backend.repo;

import com.Messenger.Backend.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserData, String> {

    UserData findByEmail(String email);
}
