package com.support.desk.dto;

import com.support.desk.model.TicketPriority;
import com.support.desk.model.TicketStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketDetailsUpdateDTO {

    private Long ticketId;
    @Size(max = 1000)
    private String content;
    private LocalDateTime createdAt;
    private TicketPriority priority;
    private TicketStatus status;
}
