/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tintin.dao.impl;

import com.tintin.dao.VipmfLatLngDao;
import com.tintin.model.VipmfLatLng;
import java.sql.BatchUpdateException;
import java.util.List;
import javax.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author hsiehkaiyang
 */
@Repository
public class VipmfLatLngDaoImpl implements VipmfLatLngDao {

    private static final Logger log = LogManager.getLogger(VipmfLatLngDaoImpl.class);
    private static final int BATCH_SIZE = 100;
    
    @Autowired
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public void save(VipmfLatLng vipmf_latlng) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(vipmf_latlng);
            session.getTransaction().commit();

        } catch (HibernateException e) {
            log.error(e.getLocalizedMessage());
            session.getTransaction().rollback();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    @Transactional
    public void update(VipmfLatLng vipmf_latlng) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(vipmf_latlng);
            session.getTransaction().commit();

        } catch (HibernateException e) {
            log.error(e.getLocalizedMessage());
            session.getTransaction().rollback();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    @Transactional
    public void delete(VipmfLatLng vipmf_latlng) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.delete(vipmf_latlng);
            session.getTransaction().commit();

        } catch (HibernateException e) {
            log.error(e.getLocalizedMessage());
            session.getTransaction().rollback();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    @Transactional
    public VipmfLatLng findByVipcode(String vip_code) {
        String sql_query = "FROM VipmfLatLng v WHERE v.vip_code = :vip_code";
        Session session = sessionFactory.openSession();
        try {
            Query query = session.createQuery(sql_query);
            query.setParameter("vip_code", vip_code);

            List<VipmfLatLng> lists = query.list();
            return (lists.isEmpty() ? null : lists.get(0));
        } catch (HibernateException e) {
            log.error(e.getLocalizedMessage());
            //session.getTransaction().rollback();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    @Transactional
    public void insertBatch(VipmfLatLng[] vipmf_latlng) {
        int batchSize = BATCH_SIZE;
        Session session = sessionFactory.openSession();
        //log.info("BEFORE UPDATE ");
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            for (int i = 0; i < vipmf_latlng.length; i++) {
                session.saveOrUpdate(vipmf_latlng[i]);
                if (i > 0 && i % batchSize == 0) {
                    //log.info("CLEAR");
                    session.flush();
                    session.clear();
                }
            }
            transaction.commit();

            //session.beginTransaction();
            //log.info("vipmf_latlng[0]: "+vipmf_latlng[0].toString());
            //sessionFactory.getCurrentSession().update(vipmf_latlng[0]);
            //session.getTransaction().commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
            session.getTransaction().rollback();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

}
