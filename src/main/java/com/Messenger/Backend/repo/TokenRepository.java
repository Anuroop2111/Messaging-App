package com.Messenger.Backend.repo;

import com.Messenger.Backend.entity.TokenData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface TokenRepository extends JpaRepository<TokenData, Long> {

    TokenData findByRefSeriesAndUsername(String refSeries, String username);

    void deleteByRefSeriesAndUsername(String refSeries, String username);
}
