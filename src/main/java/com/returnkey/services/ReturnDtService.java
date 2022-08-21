/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.returnkey.services;

import com.returnkey.model.ReturnDt;
import com.returnkey.repository.ReturnDtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Robi Goh
 */
@Service
public class ReturnDtService {

    @Autowired
    private ReturnDtRepository<ReturnDt> returnDtRepository;
    
    @Transactional
    public void addOrSaveReturnDt(ReturnDt retDt) {
        returnDtRepository.save(retDt);
    }

    @Transactional
    public ReturnDt findReturnDtByReturnIdAndItemId(Long retId, Long itemId) {
        return returnDtRepository.findReturnDtByReturnIdAndItemId(retId, itemId);
    }
    
    @Transactional
    public void delAll() {
        returnDtRepository.deleteAll();
    }
}
