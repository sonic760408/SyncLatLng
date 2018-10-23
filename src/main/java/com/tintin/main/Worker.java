/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tintin.main;

import com.tintin.model.Vipmf;
import com.tintin.model.VipmfLatLng;
import com.tintin.service.VipmfLatLngService;
import com.tintin.service.VipmfService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;

/**
 *
 * @author hsiehkaiyang
 */
public class Worker implements Runnable, DisposableBean {

    private static final Logger log = LogManager.getLogger(Worker.class);
    private static final int TIMEOUT = 5000;

    private VipmfService vipmfService;
    private VipmfLatLngService vipmfLatLngService;

    private final String TEMP_CSV_FILE = "temp.csv";

    private final String LOGIN_URL = "https://www.tgos.tw/TGOS/UserControl/Utility/USM/Account/GEN_USM_Account.ashx";
    private final String LOGOUT_URL = "https://www.tgos.tw/TGOS/Web/TGOS_Home.aspx?EXECUTE_TYPE=LOGOUT";

    private final String UPLOAD_URL = "https://www.tgos.tw/TGOS/Web/Address/TGOS_AddressBatchQuery.aspx";

    private final String UPLOAD_QUERY_URL = "https://www.tgos.tw/TGOS/Generic/Project/GEN_PROJ_BATHQUERY.ashx";

    private final String ROOT_URL = "https://www.tgos.tw/TGOS/Web/TGOS_Home.aspx";
    private final String LOGIN_ENCRYPT_ID = "3E2A3074E9AA6F5EF98BE0A9FF"
            + "83A26FD7D6191CB6E0B370ABDFC13779D3D2F57A55E30F3892A711EEF5C4A650D2617F";
    private final String LOGIN_ENCRYPT_PWD = "43B5CE9704F5FBF857BAA1EC2734989956E94ED8BA9E9546";

    private final String API_KEY = "cGEErDNy5yNTw4lb4fzGu/McHxMCS"
            + "5Ecw4JFpLqabVHJTHtEjXAyKRxOn+37+tPZmQl5EOzQ5vd7S8I"
            + "P8Wm/emt1TA72qPL18heie4BOWq2yoGjm/W2V9em4NDhrzyfmr"
            + "bJjgIUxxef7oRSt2s3zssZMqK6zyT/eLT+xAXdIRsno5nkwLmm"
            + "dO/XGA5GfzQFNmu81b2OwClI+xm+3ImaygqPdacB+Onrb/SFp5"
            + "J8P0IHEE7mMGBTzmPWs0mrFzL8DCIyB2HYxmlQFUkeXewMSVMB"
            + "es4AnCd50YEl5TUTwjWp4omVJpaes5ClACCClxLKKUPR/9NjnB"
            + "3kcID7hFLqTqskeiYXMOiyw";

    private final String COORDINATOR_EPSG_WGS84 = "EPSG:4326";
    private final String COORDINATOR_EPSG_TWD97TM119 = "EPSG:3825";
    private final String COORDINATOR_EPSG_TWD97TM121 = "EPSG:3826";

    private final String CODE_TYPE = "BIG5";
    private final String TEMPFILE = "temp.csv";
    private final Double NO_COORDINATE = 0.0;

    private List<Cookie> cookies;

    private String tag = "";
    private String import_webpath = "";

    private ClassPathXmlApplicationContext context;

    public Worker(String[] args) {
        // store parameter for later user
        if (args != null) {
            tag = args[0];
            import_webpath = args[1];
        }
    }

    @Override
    public void run() {

        context = new ClassPathXmlApplicationContext("spring/config/BeanLocations.xml");
        try {
            context.registerShutdownHook();
        } catch (BeansException e) {
            log.error(e.getLocalizedMessage());
            return;
        }
        if (tag.equals("-import")) {
            downloadFromUrl(import_webpath);
            start_import();
        } else {
            start_sync();
        }
    }

    @Override
    public void destroy() throws Exception {

    }

