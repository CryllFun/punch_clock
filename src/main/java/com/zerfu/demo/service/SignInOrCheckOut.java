package com.zerfu.demo.service;

import com.zerfu.demo.entity.UserInfo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SignInOrCheckOut {
    public static SimpleDateFormat yyyyddmm = new SimpleDateFormat("yyyy-MM-dd");

    private static final String GET_EXECUTION = "http://portal.zts.com.cn/cas/login?service=http%3A%2F%2F10.55.10.13%2FCASLogin";

    private static final String INDEX = "http://10.55.10.13/UserProject.do?&project=itzhgl_sc";

    private static final String baseUrl = "http://10.55.10.13/";

    private static BasicCookieStore cookieStore = new BasicCookieStore();

    private static UserInfo user;

    public  void loginwb() throws Exception {

        Map<String, Object> map = getLoginInfo(GET_EXECUTION);
        String execution = map.get("execution") + "";
        String lt = map.get("lt") + "";
        String TGC = putTGC(user.getAccount(), user.getPassword(), execution, lt);
        if (StringUtils.isNotBlank(TGC)) {
            String base = getTGC(TGC);
        }
    }
    public  Map<String, Object> getLoginInfo(String GET_EXECUTION) {
        Map<String, Object> map = new HashMap<String, Object>();
        String execution = "";
        String lt = "";
        try {
            @SuppressWarnings({ "deprecation", "resource" })
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(GET_EXECUTION);
            HttpResponse response = client.execute(request);
            String strResult = EntityUtils.toString(response.getEntity());
            Page page = new Page();
            page.setRawText(strResult);
            page.setRequest(new Request(GET_EXECUTION));
            execution = page.getHtml().xpath("//input[@name='execution']/@value").get();
            lt = page.getHtml().xpath("//input[@name='lt']/@value").get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("execution", execution);
        map.put("lt", lt);
        return map;
    }
    public  String putTGC(String username, String password, String execution, String lt)
            throws Exception {
        System.out.println("=========================获取TGC结束============================");
        CloseableHttpClient httpClient = null;
        String tgt = "";
        try {
            httpClient = HttpClients.custom()
                    .setDefaultCookieStore(cookieStore).build();
            HttpPost httpPost = new HttpPost(GET_EXECUTION);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("username", username));
            nvps.add(new BasicNameValuePair("password", password));
            nvps.add(new BasicNameValuePair("execution", execution));
            nvps.add(new BasicNameValuePair("_eventId", "submit"));
            nvps.add(new BasicNameValuePair("lt", lt));
            nvps.add(new BasicNameValuePair("uuid", ""));

            HttpEntity reqEntity = new UrlEncodedFormEntity(nvps, "utf-8");
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setEntity(reqEntity);

            CloseableHttpResponse response = httpClient.execute(httpPost);
            String strResult = EntityUtils.toString(response.getEntity());
            System.out.println(strResult);
            Header[] tgtHead = response.getAllHeaders();
            if (tgtHead != null) {
                for (int i = 0; i < tgtHead.length; i++) {
                    System.out.println(tgtHead[i].getName()  + " :"+  tgtHead[i].getValue() );
                    if (StringUtils.equals(tgtHead[i].getName(), "Location")) {
                        tgt = tgtHead[i].getValue();
                    }
                }
            }
        } finally {
            httpClient.close();
        }
        System.out.println("=========================获取TGC结束============================");
        return tgt;
    }
    public  String getTGC(String tgc)
            throws Exception {
        CloseableHttpClient httpClient = null;
        String tgt = "";
        try {
            httpClient =HttpClients.custom()
                    .setDefaultCookieStore(cookieStore).build();
            HttpPost httpPost = new HttpPost(tgc);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            HttpEntity reqEntity = new UrlEncodedFormEntity(nvps, "utf-8");
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setEntity(reqEntity);

            CloseableHttpResponse response = httpClient.execute(httpPost);
            Header[] tgtHead = response.getAllHeaders();
            if (tgtHead != null) {
                for (int i = 0; i < tgtHead.length; i++) {
                    System.out.println(tgtHead[i].getName()  + " :"+  tgtHead[i].getValue() );
                    if (StringUtils.equals(tgtHead[i].getName(), "Location")) {
                        tgt = tgtHead[i].getValue();
                    }
                }
            }
        } finally {
            httpClient.close();
        }
        return tgt;
    }

    /**
     * 登录获取cookie,获取token,签到签出
     * @param type   打卡类型 0:签到,1:签出
     * @throws Exception
     */
    public  String qdqc( int type,UserInfo userInfo) throws Exception {
        user = userInfo;
        loginwb();
        String token = "";
        int statusCode = 0;
        HttpClient client1 = null;
//	        //获取token
        client1 =HttpClients.custom()
                .setDefaultCookieStore(cookieStore).build();

        HttpGet getToken = new HttpGet(baseUrl + "UIProcessor?Table=vem_wb_qdqc");
        getToken.setHeader("Content-type", "application/x-www-form-urlencoded");
        HttpResponse response1 = client1.execute(getToken);
        statusCode = response1.getStatusLine().getStatusCode();
        String records_str = "";
        if (statusCode == 200) {
            org.apache.http.HttpEntity entity = response1.getEntity();
            if (entity != null) {
                String retStr = EntityUtils.toString(entity);
                if (StringUtils.isNotBlank(retStr)) {
                    token = retStr.split("token : ")[1].split(",")[0];
                    token = token.split("'")[1].split("'")[0];
                    records_str = retStr.split("\"records\": ")[1].split("};")[0];
                    System.out.println("token  ：" + token);
                }
            }
        }
        //打卡
        String id = "";
        String today = yyyyddmm.format(new Date());
        JSONArray array = JSONArray.fromObject(records_str);
        for (int i = 0; i < array.size(); i++) {
            JSONObject json = JSONObject.fromObject(array.get(i));
            String kqrq = json.get("kqrq") + "";
            if (today.equals(kqrq)) {
                id = json.get("id") + "";
                break;
            }
        }
        String operate = "vem_wb_qdqc_M1";//签到
        if (type == 1) {
            operate = "vem_wb_qdqc_M2";//签出
        }
        HttpClient qc_client = null;
//	        //获取token
        qc_client =HttpClients.custom()
                .setDefaultCookieStore(cookieStore).build();
        HttpGet qc = new HttpGet(baseUrl + "OperateProcessor?operate=" + operate + "&Table=vem_wb_qdqc&WindowType=1&extWindow=true&PopupWin=true&Token=" + token + "&" + id);
        HttpResponse qc_response = qc_client.execute(qc);
        statusCode = qc_response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            org.apache.http.HttpEntity entity = qc_response.getEntity();
            if (entity != null) {
                String retStr = EntityUtils.toString(entity);//

                if (StringUtils.isNotBlank(retStr)) {
                    String message = retStr.split("\"message\":")[1].split(",")[0];
                    return message;
                }
            }
        }
        return "打卡失败："+statusCode;
    }
}
