package com.example.Centrifugo.Message;

import com.example.Centrifugo.dto.MessageDTO;
import com.example.Centrifugo.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface MessageService {

    public ResponseEntity<ResponseDTO> sendMessage(MessageDTO messageDTO);

    public ResponseEntity<ResponseDTO> getMessages(UUID id);
}
