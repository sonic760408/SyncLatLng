/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tintin.main;

import com.tintin.service.VipmfService;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.logging.Level;

/**
 *
 * @author hsiehkaiyang
 */
public class App {

    private static final Logger log = LogManager.getLogger(App.class);
    private static final String OAPPID = "gcfIIR/LAX9l2wnt8HTOpOKhLPqHF1hBUja/MKy1zWgZgDwbgQUnow==";
    private static final String OAPIKEY
            = "cGEErDNy5yNTw4lb4fzGu/McHxMCS5Ecw4JFpLqabVHJTHtEjXAyKRxOn+37+tPZmQl"
            + "5EOzQ5vd7S8IP8Wm/emt1TA72qPL18heie4BOWq2yoGjm/W2V9em4NDhrzyfmrbJj"
            + "gIUxxef7oRSt2s3zssZMqK6zyT/eLT+xAXdIRsno5nkwLmmdO/XGA5GfzQFNmu81b"
            + "2OwClI+xm+3ImaygqPdacB+Onrb/SFp5J8P0IHEE7mMGBTzmPWs0mrFzL8DCIyB2H"
            + "YxmlQFUkeXewMSVMBes4AnCd50YEl5TUTwjWp4omVJpaes5ClACCClxLKKUPR/9Nj"
            + "nB3kcID7hFLqTqskeiYXMOiyw";

    private static final String API_URL = "https://addr.tgos.tw/addrws/v30/QueryAddr.asmx/QueryAddr?";
    private static final String TEST_ADDR = "新北市中和區平河里1鄰連城路260號1樓";

    private static final String TEMP_CSV_FILE = "./temp.csv";

    static VipmfService vipmfService;
    //private static SessionFactory sessionFactory;

    public static void main(String[] args) {
        //fix java.io.CharConversionException db2
        System.setProperty("db2.jcc.charsetDecoderEncoder", "3");
        new Worker().run();

    }

    private void doUploadLatLng() {
        final String url = "https://ebpps.taipower.com.tw/EBPPS/action/conLogin.do";
        log.debug("開啟 {}", url);
    }

    private String doGetLatLng(String addr) {
        String errormsg = "";

        try {
            String postdata = "oAPPId=" + OAPPID
                    + "&oAPIKey=" + OAPIKEY
                    + "&oAddress=" + addr
                    + "&oSRS=" + "EPSG:4326"
                    + "&oFuzzyType=" + "0"
                    + "&oResultDataType=" + "JSON"
                    + "&oFuzzyBuffer=" + "0"
                    + "&oIsOnlyFullMatch=" + "false"
                    + "&oIsLockCounty=" + "false"
                    + "&oIsLockTown=" + "false"
                    + "&oIsLockVillage=" + "false"
                    + "&oIsLockRoadSection=" + "false"
                    + "&oIsLockLane=" + "false"
                    + "&oIsLockAlley=" + "false"
                    + "&oIsLockArea=" + "false"
                    + "&oIsSameNumber_SubNumber=" + "false"
                    + "&oCanIgnoreVillage=" + "true"
                    + "&oCanIgnoreNeighborhood=" + "true"
                    + "&oReturnMaxCount=" + "0";

            //postdata = App.toUtf8(postdata);
            postdata = postdata.replaceAll("\\+", "%2B");

            URL connectto = new URL(API_URL);
            HttpsURLConnection conn = (HttpsURLConnection) connectto.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(postdata.length()));
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setInstanceFollowRedirects(false);
            conn.setDoOutput(true);

            log.info("Content-Length: " + Integer.toString(postdata.length()));

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(postdata);
            wr.flush();
            wr.close();

            int responseCode = conn.getResponseCode();

            log.info("\nSending 'POST' request to URL : " + API_URL);
            log.info("Post parameters : " + postdata);
            log.info("Response Code : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }

            errormsg = sb.toString();

            log.info(errormsg);

            br.close();

            //DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            //DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            //Document doc = dBuilder.parse(conn.getInputStream());
            //doc.getDocumentElement().normalize();
            //NodeList nList = doc.getElementsByTagName("Result");
            //nList = doc.getElementsByTagName("SysMsg");
            //errormsg = nList.item(0).getTextContent();
            //logger.info("WEB return value is : " + sb);
            //return errormsg;
        } catch (Exception e) {
            //e.printStackTrace();
            errormsg = e.getLocalizedMessage();
        } finally {

        }

        return errormsg;
    }

    public static String toUtf8(String str) {
        try {
            return new String(str.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
//str.getBytes("UTF-8"); 的意思是以UTF-8的編碼取得位元組
//new String(XXX,"UTF-8"); 的意思是以UTF-8的編碼生成字串
}
