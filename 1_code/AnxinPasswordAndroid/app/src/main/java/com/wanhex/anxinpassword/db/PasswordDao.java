package com.wanhex.anxinpassword.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PasswordDao {

    @Query("SELECT * FROM password order by timestamp desc")
    List<Password> getAll();

    @Query("SELECT * FROM password WHERE id IN (:ids)")
    List<Password> loadAllByIds(int[] ids);

    @Query("SELECT * FROM password WHERE site LIKE :site ")
    List<Password> findBySite(String site);

    @Insert
    void insertAll(Password... passwords);

    @Insert
    void insert(Password password);

    @Delete
    void delete(Password password);

    @Update
    void update(Password password);
}

