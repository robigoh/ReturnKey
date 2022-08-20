/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.returnkey.repository;

import com.returnkey.model.Item;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Robi Goh
 */
public interface ItemRepository<P> extends CrudRepository<Item, Long> {

    @Query("SELECT a.price FROM Item a WHERE a.id = :id")
    BigDecimal getItemPrice(@Param("id") Long id);
    
    @Query("SELECT a FROM Item a WHERE a.sku = :sku")
    Item getItemBySku(@Param("sku") String sku);
}
