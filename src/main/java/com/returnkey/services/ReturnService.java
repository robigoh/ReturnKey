/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.returnkey.services;

import com.returnkey.model.Order;
import com.returnkey.model.Return;
import com.returnkey.repository.ReturnRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Robi Goh
 */
@Service
public class ReturnService {
    
    @Autowired
    private ReturnRepository<Return> returnRepository;
    
    @Transactional
    public String addReturn(Return ret) {
        return returnRepository.save(ret).getToken();
    }

    @Transactional
    public Return findPendingReturn(Return ret) {
        return returnRepository.findPendingReturn(ret.getToken());
    }
    
    @Transactional
    public void updateReturn(Return ret) {
        returnRepository.save(ret);
    }
    
    @Transactional
    public Return getReturnById(Long id) {
        return returnRepository.getReturnById(id);
    }
    
    @Transactional
    public void delAll() {
        returnRepository.deleteAll();
    }
}
