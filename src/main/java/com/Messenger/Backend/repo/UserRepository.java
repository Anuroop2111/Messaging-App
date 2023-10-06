package com.Messenger.Backend.repo;

import com.Messenger.Backend.entity.UserData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
@Transactional
@Slf4j
public class UserRepository{
    @PersistenceContext
    private EntityManager entityManager;

//    UserData findByEmail(String email);

    public UserData findByEmail(String email) {
        try{
            String query = "SELECT id, username,name,password,phone,email FROM users where email=?1";
            Object[] singleResult = (Object[]) entityManager.createNativeQuery(query).setParameter(1, email).getSingleResult();
            return getUserData(singleResult);
        } catch (NoResultException e){
            log.warn("No entity found for user: {}",email);
            return null;
        }
    }

    private UserData getUserData(Object[] objects) {
        return new UserData((String) objects[0],
                (String) objects[1],
                (String) objects[2],
                (String) objects[3],
                (String) objects[4],
                (String) objects[5]);
    }

    public UserData findById(String id) {
        String query = "SELECT  id, username,name,password,phone,email FROM users where id=?1";
        Object[] singleResult = (Object[]) entityManager.createNativeQuery(query).setParameter(1, id).getSingleResult();
        return getUserData(singleResult);
    }

    public UserData save(UserData userData) {
        String query = "INSERT INTO users (id, username,name,password,phone,email) " +
                "VALUES (:id, :username, :name , :password, :phone, :email)";

        entityManager.createNativeQuery(query)
                .setParameter("id", userData.getId())
                .setParameter("username", userData.getUsername())
                .setParameter("name", userData.getName())
                .setParameter("password", userData.getPassword())
                .setParameter("phone", userData.getPhone())
                .setParameter("email", userData.getEmail())

                .executeUpdate();

        return userData;
    }


}
