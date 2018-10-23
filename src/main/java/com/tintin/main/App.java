/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tintin.main;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author hsiehkaiyang
 */
class Option {

    String flag, opt;

    public Option(String flag, String opt) {
        this.flag = flag;
        this.opt = opt;
    }
}

public class App {

    private static final Logger log = LogManager.getLogger(App.class);

    public static void main(String[] args) {

        //fix java.io.CharConversionException db2
        System.setProperty("db2.jcc.charsetDecoderEncoder", "3");

        if (args.length > 0) {
            String cmd = args[0];
            if (null != cmd)
            switch (cmd) {
                case "-help":
                    if (args.length != 1) {
                        printUsage();
                        throw new IllegalArgumentException("-help後面不用加入參數!!");
                    }
                    printUsage();
                    return;
                case "-import":
                    if (args.length != 2) {
                        printUsage();
                        throw new IllegalArgumentException("-import <URL>, 必須要加入有效的URL網址");
                    }else
                    {
                        log.info("執行會員地址座標產生的csv結果匯入到資料庫內");
                        new Worker(new String[]{args[0], args[1]}).run();
                    }
                    break;
                default:{
                    printUsage();
                    throw new IllegalArgumentException("未知指令參數: " + cmd);
                }
            }
        } else {
            log.info("執行會員地址座標擷取");
            new Worker(null).run();
        }

    }

    private static void printUsage() {
        log.warn("參數說明:\n"
                + "\n"
                + "SyncLatLng -help\n"
                + "    顯示使用說明\n"
                + "\n"
                + "SyncLatLng \n "
                + "    執行會員地址座標擷取"
                + "SyncLatLng -import <URL>\n"
                + "    下載會員地址座標產生結果的csv檔案並匯入到資料庫內\n"
        );

    }
}
