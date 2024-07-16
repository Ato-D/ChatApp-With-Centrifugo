package com.example.Centrifugo.Message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {

    @Query(nativeQuery = true, value = "SELECT * FROM Message_Table WHERE receiver_id = :receiverId")
    List<MessageEntity> findByRecipientId(@Param("receiverId") UUID receiverId);
}
