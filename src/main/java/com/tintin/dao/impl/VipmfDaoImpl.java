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
@Transactional
public class VipmfDaoImpl implements VipmfDao {

    private static final Logger log = LogManager.getLogger(VipmfDaoImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(Vipmf vipmf) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            sessionFactory.getCurrentSession().save(vipmf);
            session.getTransaction().commit();

        } catch (HibernateException e) {
            log.error(e.getLocalizedMessage());
            session.getTransaction().rollback();
        }
    }

    @Override
    public void update(Vipmf vipmf) {

        Session session = sessionFactory.getCurrentSession();

        try {
            session.beginTransaction();
            sessionFactory.getCurrentSession().update(vipmf);
            session.getTransaction().commit();

        } catch (HibernateException e) {
            log.error(e.getLocalizedMessage());
            session.getTransaction().rollback();
        }
    }

    @Override
    public void delete(Vipmf vipmf) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            sessionFactory.getCurrentSession().delete(vipmf);
            session.getTransaction().commit();

        } catch (HibernateException e) {
            log.error(e.getLocalizedMessage());
            session.getTransaction().rollback();
        }
    }

    @Override
    public Vipmf findByVipcode(String vip_code) {
        //List list = getHibernateTemplate().find(" FROM Vipmf WHERE vip_code = ?", stockCode);
        //return (Vipmf) list.get(0);
        String sql_query = "FROM Vipmf v WHERE v.vip_code = :vip_code";
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(sql_query);
        query.setParameter("vip_code", vip_code);

        List<Vipmf> lists = query.list();
        return (lists.isEmpty()? null : lists.get(0));

    }

    @Override
    public List<Vipmf> findAll() {
        int maxRows = 10000, start = 0;

        String sql_query = "FROM Vipmf "
                + "WHERE TRIM(address) != '' "
                + "ORDER BY vip_code ASC ";

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(sql_query);
        query.setFirstResult(start);
        query.setMaxResults(maxRows);

        List<Vipmf> list = query.list();
        return list;
    }

}