    private void start_sync() {
        //setup xml

        vipmfService = (VipmfService) context.getBean("vipmfService");
        if (vipmfService != null) {

            writeTempCsv();
            cookies = getCookie();
            if (cookies != null) {
                doLogin(cookies);
                uploadCsv(cookies);
                doLogout();
            }

        } else {
            log.error("vipmfService IS NULL");
        }

    }

    private void writeTempCsv() {
        if (vipmfService != null) {
            List<Vipmf> vipmfs = vipmfService.findAll();
            if(vipmfs != null){
                if(vipmfs.isEmpty() == false){
                    doWriteCSV(vipmfs);
                }else
                {
                    log.info("NO DATA TO UPDATE ");
                }
            }else
            {
                log.error("ERROR ENCOUNTER");
            }
        }
    }

    private void uploadCsv(List<Cookie> cookies) {

        int i = 0;
        int statuscode;
        String cookie_req = "_ga=; G_ENABLED_IDPS=;  _gid=; ApplySID=; WmsID=; ApplyMap=; _gat=;";

        //log.info("UPLOAD CSV xxx");
        for (Cookie item : cookies) {
            //log.info("COOKIE["+i+"]: "+item.getName()+"="+item.getValue());
            cookie_req = cookie_req + item.getName() + "=" + item.getValue();
            i++;

            if (i < cookies.size()) {
                cookie_req = cookie_req + ";";
            }
        }

        final String URLENCODED = "?method=ExecuteBathQuery";

        CloseableHttpClient http;
        HttpPost post = new HttpPost(UPLOAD_QUERY_URL + URLENCODED);

        //設定boundary為Unique ID(timestamp)
        String boundary = "---------------------------" + System.currentTimeMillis();

        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT); //5sec
        http = new DefaultHttpClient(httpParams);

        //log.info("COOKIE: "+cookie_req);
        CloseableHttpResponse response = null;

        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        log.info("Current relative path is: " + s);

        File file = new File(TEMP_CSV_FILE);

        if (file.exists()) {
            if (file.isFile()) {
                log.debug(" FIND FILE: " + TEMP_CSV_FILE);
            } else {
                log.debug(" WRONG FILE  " + TEMP_CSV_FILE);
            }
        } else {
            log.error(" NO FIND FILE ON " + currentRelativePath + TEMP_CSV_FILE);
            return;
        }

        InputStream inputStream;

        try {
            inputStream = new FileInputStream(file);

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
            return;
        }

