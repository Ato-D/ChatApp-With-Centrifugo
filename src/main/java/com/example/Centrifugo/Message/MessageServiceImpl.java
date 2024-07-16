package com.example.Centrifugo.Message;


import com.example.Centrifugo.CentrifugoPublisher;
import com.example.Centrifugo.dto.MessageDTO;
import com.example.Centrifugo.dto.ResponseDTO;
import com.example.Centrifugo.utility.ObjectNotValidException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.Centrifugo.utility.AppUtils.getResponseDTO;

@Service
@AllArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService{

    private final MessageRepository messageRepository;
    private final CentrifugoPublisher centrifugoPublisher;
    @Override
    public ResponseEntity<ResponseDTO> sendMessage(MessageDTO messageDTO) {
        log.info("Inside the Send Message Method ::: Trying to send a message");
        ResponseDTO respose;
        try{
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setSenderId(messageDTO.getSenderId());
            messageEntity.setReceiverId(messageDTO.getReceiverId());
            messageEntity.setMessage(messageDTO.getMessage());
            var record =  messageRepository.save(messageEntity);
           centrifugoPublisher.sendImmobilizationToCentrifugo(record, "save");
            log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.CREATED, record);
            respose = getResponseDTO("Record Saved Successfully", HttpStatus.OK, record);

        } catch (ResponseStatusException e) {
            log.error("Error Occured! statusCode -> {}, Message -> {}, Reason -> {}", e.getStatusCode(), e.getMessage(), e.getReason());
            respose = getResponseDTO(e.getReason(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (ObjectNotValidException e) {
            var message = String.join("\n", e.getErrorMessages());
            log.info("Exception Occured! Reason -> {}", message);
            respose = getResponseDTO(message, HttpStatus.BAD_REQUEST);

        } catch (DataIntegrityViolationException e) {
            log.error("Exception Occured! Message -> {} and Cause -> {}", e.getMostSpecificCause(), e.getMessage());
            respose = getResponseDTO(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Exception Occured! statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            respose = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(respose, HttpStatus.valueOf(respose.getStatusCode()));
    }

    @Override
    public ResponseEntity<ResponseDTO> getMessages(UUID recipientId) {
        log.info("Inside find Get Messages by Id ::: Trying to Get Messages By Their Recipient id -> {}", recipientId);
        ResponseDTO response = new ResponseDTO();

        try {
            List<MessageEntity> messageEntities;
            messageEntities = messageRepository.findByRecipentId(recipientId);
            if (!messageEntities.isEmpty()) {
                log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.OK, messageEntities);
                List<MessageDTO> messageDTOS = messageEntities.stream()
                        .map(messageEntity -> {
                            MessageDTO messageDTO = new MessageDTO();
                            messageDTO.setSenderId(messageEntity.getSenderId());
                            messageDTO.setReceiverId(messageEntity.getReceiverId());
                            messageDTO.setMessage(messageEntity.getMessage());
                            return messageDTO;
                        }).collect(Collectors.toList());
                response = getResponseDTO("Successfully retreived all the messages with recipientid " + recipientId, HttpStatus.OK, messageDTOS);
            }
        } catch (ResponseStatusException e) {
            log.error("Exception Occured! and Message -> {} and Cause -> {}", e.getMessage(), e.getReason());
            response = getResponseDTO(e.getMessage(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (Exception e) {
            log.error("Exception Occured! StatusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }
    }
//        List<MessageDTO> messageDTOS = new ArrayList<>();
//        for(MessageEntity messageEntity: messageEntities) {;
//            MessageDTO messageDTO = new MessageDTO();
//            messageDTO.setSenderId(messageEntity.getSenderId());
//            messageDTO.setReceiverId(messageEntity.getReceiverId());
//            messageDTO.setMessage(messageEntity.getMessage());
//        }
//        return messageDTOS;
//    }
//}
