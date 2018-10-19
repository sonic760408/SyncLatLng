/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tintin.service.impl;

import com.tintin.dao.VipmfDao;
import com.tintin.model.Vipmf;
import com.tintin.service.VipmfService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author hsiehkaiyang
 */
@Service("vipmfService")
public class VipmfServiceImpl implements VipmfService {

    @Autowired
    @Qualifier("vipmfDao")
    VipmfDao vipmfDao;

    public void setVipmfDao(VipmfDao vipmfDao) {
        this.vipmfDao = vipmfDao;
    }

    @Override
    public void save(Vipmf vipmf) {
        vipmfDao.save(vipmf);
    }

    @Override
    public void update(Vipmf vipmf) {
        vipmfDao.update(vipmf);
    }

    @Override
    public void delete(Vipmf vipmf) {
        vipmfDao.delete(vipmf);
    }

    @Override
    public Vipmf findByVipcode(String vip_code) {
        return vipmfDao.findByVipcode(vip_code);
    }
    
    @Override
    public List<Vipmf> findAll() {
        return vipmfDao.findAll();
    }
}
