package com.example.textedd.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Users")
public class UserEntity {
    @PrimaryKey
    @NonNull
    String userId;
    String displayName;
    String password;

    public UserEntity(String displayName,
                      String password){
        this.userId = java.util.UUID.randomUUID().toString();
        this.displayName = displayName;
        this.password = password;
    }



}
