/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tintin.dao;

import com.tintin.model.Vipmf;
import java.util.List;

/**
 *
 * @author hsiehkaiyang
 */
public interface VipmfDao {

    void save(Vipmf vipmf);

    void update(Vipmf vipmf);

    void delete(Vipmf vipmf);

    Vipmf findByVipcode(String vip_code);
    
    List<Vipmf> findAll();
}
