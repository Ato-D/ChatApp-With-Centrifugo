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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.Centrifugo.utility.AppUtils.getAuthenticatedUserId;
import static com.example.Centrifugo.utility.AppUtils.getResponseDTO;

@Service
@AllArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService{

    private final MessageRepository messageRepository;
    private final CentrifugoPublisher centrifugoPublisher;

    /**
     * Sends a message using the provided MessageDTO.
     *
     * This method attempts to save the message to the database using MessageRepository.
     * If successful, it publishes the message to Centrifugo using CentrifugoPublisher.
     * Handles specific exceptions with appropriate status codes and logs relevant information.
     *
     * @param messageDTO The MessageDTO containing senderId, receiverId, and message to be saved and sent.
     * @return ResponseEntity containing a ResponseDTO with the status and details of the saved message.
     */
    @Override
    public ResponseEntity<ResponseDTO> sendMessage(MessageDTO messageDTO) {
        log.info("Inside the Send Message Method ::: Trying to send a message");
        ResponseDTO respose;
        try{
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setSenderId(getAuthenticatedUserId());
            messageEntity.setReceiverId(messageDTO.getReceiverId());
            messageEntity.setContent(messageDTO.getContent());
            messageEntity.setCreatedAt(ZonedDateTime.now());
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


    /**
     * Retrieves messages by receiver ID.
     *
     * This method attempts to retrieve messages from the repository based on the provided receiver ID.
     * If messages are found, they are converted into MessageDTO objects and returned with a success status.
     * Handles known exceptions with specific status codes and logs relevant information.
     *
     * @param receiverId The UUID of the receiver whose messages are to be retrieved.
     * @return ResponseEntity containing a ResponseDTO with the status and messages retrieved.
     */

    @Override
    public ResponseEntity<ResponseDTO> getMessgesForUser(UUID receiverId) {
        log.info("Inside find Get Messages by Id ::: Trying to Get Messages By Their Receiver id -> {}", receiverId);
        ResponseDTO response;

        try {
            List<MessageEntity> messageEntityList;
            messageEntityList =  messageRepository.findByReceiverId(receiverId);
            if (!messageEntityList.isEmpty()) {
                log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.OK, messageEntityList);
                List<MessageDTO> messageDTOS = messageEntityList.stream()
                        .map(messageEntity -> {
                            MessageDTO messageDTO = new MessageDTO();
                            messageDTO.setSenderId(messageEntity.getSenderId());
                            messageDTO.setReceiverId(messageEntity.getReceiverId());
                            messageDTO.setContent(messageEntity.getContent());
                            return messageDTO;
                        }).collect(Collectors.toList());
                response = getResponseDTO("Successfully retreived all the messages with receiverId " + receiverId, HttpStatus.OK, messageDTOS);
                return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
            }
            log.info("No record found! statusCode -> {} and Message -> {}", HttpStatus.NOT_FOUND);
            response = (getResponseDTO("Not Found!", HttpStatus.NOT_FOUND));
            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));

        } catch (ResponseStatusException e) {
            log.error("Exception Occured! and Message -> {} and Cause -> {}", e.getMessage(), e.getReason());
            response = getResponseDTO(e.getMessage(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (Exception e) {
            log.error("Exception Occured! StatusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            response = getResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }


    /**
     * This method retrieves the conversation between two users identified by their UUIDs.
     * It logs the process and handles exceptions appropriately, returning a ResponseEntity
     * with a ResponseDTO containing the conversation details or an error message.
     *
     * @param userId1 The UUID of the first user.
     * @param userId2 The UUID of the second user.
     * @return A ResponseEntity containing a ResponseDTO with the conversation details or an error message.
     */
    @Override
    public ResponseEntity<ResponseDTO> getConversation(UUID userId1, UUID userId2) {
        log.info("Inside find Get Conversation Method ::: Trying to Get Conversation Between Two Users -> {}, {}", userId1,userId2);

        ResponseDTO response;
        try {
            List<MessageEntity> conversationEntities = messageRepository.findConverstationBetweenUsers(userId1, userId2);
            if (!conversationEntities.isEmpty()) {
                List<MessageDTO> conversationDTOS = conversationEntities.stream()
                        .map(messageEntity -> {
                            MessageDTO messageDTO = new MessageDTO();
                            messageDTO.setSenderId(messageEntity.getSenderId());
                            messageDTO.setReceiverId(messageEntity.getReceiverId());
                            messageDTO.setContent(messageEntity.getContent());
                            return messageDTO;
                        }).collect(Collectors.toList());
                log.info("Success! statusCode -> {} and Message -> {}", HttpStatus.OK, conversationDTOS);
                response =  getResponseDTO("Successfully retrieved conversation between users " + userId1 + " and " + userId2, HttpStatus.OK, conversationDTOS);
                return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
            } else {
                response = getResponseDTO("No conversation found between users " + userId1 + " and " + userId2, HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
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

