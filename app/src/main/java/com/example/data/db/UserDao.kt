package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profiles WHERE id = 'user_priya_1' LIMIT 1")
    fun getCurrentUser(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE id = 'user_priya_1' LIMIT 1")
    suspend fun getCurrentUserSync(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserProfile)

    @Update
    suspend fun updateUser(user: UserProfile)

    @Query("UPDATE user_profiles SET role = :role WHERE id = 'user_priya_1'")
    suspend fun updateRole(role: UserRole)

    @Query("UPDATE user_profiles SET city = :city, locality = :locality WHERE id = 'user_priya_1'")
    suspend fun updateLocation(city: String, locality: String)
}