        log.info("FILENAME: " + file.getAbsolutePath());
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        //FileBody filebody = new FileBody(file, ContentType.create("text/csv"));
        //FileBody filebody = new FileBody(new InputStreamBody(inputStream), ContentType.create("text/csv"));
        try {
            builder.addBinaryBody("ADDR_BOX_CONTENT_FILE", inputStream, ContentType.create("text/csv"), file.getName());
            //builder.addPart("ADDR_BOX_CONTENT_FILE", filebody);
            builder.addPart("ADDR_BOX_CRS", new StringBody(COORDINATOR_EPSG_WGS84));
            builder.addPart("ADDR_BOX_APIKEY", new StringBody(API_KEY));
            builder.addPart("refFile", new StringBody("my.csv"));
            builder.addPart("ADDR_BOX_ISONLY_FULL_MATCH", new StringBody("0"));
            builder.addPart("ADDR_BOX_ODD_EVEN_TYPE", new StringBody("0"));
            builder.addPart("ADDR_BOX_ODD_OEVEN", new StringBody("0"));
            builder.addPart("ADDR_BOX_CanIgnoreVillage", new StringBody("on"));
            builder.addPart("ADDR_BOX_CanIgnoreNeighborhood", new StringBody("on"));
            builder.addPart("ADDR_BOX_ReturnMaxCount", new StringBody("1"));
            builder.setBoundary(boundary);  //使用boundary來確認資料有上傳成功
            builder.setCharset(Charset.forName(CODE_TYPE));

            post.setEntity(builder.build());
            post.addHeader("Referer", UPLOAD_URL);
            post.addHeader("Cookie", cookie_req);
            post.addHeader("Content-Type", "multipart/form-data; boundary=" + boundary);   //使用boundary來確認資料有上傳成功

            long length = post.getEntity().getContentLength();
            if (length != -1) {
                post.addHeader("Content-Length", String.valueOf(length));
            }

            //Header[] headers = post.getAllHeaders();
            /*
            log.info("HEADER: ");
            for (Header header : headers) {
                log.info(header.getName() + ": " + header.getValue());
            }
             */
            //HttpEntity entity = post.getEntity();
            //String responseString = EntityUtils.toString(entity, CODE_TYPE);
            //log.info("xxx ENTITY: " + responseString);
            response = http.execute(post);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
            return;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getLocalizedMessage());
                }
            }
        }

        statuscode = response.getStatusLine().getStatusCode();
        JSONObject json;

        if (statuscode == HttpStatus.SC_OK) {
            try {

                String str = EntityUtils.toString(response.getEntity());
                EntityUtils.consume(response.getEntity());

                log.info(" ooo: " + str);
                //String str = EntityUtils.toString(response.getEntity());
                if (Util.isJson(str)) {
                    json = new JSONObject(str);
                    if (json.has("Code")) {
                        if (json.getInt("Code") > 0) {
                            log.info("上傳成功");
                        } else {
                            log.info("上傳失敗");
                        }
                    }
                }
            } catch (IOException e) {
                //e.printStackTrace();
                log.error(e.getLocalizedMessage());
            } catch (ParseException e) {
                log.error(e.getLocalizedMessage());
            }
        } else {
            log.error("CODE: " + statuscode + ", CAUSED: " + response.getStatusLine().getReasonPhrase());
        }

        try {
            http.close();
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
        try {
            response.close();
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }

    }

    private List<Cookie> getCookie() {
        /* init client */
        CloseableHttpClient http = null;
        CookieStore httpCookieStore = new BasicCookieStore();
        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore);
        http = builder.build();
        int statuscode;
        /* do stuff */
        HttpPost post = new HttpPost(ROOT_URL);
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = http.execute(post);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return null;
        }

        /* check cookies */
        statuscode = httpResponse.getStatusLine().getStatusCode();
        //log.info("RESPONSE: "+httpResponse.getStatusLine().getStatusCode());
        //log.info("cookie: "+httpCookieStore.getCookies().toString());

        try {
            http.close();
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
        try {
            httpResponse.close();
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }

        //if response is not 200, return null
        if (statuscode != HttpStatus.SC_OK) {
            return null;
        } else {
            return httpCookieStore.getCookies();
        }

    }

    private void doLogout() {
        boolean isSuccess = false;
        int i = 0;
        int statuscode;
        String cookie_req = "_ga=; G_ENABLED_IDPS=;  _gid=; ApplySID=; WmsID=; ApplyMap=; _gat=1;";
        for (Cookie item : cookies) {
            //log.info("COOKIE["+i+"]: "+item.getName()+"="+item.getValue());
            cookie_req = cookie_req + item.getName() + "=" + item.getValue();
            i++;

            if (i < cookies.size()) {
                cookie_req = cookie_req + ";";
            }
        }

        CloseableHttpClient http = HttpClients.createDefault();
        HttpPost post = new HttpPost(LOGOUT_URL);
        CloseableHttpResponse response = null;

        //log.info("cookie_req: "+cookie_req);
        try {
            response = http.execute(post);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }

        statuscode = response.getStatusLine().getStatusCode();

        if (statuscode == HttpStatus.SC_OK) {
            log.info("LOGOUT");
        } else {
            log.error("CODE: " + statuscode + ", CAUSED: " + response.getStatusLine().getReasonPhrase());
        }

        try {
            http.close();
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
        try {
            response.close();
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }

    }

    private boolean doLogin(List<Cookie> cookies) {

        boolean isSuccess = false;
        int i = 0;
        int statuscode;
        String cookie_req = "_ga=; G_ENABLED_IDPS=;  _gid=; ApplySID=; WmsID=; ApplyMap=; _gat=1;";
        for (Cookie item : cookies) {
            //log.info("COOKIE["+i+"]: "+item.getName()+"="+item.getValue());
            cookie_req = cookie_req + item.getName() + "=" + item.getValue();
            i++;

            if (i < cookies.size()) {
                cookie_req = cookie_req + ";";
            }
        }

        CloseableHttpClient http = HttpClients.createDefault();
        HttpPost post = new HttpPost(LOGIN_URL);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + CODE_TYPE);
        post.setHeader("Referer", LOGOUT_URL);
        post.setHeader("Cookie", cookie_req);
        CloseableHttpResponse response = null;

        //log.info("cookie_req: "+cookie_req);
        List<NameValuePair> arguments = new ArrayList<>();
        arguments.add(new BasicNameValuePair("method", "logon"));
        arguments.add(new BasicNameValuePair("id", LOGIN_ENCRYPT_ID));
        arguments.add(new BasicNameValuePair("password", LOGIN_ENCRYPT_PWD));
        arguments.add(new BasicNameValuePair("appid", "1"));
        arguments.add(new BasicNameValuePair("captcha", "undefined"));

        try {
            post.setEntity(new UrlEncodedFormEntity(arguments));
            response = http.execute(post);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return isSuccess;
        }

        statuscode = response.getStatusLine().getStatusCode();

        JSONObject json;

        if (statuscode == HttpStatus.SC_OK) {
            try {
                String str = EntityUtils.toString(response.getEntity());
                EntityUtils.consume(response.getEntity());
                log.info(" LOGIN: " + str);
                //String str = EntityUtils.toString(response.getEntity());
                if (Util.isJson(str)) {
                    json = new JSONObject(str);
                    if (json.has("Code")) {
                        if (json.getInt("Code") > 0) {
                            isSuccess = true;
                        } else {

                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.error(e.getLocalizedMessage());
            } catch (ParseException e) {
                log.error(e.getLocalizedMessage());
            }
        } else {
            log.error("CODE: " + statuscode + ", CAUSED: " + response.getStatusLine().getReasonPhrase());
        }

        try {
            http.close();
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
        try {
            response.close();
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }

        return isSuccess;
    }

    private void doWriteCSV(List<Vipmf> vipmfs) {
        CSVFormat format = CSVFormat.DEFAULT
                .withHeader("id", "Address", "Response_Address", "Response_X", "Response_Y");

        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter(new FileOutputStream(Paths.get(TEMP_CSV_FILE).toString()), CODE_TYPE);
            //writer = Files.newBufferedWriter(Paths.get(TEMP_CSV_FILE));

            //CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
            //        .withHeader("id", "Address", "Response_Address", "Response_X", "Response_Y"));
            CSVPrinter csvPrinter = new CSVPrinter(osw, format);
            int i = 0;
            for (Vipmf item : vipmfs) {
                csvPrinter.printRecord(i + "_" + item.getVip_code().trim(), item.getAddress().trim(), "", "", "");
                i++;
            }

            csvPrinter.flush();

        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        } finally {
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    log.error(e.getLocalizedMessage());
                }
            }
        }
    }

    private void start_import() {

        File file = new File(TEMPFILE);
        if (file.exists() == false) {
            String err = "找不到檔案: " + TEMPFILE;
            //System.out.println(err);
            log.error(err);
        } else if ("csv".equals(Util.getFileExtension(file))) {

            vipmfLatLngService = (VipmfLatLngService) context.getBean("vipmfLatLngService");
            if (vipmfLatLngService != null) {
                log.info("vipmfLatLngService IS NOT NULL");
            } else {
                log.error("vipmfLatLngService IS NULL");
            }

            //VipmfLatLng vipmfLatLng = vipmfLatLngService.findByVipcode("0000001");
            //log.info(vipmfLatLng.toString());
            CSVFormat format = CSVFormat.DEFAULT
                    //.withHeader(" ﻿id", "Address", "Response_Address", "Response_X", "Response_Y")
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim();
            InputStreamReader isr = null;
            try {
                isr = new InputStreamReader(new FileInputStream(Paths.get(TEMPFILE).toString()), "UTF-8");
                String str = null;
                int i = 0;
                
                /*
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(Paths.get(import_filepath).toString()), "UTF-8"));

                while (true) {
                    
                    if (i > 2) {
                        i = 0;
                        break;
                    }
                    
                    str = reader.readLine();

                    log.info(String.format("%040x", new BigInteger(1, str.getBytes(StandardCharsets.UTF_8))));
                    if (str != null) {
                        log.info("REC["+i+"]: "+(str));
                    } else {
                        break;
                    }
                    i++;
                }
                */

                List<VipmfLatLng> vipmfLatLngs = new ArrayList<>();
                VipmfLatLng item;
                //read csv
                
                CSVParser csvParser = new CSVParser(isr, format);
                for (CSVRecord csvRecord : csvParser) {

                    // Accessing values by Header names
                    String id = (csvRecord.get("﻿id"));
                    String addr = (csvRecord.get("Address"));
                    String response_addr = (csvRecord.get("Response_Address"));
                    String response_X = (csvRecord.get("Response_X"));
                    String response_Y = (csvRecord.get("Response_Y"));
                    
                    if(id.contains("_"))
                    {
                        id = id.substring(id.indexOf("_")+1, id.length());
                    }

                    if(response_X.isEmpty() == false && 
                            response_Y.isEmpty() == false){
                        
                        if(response_X.contains(";"))
                        {
                            response_X = response_X.substring(0, response_X.indexOf(";"));
                        }
                        
                        if(response_Y.contains(";"))
                        {
                            response_Y = response_Y.substring(0, response_Y.indexOf(";"));
                        }
                        
                        str = "REC[" + csvRecord.getRecordNumber() + "]: "
                                + "ID: " + id
                                + ", ADDR: " + addr
                                + ", RES_ADDR: " + response_addr
                                + ", RES_X: " + response_X
                                + ", RES_Y: " + response_Y;
                        
                        
                        item = new VipmfLatLng();
                        item.setVip_code(id);
                        item.setLat(Double.parseDouble(response_Y));
                        item.setLng(Double.parseDouble(response_X));
                        vipmfLatLngs.add(item);
                        //log.info(str);
                    }else
                    {
                        log.info("REC[" + csvRecord.getRecordNumber() + "]: 找不到座標");
                        
                        //add empty lat, lng
                        item = new VipmfLatLng();
                        item.setVip_code(id);
                        item.setLat(NO_COORDINATE);
                        item.setLng(NO_COORDINATE);
                        vipmfLatLngs.add(item);
                        //log.info(str);
                    }

                    //write to table
                    i++;
                }
                vipmfLatLngService.insertBatch(vipmfLatLngs.toArray(new VipmfLatLng[vipmfLatLngs.size()]));
                //vipmfLatLngService.updateBatch(vipmfLatLngs.toArray(new VipmfLatLng[vipmfLatLngs.size()]));
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getLocalizedMessage());
            } finally {
                if (isr != null) {
                    try {
                        isr.close();
                    } catch (IOException e) {
                        log.error(e.getLocalizedMessage());
                    }
                }
            }

        } else {
            String err = "檔案格式不正確, 非csv檔: " + TEMPFILE;
            //System.out.println(err);
            log.error(err);
        }
    }
    
    private void downloadFromUrl(String url){
        CloseableHttpClient client = HttpClients.createDefault();
        
        FileOutputStream outstream = null;
        File myFile = new File(TEMPFILE);
        
        try (CloseableHttpResponse response = client.execute(new HttpGet(url))) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                outstream = new FileOutputStream(myFile);
                entity.writeTo(outstream);
                
            }
        }catch(Exception e)
        {
            log.error(e.getLocalizedMessage());
        }finally{
            if(outstream != null)
            {
                try {
                    outstream.close();
                } catch (IOException e) {
                    log.error(e.getLocalizedMessage());
                }
            }
        }
    }
}
