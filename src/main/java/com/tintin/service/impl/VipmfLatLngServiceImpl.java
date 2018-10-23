/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tintin.service.impl;

import com.tintin.dao.VipmfLatLngDao;
import com.tintin.model.VipmfLatLng;
import com.tintin.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author hsiehkaiyang
 */
@Service("vipmfLatLngService")
public class VipmfLatLngServiceImpl implements VipmfLatLngService {

    @Autowired
    @Qualifier("vipmfLatLngDao")
    VipmfLatLngDao vipmfLatLngDao;

    public void setVipmfLatLngDao(VipmfLatLngDao vipmfLatLngDao) {
        this.vipmfLatLngDao = vipmfLatLngDao;
    }
    
    @Override
    public void save(VipmfLatLng vipmf_latlng) {
        vipmfLatLngDao.save(vipmf_latlng);
    }

    @Override
    public void update(VipmfLatLng vipmf_latlng) {
        vipmfLatLngDao.update(vipmf_latlng);
    }

    @Override
    public void delete(VipmfLatLng vipmf_latlng) {
        vipmfLatLngDao.delete(vipmf_latlng);
    }

    @Override
    public VipmfLatLng findByVipcode(String vip_code) {
        return vipmfLatLngDao.findByVipcode(vip_code);
    }

    @Override
    public void insertBatch(VipmfLatLng[] vipmf_latlng) {
        vipmfLatLngDao.insertBatch(vipmf_latlng);
    }
    
}
