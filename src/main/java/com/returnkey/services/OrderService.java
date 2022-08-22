/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.returnkey.services;

import com.returnkey.model.Order;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.returnkey.repository.OrderRepository;

/**
 *
 * @author Robi Goh
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository<Order> orderRepository;

//    @Transactional
//    public List<Order> getAllOrder() {
//        return (List<Order>) orderRepository.findAll();
//    }
//
//    @Transactional
//    public List<Order> findByName(String name) {
//        return orderRepository.findOrder(name);
//    }
//
//    @Transactional
//    public boolean addOrder(Order order) {
//        return orderRepository.save(order) != null;
//    }
//
//    @Transactional
//    public void deleteOrder(Long orderId) {
//        orderRepository.deleteById(orderId);
//    }

    @Transactional
    public List<Order> findByOrderIdAndEmail(Order order) {
        return orderRepository.findOrderByOrderIdAndEmail(order.getOrderId(), order.getEmailAddress());
    }

    @Transactional
    public List<Order> findOrderByOrderId(String orderId) {
        return (List<Order>) orderRepository.findOrderByOrderId(orderId);
    }
    
    @Transactional
    public void updateReturn(Order order) {
        orderRepository.save(order);
    }
    
    @Transactional
    public void delAll() {
        orderRepository.deleteAll();
    }
    
    @Transactional
    public void updateOrderToRetured(Long id) {
        orderRepository.updateOrderToRetured(id);
    }
}
