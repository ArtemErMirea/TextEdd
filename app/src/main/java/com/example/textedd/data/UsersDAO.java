package com.example.textedd.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UsersDAO {
    @Query("SELECT * FROM users where displayName=:displayName")
    UserEntity findUserByName(String displayName);
    @Query("SELECT userId FROM users where displayName=:displayName AND password=:password")
    String findUserByInfo(String displayName, String password);
    @Insert
    void insertUserEntity(UserEntity... users);
}
