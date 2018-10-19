/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tintin.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.CoordinateTransformFactory;
import org.osgeo.proj4j.ProjCoordinate;

/**
 *
 * @author hsiehkaiyang
 */

public class Proj4j {
    
    private static final Logger log = LogManager.getLogger(Proj4j.class);
    
    public void genLatLng()
    {
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();

        CRSFactory csFactory = new CRSFactory();
        CoordinateReferenceSystem crsTaiwan = csFactory.createFromParameters("EPSG:3826", "+title=TWD97 +proj=tmerc +lat_0=0 +lon_0=121 +k=0.9999 +x_0=250000 +y_0=0 +ellps=GRS80 +units=meters +no_defs");
        CoordinateReferenceSystem crsPenghu = csFactory.createFromParameters("EPSG:3825", "+title=TWD97 +proj=tmerc +lat_0=0 +lon_0=119 +k=0.9999 +x_0=250000 +y_0=0 +ellps=GRS80 +units=公尺 +no_defs");
        
        CoordinateReferenceSystem WGS84 = csFactory.createFromParameters("WGS84", "+proj=longlat +datum=WGS84 +no_defs");

        CoordinateTransform trans = ctFactory.createTransform(crsTaiwan, WGS84);
        ProjCoordinate p = new ProjCoordinate(177747.931, 2497227.74);//我家的TWD97
        ProjCoordinate p2 = new ProjCoordinate();
        ProjCoordinate p3 = trans.transform(p, p2);//120.2973867550582,22.57350797845567
        log.info("我家：{},{}", p2.x, p2.y);
        log.info("我家：{},{}", p3.x, p3.y);
        
        p = new ProjCoordinate(184937.84,2508008.9);//公司的TWD97
        trans.transform(p, p2);//120.36685572897062,22.67115888975168
        log.info("公司：{},{}", p2.x, p2.y);
        
        trans = ctFactory.createTransform(crsPenghu, WGS84);
        p = new ProjCoordinate(308217.440, 2607766.233);//澎湖縣馬公市文光路48巷4號
        p2 = new ProjCoordinate();
        trans.transform(p, p2);//119.57033299885777,23.572232003133845
        log.info("馬公市：{},{}", p2.x, p2.y);
    }
}
