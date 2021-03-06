/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tintin.dao.impl;

import com.tintin.dao.VipmfDao;
import com.tintin.main.HibernateUtil;
import com.tintin.model.Vipmf;
import java.util.List;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

/**
 *
 * @author hsiehkaiyang
 */
@Repository
public class VipmfDaoImpl implements VipmfDao {

    private static final Logger log = LogManager.getLogger(VipmfDaoImpl.class);
    private static final int MAXROW = 10000;

    //@Autowired
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public void save(Vipmf vipmf) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(vipmf);
            session.getTransaction().commit();

        } catch (HibernateException e) {
            log.error(e.getLocalizedMessage());
            session.getTransaction().rollback();
        }finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    @Transactional
    public void update(Vipmf vipmf) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(vipmf);
            session.getTransaction().commit();

        } catch (HibernateException e) {
            log.error(e.getLocalizedMessage());
            session.getTransaction().rollback();
        }finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    @Transactional
    public void delete(Vipmf vipmf) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.delete(vipmf);
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
    public Vipmf findByVipcode(String vip_code) {
        //List list = getHibernateTemplate().find(" FROM Vipmf WHERE vip_code = ?", stockCode);
        //return (Vipmf) list.get(0);
        String sql_query = "FROM Vipmf v WHERE v.vip_code = :vip_code";
        Session session = sessionFactory.openSession();
        try {
            Query query = session.createQuery(sql_query);
            query.setParameter("vip_code", vip_code);
            List<Vipmf> lists = query.list();
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
    public List<Vipmf> findAll() {
        int maxRows = MAXROW, start = 0;

        String sql_query = "FROM Vipmf "
                + "WHERE TRIM(address) != '' "
                + "and vip_code not in (SELECT vip_code FROM VipmfLatLng) "
                + "ORDER BY vip_code ASC ";

        Session session = sessionFactory.openSession();
        try {
            Query query = session.createQuery(sql_query);
            query.setFirstResult(start);
            query.setMaxResults(maxRows);

            List<Vipmf> list = query.list();
            return list;
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
}
