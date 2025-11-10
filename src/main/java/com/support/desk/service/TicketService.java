package com.support.desk.service;

import com.support.desk.dto.TicketCommentDTO;
import com.support.desk.dto.TicketDTO;
import com.support.desk.dto.TicketDetailsUpdateDTO;
import com.support.desk.dto.TicketEmpDTO;
import com.support.desk.exception.ResourceNotFoundException;
import com.support.desk.model.*;
import com.support.desk.repository.TicketCommentRepository;
import com.support.desk.repository.TicketRepository;
import com.support.desk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketCommentRepository ticketCommentRepository;

    private static final Logger logger = LogManager.getLogger(TicketService.class);

    @Transactional
    public TicketDTO createTicket(TicketDTO ticketDTO,Long userId) {
        User customer = userRepository.findById(userId).get();
        Ticket ticket = new Ticket();
        ticket.setTicketId(generateFourDigitNumber());
        ticket.setTitle(ticketDTO.getTitle());
        ticket.setDescription(ticketDTO.getDescription());
        ticket.setPriority(TicketPriority.LOW);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCustomer(customer);
        ticket.setCreationTime(LocalDateTime.now());
        ticket.setResolutionTime(LocalDateTime.now().plusHours(48));
        logger.info("Creating ticket: {} for user: {}", ticket.getTicketId(), customer.getEmail());
        Ticket savedTicket = ticketRepository.save(ticket);
        logger.info("Ticket created: {}", savedTicket.getTicketId());
        return convertToDTO(savedTicket);
    }

    @Transactional
    public TicketDTO assignTicket(Long ticketId, Long agentId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> {
                    logger.error("Ticket not found with id: {}", ticketId);
                    return new ResourceNotFoundException("Ticket not found with id: " + ticketId);
                });

        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> {
                    logger.error("Agent not found with id: {}", agentId);
                    return new ResourceNotFoundException("Agent not found with id: " + agentId);
                });
        logger.info("Assigning ticket {} to agent {}", ticketId, agent.getEmail());
        ticket.setAssignedAgent(agent);
        ticket.setDepartment(agent.getDepartment());
        Ticket updatedTicket = ticketRepository.save(ticket);
        logger.info("Ticket {} assigned to agent {}", ticketId, agent.getEmail());
        return convertToDTO(updatedTicket);
    }

    @Transactional
    public String updateTicket(TicketDetailsUpdateDTO ticketDetailsUpdateDTO) {
        Ticket ticket = ticketRepository.findByTicketId(ticketDetailsUpdateDTO.getTicketId());

        if(!ticket.getStatus().equals(TicketStatus.RESOLVED)){
            logger.info("Updating ticket: {}", ticket.getTicketId());
            if(ticketDetailsUpdateDTO.getStatus()!=ticket.getStatus() && ticketDetailsUpdateDTO.getStatus()!=null){
                ticket.setStatus(ticketDetailsUpdateDTO.getStatus());
                logger.info("Ticket {} status updated to {}", ticket.getTicketId(), ticket.getStatus());
                if(ticket.getStatus().equals(TicketStatus.RESOLVED)){
                    ticket.setResolutionTime(LocalDateTime.now());
                    logger.info("Ticket {} marked as RESOLVED", ticket.getTicketId());
                }
            }
            if (!ticketDetailsUpdateDTO.getContent().isEmpty()){
                TicketComment ticketComment = new TicketComment();
                ticketComment.setTicket(ticket);
                ticketComment.setUser(ticket.getCustomer());
                ticketComment.setContent(ticketDetailsUpdateDTO.getContent());
                ticketComment.setCreatedAt(LocalDateTime.now());
                ticket.getComments().add(ticketComment);
                logger.info("Added comment to ticket {} by user {}", ticket.getTicketId(), ticket.getCustomer().getEmail());
            }
            if (ticketDetailsUpdateDTO.getPriority()!=ticket.getPriority() && ticketDetailsUpdateDTO.getPriority()!=null){
                ticket.setPriority(ticketDetailsUpdateDTO.getPriority());
                logger.info("Ticket {} priority changed to {}", ticket.getTicketId(), ticket.getPriority());
            }
            ticketRepository.save(ticket);
            return "Ticket details updated successfully";
        }
        else {
            logger.warn("Attempt to update already resolved ticket: {}", ticket.getTicketId());
            return "The Ticket with id "+ ticket.getTicketId()+" is already Resolved.";
        }
    }

    public List<TicketDTO> getTicketsAssociatedToCustomer(Long userId) {
        User customer = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with id: {}", userId);
                    return new ResourceNotFoundException("User not found with username: ");
                });

        logger.info("Retrieving tickets for customer id: {}", userId);

        List<Ticket> tickets = ticketRepository.findByCustomer(customer);
        return tickets.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<TicketEmpDTO> getTicketsByAgent(Long userId) {
        User agent = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Agent not found with id: {}", userId);
                    return new ResourceNotFoundException("Agent not found with id: " + userId);
                });
        logger.info("Retrieving tickets assigned to agent id: {}", userId);
        List<Ticket> tickets = ticketRepository.findByAssignedAgent(agent);
        return tickets.stream().map(this::convertToDTOs).collect(Collectors.toList());
    }

    public List<TicketDTO> getTicketsByStatus(TicketStatus status) {
        List<Ticket> tickets = ticketRepository.findByStatus(status);
        logger.info("Retrieving tickets by status: {} (count={})", status, tickets.size());
        return tickets.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<TicketDTO> getTicketsByDepartment(String department) {
        List<Ticket> tickets = ticketRepository.findByDepartment(department);
        logger.info("Retrieving tickets for department: {} (count={})", department, tickets.size());
        return tickets.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Long getTotalActiveTicketCount() {
         Integer size = ticketRepository.findByStatus(TicketStatus.OPEN).size();
        logger.info("Total active open tickets: {}", size);
        return size.longValue();
    }

    public List<TicketCommentDTO> getCommentsByTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findByTicketId(ticketId);
        if (ticket == null) {
            logger.error("Ticket not found when fetching comments for ticketId: {}", ticketId);
            throw new ResourceNotFoundException("Ticket not found with id: " + ticketId);
        }
        List<TicketComment> comments = ticketCommentRepository.findByTicketOrderByCreatedAtAsc(ticket);
        logger.info("Retrieved {} comments for ticket {}", comments.size(), ticketId);
        return comments.stream().map(this::convertToCommentDTO).collect(Collectors.toList());
    }

    private TicketDTO convertToDTO(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setTicketId(ticket.getTicketId());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setCreationTime(ticket.getCreationTime());
        dto.setResolutionTime(ticket.getResolutionTime());
        dto.setAssignedAgent(ticket.getAssignedAgent());
        dto.setStatus(ticket.getStatus());
        dto.setComments(ticket.getComments());
        return dto;
    }

    private TicketEmpDTO convertToDTOs(Ticket ticket) {
        TicketEmpDTO dto = new TicketEmpDTO();
        dto.setTicketId(ticket.getTicketId());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setCreationTime(ticket.getCreationTime());
        dto.setResolutionTime(ticket.getResolutionTime());
        dto.setPriority(ticket.getPriority());
        dto.setStatus(ticket.getStatus());
        dto.setCustomer(ticket.getCustomer());
        dto.setComments(ticket.getComments());
        return dto;
    }

    private TicketCommentDTO convertToCommentDTO(TicketComment comment) {
        TicketCommentDTO dto = new TicketCommentDTO();
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }

    public static Long generateFourDigitNumber() {
        Random random = new Random();
        return 1000 + random.nextLong(9000); // generates a number between 1000 and 9999
    }

}