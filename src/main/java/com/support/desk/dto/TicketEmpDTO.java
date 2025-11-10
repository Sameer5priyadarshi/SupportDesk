package com.support.desk.dto;

import com.support.desk.model.TicketComment;
import com.support.desk.model.TicketPriority;
import com.support.desk.model.TicketStatus;
import com.support.desk.model.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TicketEmpDTO {
    private Long ticketId;
    private String title;
    private String description;
    private LocalDateTime creationTime;
    private LocalDateTime resolutionTime;
    private TicketPriority priority;
    private User customer;
    private TicketStatus status;
    private List<TicketComment> comments = new ArrayList<>();
}
