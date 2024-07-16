package com.example.Centrifugo.Message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {

    @Query(nativeQuery = true, value = "SELECT * FROM MessageEntity WHERE receiverId = :receiverId")
    List<MessageEntity> findByRecipentId(@Param("receiverId") UUID receiverId);
}
