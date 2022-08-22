/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.returnkey.repository;

import com.returnkey.model.Order;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Robi Goh
 */
public interface OrderRepository<P> extends CrudRepository<Order, Long> {
    
    @Query("SELECT a FROM Order a WHERE a.orderId = :orderId AND a.emailAddress = :emailAddress")
    List<Order> findOrderByOrderIdAndEmail(@Param("orderId") String orderId, @Param("emailAddress") String emailAddress);
    
    @Query("SELECT a FROM Order a WHERE a.orderId = :orderId")
    List<Order> findOrderByOrderId(@Param("orderId") String orderId);
    
    @Modifying
    @Query("UPDATE Order a SET a.returned = true WHERE a.id = :id")
    void updateOrderToRetured(@Param("id") Long id);
}
