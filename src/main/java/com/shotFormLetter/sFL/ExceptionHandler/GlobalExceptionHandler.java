package com.shotFormLetter.sFL.ExceptionHandler;


import com.shotFormLetter.sFL.domain.post.domain.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    MessageDto messageDto = new MessageDto();
    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<MessageDto> handleDataNotFoundException(DataNotFoundException ex){
        log.error(ex.getMessage());
        messageDto.setMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(messageDto);
    }


    @ExceptionHandler(DataNotMatchException.class)
    public ResponseEntity<MessageDto> handleDataNotMatchException(DataNotMatchException ex){
        log.error(ex.getMessage());
        messageDto.setMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(messageDto);
    }

    @ExceptionHandler(DataNotUploadException.class)
    public ResponseEntity<MessageDto> handleDataNotUploadException(DataNotUploadException ex){
        log.error(ex.getMessage());
        messageDto.setMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(messageDto);
    }

    @ExceptionHandler({JSONException.class, IOException.class})
    public ResponseEntity<MessageDto> handleJsonException(Exception ex) {
        messageDto.setMessage("오류가 발생했습니다: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageDto);
    }

    @ExceptionHandler(DataNotAccessException.class)
    public ResponseEntity<MessageDto> handleDataNotAccessException(DataNotAccessException ex){
        messageDto.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageDto);
    }

}
