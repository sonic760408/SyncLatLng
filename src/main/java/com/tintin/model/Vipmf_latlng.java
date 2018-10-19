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
@Table(name = "vipmf_latlng")
public class Vipmf_latlng implements Serializable{
    
    private static final Logger log = LogManager.getLogger(Vipmf_latlng.class);
    
    @Id
    @Column(name = "vip_code")
    private String vip_code;
    
    @Column(name = "lat")
    private double lat;
    
    @Column(name = "lng")
    private double lng;

    public Vipmf_latlng() {
        
    }

    public String getVip_code() {
        return vip_code;
    }

    public void setVip_code(String vip_code) {
        this.vip_code = vip_code;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
    
    
    
}
