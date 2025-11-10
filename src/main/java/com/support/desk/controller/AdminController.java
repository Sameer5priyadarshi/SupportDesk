package com.support.desk.controller;

import com.support.desk.dto.TicketDTO;
import com.support.desk.dto.TicketEmpDTO;
import com.support.desk.model.TicketStatus;
import com.support.desk.service.TicketService;
import com.support.desk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    @GetMapping("/tickets")
    public ResponseEntity<List<TicketDTO>> getAllOpenTickets() {
        return ResponseEntity.ok(ticketService.getTicketsByStatus(TicketStatus.OPEN));
    }

    @GetMapping("/tickets/status/{status}")
    public ResponseEntity<List<TicketDTO>> getTicketsByStatus(@PathVariable String status) {
        TicketStatus ticketStatus = TicketStatus.valueOf(status);
        return ResponseEntity.ok(ticketService.getTicketsByStatus(ticketStatus));
    }

    @GetMapping("/tickets/department/{department}")
    public ResponseEntity<List<TicketDTO>> getTicketsByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(ticketService.getTicketsByDepartment(department));
    }

    @GetMapping("/tickets/agent/{userId}")
    public ResponseEntity<List<TicketEmpDTO>> getTicketsByAgent(@PathVariable Long userId) {
        return ResponseEntity.ok(ticketService.getTicketsByAgent(userId));
    }

    @GetMapping("/tickets/count")
    public Long getTotalActiveTickets() {
        return ticketService.getTotalActiveTicketCount();
    }

    @PutMapping("/ticket/assign")
    public ResponseEntity<TicketDTO> assignTicket(@RequestParam Long ticketId, @RequestParam Long userId) {
        return ResponseEntity.ok(ticketService.assignTicket(ticketId, userId));
    }
}