package com.support.desk.controller;

import com.support.desk.dto.TicketDetailsUpdateDTO;
import com.support.desk.dto.TicketEmpDTO;
import com.support.desk.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@PreAuthorize("hasRole('EMPLOYEE')")
public class EmployeeController {

    @Autowired
    private TicketService ticketService;

    @GetMapping("/tickets/{userId}")
    public ResponseEntity<List<TicketEmpDTO>> getAssignedTickets(@PathVariable Long userId) {
        return ResponseEntity.ok(ticketService.getTicketsByAgent(userId));
    }

    @PutMapping("/ticket/update")
    public String updateTicket(@RequestBody TicketDetailsUpdateDTO ticketDetailsUpdateDTO) {
        return ticketService.updateTicket(ticketDetailsUpdateDTO);
    }

}
