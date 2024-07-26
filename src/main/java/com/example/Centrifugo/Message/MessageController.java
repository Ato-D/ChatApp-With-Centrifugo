package com.example.Centrifugo.Message;


import com.example.Centrifugo.dto.MessageDTO;
import com.example.Centrifugo.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send-message")
    public ResponseEntity<ResponseDTO> sendMessage(@RequestBody MessageDTO messageDTO) {
        return messageService.sendMessage(messageDTO);
    }

    @GetMapping("/inbox/{userId}")
    public ResponseEntity<ResponseDTO> getMessagesForUser(@PathVariable UUID userId) {
        return messageService.getMessgesForUser(userId);
    }

    @GetMapping("/conversation")
    public ResponseEntity<ResponseDTO> getConversation(@RequestParam UUID userId1, @RequestParam UUID userId2) {
        return messageService.getConversation(userId1, userId2);
    }


//    @GetMapping("/{recipientId}")
//    public ResponseEntity<ResponseDTO> receiveMessages(@PathVariable UUID recipientId) {
//        return messageService.getMessages(recipientId);
//    }
}
