/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tintin.service;

import com.tintin.model.VipmfLatLng;

/**
 *
 * @author hsiehkaiyang
 */
public interface VipmfLatLngService {
    
    void save(VipmfLatLng vipmf_latlng);

    void update(VipmfLatLng vipmf_latlng);

    void delete(VipmfLatLng vipmf_latlng);

    VipmfLatLng findByVipcode(String vip_code);
  
    void insertBatch(VipmfLatLng[] vipmf_latlng);
}
