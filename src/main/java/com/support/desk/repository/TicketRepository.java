package com.support.desk.repository;

import com.support.desk.model.Ticket;
import com.support.desk.model.TicketStatus;
import com.support.desk.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByCustomer(User customer);

    Ticket findByTicketId(Long ticketId);

    List<Ticket> findByAssignedAgent(User agent);

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByDepartment(String department);
}