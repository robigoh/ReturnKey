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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Object> importReturns() {
        Map<String, Object> map = new HashMap<String, Object>();
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
        return generateResponse("Import Successful", HttpStatus.OK, null);
    }
    
    @RequestMapping(value = "/pending/returns", method = RequestMethod.POST)
    public ResponseEntity<Object> pendingReturns(@RequestBody Order order) {
        Return ret;
        if (!orderService.findByOrderIdAndEmail(order).isEmpty()) {
            ret = new Return();
            UUID uuid = UUID.randomUUID();
            String uuidAsString = uuid.toString();
            ret.setStatus(STATUS_WAITING);
            ret.setToken(uuidAsString);
            ret.setOrderId(order.getOrderId());
            String token = returnService.addReturn(ret);
            
            return generateResponse("Your token is : " + token, HttpStatus.OK, ret);
        }
        return generateResponse("Invalid order id/email address", HttpStatus.NOT_FOUND, null);
    }
    
    @RequestMapping(value = "/returns", method = RequestMethod.POST)
    public ResponseEntity<Object> returns(@RequestBody Return ret) {
        Return returnPending = returnService.findPendingReturn(ret);
        boolean found;
        boolean enough;
        boolean isReturned;
        boolean wrongQty;
        List<Order> orderList = new ArrayList<>();
        
        if (returnPending != null) {
            if (returnPending.getStatus().equalsIgnoreCase(STATUS_COMPLETE)) {
                return generateResponse(returnPending.getToken() + " status has been complete", HttpStatus.BAD_REQUEST, null);
            }
            if (ret.getReturnDt().isEmpty()) {
                return generateResponse("No item to return", HttpStatus.BAD_REQUEST, null);
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
                        if (rDt.getQuantity() <= o.getQuantity()) {
                            o.setQuantity(rDt.getQuantity());
                            orderList.add(o);
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
                    return generateResponse("Item id " + rDt.getItem().getId() + " not found in order id " + returnPending.getOrderId(), HttpStatus.BAD_REQUEST, null);
                } 
                if (!enough) {
                    return generateResponse("Item id " + rDt.getItem().getId() + " not enough to do return", HttpStatus.BAD_REQUEST, null);
                }
                if (isReturned) {
                    return generateResponse("Item id " + rDt.getItem().getId() + " has been returned before", HttpStatus.BAD_REQUEST, orders);
                }
                if (wrongQty) {
                    return generateResponse("Item id " + rDt.getItem().getId() + " should return quantity in positive value", HttpStatus.BAD_REQUEST, null);
                }
            }
            returnPending.setStatus(STATUS_COMPLETE);
            returnService.updateReturn(returnPending);
            ReturnDt retDt;
            BigDecimal totalRefund = BigDecimal.ZERO;
            List<ReturnDt> retDtList = new ArrayList<>();
            
            for (Order o : orderList) {
                o.setReturned(true);
                orderService.updateReturn(o);
                
                retDt = new ReturnDt();
                retDt.setReturns(returnPending);
                retDt.setItem(o.getItem());
                retDt.setQuantity(o.getQuantity());
                retDt.setQcStatus(QC_STATUS_ACCEPTED); // Set default qc status to Accepted
                returnDtService.addOrSaveReturnDt(retDt);
                retDtList.add(retDt);
                
                totalRefund = totalRefund.add(itemService.getItemPrice(o.getItem().getId()).multiply(new BigDecimal(o.getQuantity())));
            }
            returnPending.setReturnDt(retDtList);
            return generateResponse("Return Success! Total refund : $" + totalRefund, HttpStatus.OK, returnPending);
        }
        return generateResponse("Wrong Token", HttpStatus.BAD_REQUEST, null);
    }
    
    @RequestMapping(value = "/returns/{retId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getReturnsById(@PathVariable Long retId) {
        Return ret = returnService.getReturnById(retId);
        if (ret == null) {
            return generateResponse("Return id not found", HttpStatus.BAD_REQUEST, null);
        }
        
        String itemDt = "";
        BigDecimal totalRefund = BigDecimal.ZERO;
        for (ReturnDt rDt : ret.getReturnDt()) {
            if (rDt.getQcStatus().equalsIgnoreCase(QC_STATUS_ACCEPTED)) {
                totalRefund = totalRefund.add(new BigDecimal(rDt.getQuantity()).multiply(rDt.getItem().getPrice()));
            }
        }
        return generateResponse("Return id : " + retId + ", Status : " + ret.getStatus() 
                + ", Refund amount : $" + totalRefund, HttpStatus.OK, ret);
    }
    
    @RequestMapping(value = "/returns/{retId}/items/{itemId}/qc/{status}", method = RequestMethod.PUT)
    public ResponseEntity<Object> getReturnsById(@PathVariable("retId") Long retId, @PathVariable("itemId") Long itemId, 
            @PathVariable("status") String status) {
        ReturnDt rDt = returnDtService.findReturnDtByReturnIdAndItemId(retId, itemId);
        if (rDt == null) {
            return generateResponse("Return id and/or item id not found", HttpStatus.BAD_REQUEST, null);
        } else {
            rDt.setQcStatus(status);
            returnDtService.addOrSaveReturnDt(rDt);
            return generateResponse("Update QC status completed", HttpStatus.OK, rDt);
        }
    }
    
    public ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message", message);
        map.put("status", status.value());
        map.put("data", responseObj);

        return new ResponseEntity<Object>(map,status);
    }
}
