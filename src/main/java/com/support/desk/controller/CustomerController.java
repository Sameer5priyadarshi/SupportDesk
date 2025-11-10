package com.support.desk.controller;

import com.support.desk.dto.TicketCommentDTO;
import com.support.desk.dto.TicketDTO;
import com.support.desk.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/raise/issue")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<TicketDTO> generateIssue(@Valid @RequestBody TicketDTO ticketDTO, @RequestParam Long userId) {
        return ResponseEntity.ok(ticketService.createTicket(ticketDTO,userId));
    }

    @GetMapping("/tickets/{userId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<TicketDTO>> getMyTickets(@PathVariable Long userId) {
        return ResponseEntity.ok(ticketService.getTicketsAssociatedToCustomer(userId));
    }

    @GetMapping("/comments/{ticketId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<TicketCommentDTO>> getComments(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ticketService.getCommentsByTicket(ticketId));
    }
}