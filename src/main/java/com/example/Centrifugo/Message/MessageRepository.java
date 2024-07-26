package com.example.Centrifugo.Message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {

    @Query(nativeQuery = true, value = "SELECT * FROM Message_Table WHERE receiver_id = :receiverId")
    List<MessageEntity> findByReceiverId(@Param("receiverId") UUID receiverId);

    @Query(nativeQuery = true, value = "SELECT m FROM MessageEntity m WHERE (m.senderId =:userID1 AND m.receiverId = :userId2) OR (m.senderId = :userId2 AND m.receiverId = :userId1)")
    List<MessageEntity> findConverstationBetweenUsers(UUID userId1, UUID userId2);
}
