/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tintin.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author hsiehkaiyang
 */
@Entity
@Table(name = "vipmf")
public class Vipmf implements Serializable{
    
    private static final Logger log = LogManager.getLogger(Vipmf.class);
    
    @Id
    @Column(name = "vip_code")
    private String vip_code;
    
    @Column(name = "vip_name")
    private String vip_name;
    
    @Column(name = "address")
    private String address;

    public Vipmf() {
        
    }
    
    public String getVip_code() {
        return vip_code;
    }

    public void setVip_code(String vip_code) {
        this.vip_code = vip_code;
    }

    public String getVip_name() {
        return vip_name;
    }

    public void setVip_name(String vip_name) {
        this.vip_name = vip_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Vipmf{" 
                + "vip_code=" + vip_code +
                ", vip_name=" + vip_name 
                + ", address=" + address + '}';
    }
    
    
    
    
}
