package com.example.Centrifugo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseDTO {

    private int statusCode;
    private String message;
    private Object data;
    private ZonedDateTime date;

    public ResponseDTO(ResponseDTO responseDTO, HttpStatus badRequest){

    }
}
