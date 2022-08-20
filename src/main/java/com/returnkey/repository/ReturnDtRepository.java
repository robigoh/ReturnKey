/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.returnkey.repository;

import com.returnkey.model.ReturnDt;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Robi Goh
 */
public interface ReturnDtRepository<P> extends CrudRepository<ReturnDt, Long> {
    
    @Query("SELECT a FROM ReturnDt a WHERE a.returns.id = :retId AND a.item.id = :itemId")
    ReturnDt findReturnDtByReturnIdAndItemId(@Param("retId") Long retId, @Param("itemId") Long itemId);
    
}