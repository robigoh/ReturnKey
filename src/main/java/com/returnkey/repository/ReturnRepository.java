/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.returnkey.repository;

import com.returnkey.model.Order;
import com.returnkey.model.Return;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Robi Goh
 */
public interface ReturnRepository <P> extends CrudRepository<Return, Long> {
    
    @Query("SELECT a FROM Return a WHERE a.token = :token")
    Return findPendingReturn(@Param("token") String token);
    
    @Query("SELECT a FROM Return a WHERE a.id = :id")
    Return getReturnById(@Param("id") Long id);
}
