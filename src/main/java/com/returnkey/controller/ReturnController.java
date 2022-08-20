package com.returnkey.controller;

import com.returnkey.model.Item;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.returnkey.model.Order;
import com.returnkey.model.Return;
import com.returnkey.model.ReturnDt;
import com.returnkey.services.ItemService;
import com.returnkey.services.OrderService;
import com.returnkey.services.ReturnDtService;
import com.returnkey.services.ReturnService;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class ReturnController {
    
    private static final String STATUS_WAITING = "WAITING_APPROVAL";
    private static final String STATUS_COMPLETE = "COMPLETE";
    
    private static final String QC_STATUS_ACCEPTED = "ACCEPTED";
    private static final String QC_STATUS_REJECTED = "REJECTED";
    
    @Autowired
    OrderService orderService;
    @Autowired
    ReturnService returnService;
    @Autowired
    ReturnDtService returnDtService;
    @Autowired
    ItemService itemService;
    
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public String importReturns() {
        try {
            Scanner sc = new Scanner(new File("C:\\Users\\Robi Goh\\Desktop\\orders.csv"));
            //skip header
            sc.nextLine();
            
            String line[];
            String orderId, email, sku, qty, price, itemName;
            Order o;
            Item i;
            
            while (sc.hasNextLine()) {
                line = sc.nextLine().split(",");
                if (line.length > 1) {
                    o = new Order();
                    
                    orderId = line[0];
                    email = line[1];
                    sku = line[2];
                    qty = line[3];
                    price = line[4];
                    itemName = line[5];
                    
                    i = itemService.getItemPrice(sku);
                    if (i == null) {
                        i = new Item();
                        
                        i.setSku(sku);
                        i.setPrice(new BigDecimal(price));
                        i.setItemName(itemName);
                        itemService.saveItem(i);
                    }
                    
                    o.setItem(i);
                    o.setOrderId(orderId);
                    o.setEmailAddress(email);
                    o.setQuantity(Integer.parseInt(qty));
                    
                    orderService.updateReturn(o);
                }
            }
            sc.close();
        } catch (Exception e) {
            System.out.print(e);
        }
        return "Import Successful";
    }
    
    @RequestMapping(value = "/pending/returns", method = RequestMethod.POST)
    public String pendingReturns(@RequestBody Order order) {
        Return ret;
        if (!orderService.findByOrderIdAndEmail(order).isEmpty()) {
            ret = new Return();
            UUID uuid = UUID.randomUUID();
            String uuidAsString = uuid.toString();
            ret.setStatus(STATUS_WAITING);
            ret.setToken(uuidAsString);
            ret.setOrderId(order.getOrderId());
            return returnService.addReturn(ret);
        }
        return "Invalid order id/email address";
    }
    
    @RequestMapping(value = "/returns", method = RequestMethod.POST)
    public String returns(@RequestBody Return ret) {
        Return returnPending = returnService.findPendingReturn(ret);
        boolean found;
        boolean enough;
        boolean isReturned;
        boolean wrongQty;
        List<Order> orderList = new ArrayList<>();
        
        if (returnPending != null) {
            if (returnPending.getStatus().equalsIgnoreCase(STATUS_COMPLETE)) {
                return returnPending.getToken() + " status has been complete";
            }
            List<Order> orders = orderService.findOrderByOrderId(returnPending.getOrderId());
            for (ReturnDt rDt : ret.getReturnDt()) {
                found = false;
                enough = false;
                isReturned = false;
                wrongQty = false;
                for (Order o : orders) {
                    if (rDt.getItem().getId() == o.getItem().getId()) {
                        found = true;
                        o.setQuantity(rDt.getQuantity());
                        orderList.add(o);
                        if (rDt.getQuantity() <= o.getQuantity()) {
                            enough = true;
                        }
                        if (o.isReturned()) {
                            isReturned = true;
                        }
                        if (rDt.getQuantity() < 1) {
                            wrongQty = true;
                        }
                        break;
                    }
                }
                if (!found) {
                    return "Item id " + rDt.getItem().getId() + " not found in order id " + returnPending.getOrderId();
                } 
                if (!enough) {
                    return "Item id " + rDt.getItem().getId() + " not enough to do return";
                }
                if (isReturned) {
                    return "Item id " + rDt.getItem().getId() + " has been returned before";
                }
                if (wrongQty) {
                    return "Item id " + rDt.getItem().getId() + " should return in positive value";
                }
            }
            returnPending.setStatus(STATUS_COMPLETE);
            returnService.updateReturn(returnPending);
            ReturnDt retDt;
            BigDecimal totalRefund = BigDecimal.ZERO;
            
            for (Order o : orderList) {
                o.setReturned(true);
                orderService.updateReturn(o);
                
                retDt = new ReturnDt();
                retDt.setReturns(returnPending);
                retDt.setItem(o.getItem());
                retDt.setQuantity(o.getQuantity());
                retDt.setQcStatus(QC_STATUS_ACCEPTED); // Set default qc status to Accepted
                returnDtService.addOrSaveReturnDt(retDt);
                
                totalRefund = totalRefund.add(itemService.getItemPrice(o.getItem().getId()).multiply(new BigDecimal(o.getQuantity())));
            }
            
            return "Return Success!\nReturn id : " + returnPending.getId() + "\nTotal refund : $" + totalRefund;
        }
        return "Wrong Token";
    }
    
    @RequestMapping(value = "/returns/{retId}", method = RequestMethod.GET)
    public String getReturnsById(@PathVariable Long retId) {
        Return ret = returnService.getReturnById(retId);
        if (ret == null) {
            return "Return id not found";
        }
        
        String itemDt = "";
        BigDecimal totalRefund = BigDecimal.ZERO;
        for (ReturnDt rDt : ret.getReturnDt()) {
            itemDt += "\n\nSKU : " + rDt.getItem().getSku();
            itemDt += "\nQC checked : " + rDt.getQcStatus();
            itemDt += "\nQty returned : " + rDt.getQuantity();
            itemDt += "\nPrice : $" + rDt.getItem().getPrice();
            
            if (rDt.getQcStatus().equalsIgnoreCase(QC_STATUS_ACCEPTED)) {
                totalRefund = totalRefund.add(new BigDecimal(rDt.getQuantity()).multiply(rDt.getItem().getPrice()));
            }
        }
        return "Return id : " + retId + "\nStatus : " + ret.getStatus() + itemDt 
                + "\n\nRefund amount : $" + totalRefund;
    }
    
    @RequestMapping(value = "/returns/{retId}/items/{itemId}/qc/{status}", method = RequestMethod.PUT)
    public String getReturnsById(@PathVariable("retId") Long retId, @PathVariable("itemId") Long itemId, 
            @PathVariable("status") String status) {
        ReturnDt rDt = returnDtService.findReturnDtByReturnIdAndItemId(retId, itemId);
        if (rDt == null) {
            return "Return id and/or item id not found";
        } else {
            rDt.setQcStatus(status);
            returnDtService.addOrSaveReturnDt(rDt);
            return "Update QC status completed";
        }
    }
}
