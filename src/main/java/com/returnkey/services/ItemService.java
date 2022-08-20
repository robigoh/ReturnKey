/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.returnkey.services;

import com.returnkey.model.Item;
import com.returnkey.repository.ItemRepository;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Robi Goh
 */
@Service
public class ItemService {

    @Autowired
    private ItemRepository<Item> itemRepository;
    
    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }
    
    @Transactional
    public BigDecimal getItemPrice(Long itemId) {
        return itemRepository.getItemPrice(itemId);
    }
    
    @Transactional
    public Item getItemPrice(String sku) {
        return itemRepository.getItemBySku(sku);
    }
    
    @Transactional
    public void delAll() {
        itemRepository.deleteAll();
    }
}
