package com.support.desk.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketCommentDTO {

    @Size(max = 1000)
    private String content;
    private LocalDateTime createdAt;
}