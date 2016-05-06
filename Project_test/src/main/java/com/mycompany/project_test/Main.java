package com.mycompany.project_test;

//<editor-fold defaultstate="collapsed" desc="IMPORT">
import com.google.api.client.util.ArrayMap;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//</editor-fold>

/**
 *
 * @author NGUYEN DUC THIEN
 */
public class Main {

    private final DBconnection dbConnection = new DBconnection();

    public static void main(String[] args) throws InterruptedException {
        Main test = new Main();
        List<String> listURL = test.getURL();
        Map<String, List<Integer>> resultMap = test.executeUrl(listURL);
        Map<Integer, Object[]> result = new TreeMap<>();
        result.put(0, new Object[]{"ID", "URL", "URLdecode", "FACEBOOK", "GOOGLE PLUS", "HATENA", "PINTEREST", "LINKEDIN", "BUFFER", "TIME"});
        Set<String> keys = resultMap.keySet();
        int index = 0;
        for (Map.Entry<String, List<Integer>> entry : resultMap.entrySet()) {
            result.put(index + 1, new Object[]{index + 1, entry.getKey(), entry.getKey(), entry.getValue().get(0), entry.getValue().get(1), entry.getValue().get(2), entry.getValue().get(3), entry.getValue().get(4), entry.getValue().get(5), new Date().toString()});
            index++;
        }
        test.writeExcel(result);
    }

    //<editor-fold defaultstate="collapsed" desc="CONNECT DB">
    public List<String> getURL() {
        List<String> listWebsite = new ArrayList<>();
        try (Connection con = DBconnection.getConnection()) {
            String sql = "SELECT URL FROM URL";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listWebsite.add(rs.getString("URL"));
            }
        } catch (Exception e) {

        }
        return listWebsite;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="WRITE EXCEL FILE">
    public void writeExcel(Map<Integer, Object[]> data) {
        //Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("API Test");

        //This data needs to be written (Object[])
        Set<Integer> keyset = data.keySet();
        int rownum = 0;
        for (int key : keyset) {
            Row row = sheet.createRow(rownum++);
            Object[] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if (obj instanceof String) {
                    cell.setCellValue((String) obj);
                } else if (obj instanceof Integer) {
                    cell.setCellValue((Integer) obj);
                }
            }
        }
        try {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File("C:\\vagrant\\Api_result.xlsx"));
            workbook.write(out);
            out.close();
            System.out.println("result.xlsx written successfully on disk.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="EXECUTE URL">
    public Map<String, List<Integer>> executeUrl(List<String> UrlList) throws InterruptedException {
        Map<String, List<Integer>> result = new ArrayMap<>();
        Social social = new Social("Get Social", UrlList);
        Social social1 = new Social("Get Buffer", UrlList);
        Social social2 = new Social("Get Pinterest,Hatena", UrlList);
        Social social3 = new Social("Get Linkedin", UrlList);
        if (!social.t.isAlive() && !social1.t.isAlive() && !social2.t.isAlive() && !social3.t.isAlive()) {
            for (int i = 0; i < UrlList.size(); i++) {
                List<Integer> urlResult = new ArrayList<>();
                urlResult.add(social.Facebook.get(i));
                urlResult.add(social.GooglePlus.get(i));
                urlResult.add(social2.Hatena.get(i));
                urlResult.add(social2.Pinterest.get(i));
                urlResult.add(social3.Linkedin.get(i));
                urlResult.add(social1.Buffer.get(i));
                result.put(UrlList.get(i), urlResult);
            }
        }
        return result;
    }
}
//</editor-fold>

final class Social extends Thread {

    //<editor-fold defaultstate="collapsed" desc="HEADER">
    Thread t;
    private String threadName = "";
    private final URLUtils urlUtils = new URLUtils();
    private final List<String> UrlList;
    final List<Integer> Facebook = new ArrayList<>();
    final List<Integer> GooglePlus = new ArrayList<>();
    final List<Integer> Hatena = new ArrayList<>();
    final List<Integer> Linkedin = new ArrayList<>();
    final List<Integer> Buffer = new ArrayList<>();
    final List<Integer> Pinterest = new ArrayList<>();
    //</editor-fold>

//    public static URLUtils urlUtils = new URLUtils();
    public Social(String threadName, List<String> UrlList) throws InterruptedException {
        this.threadName = threadName;
        this.UrlList = UrlList;
        this.start();
        this.t.join();
    }

    //<editor-fold defaultstate="collapsed" desc="SOCIAL FACEBOOK">
    public List<FacebookDTO> getFacebook(String urls, String abc) {
        List<FacebookDTO> listFacebookResult = new ArrayList<>();
        String url = "http://api.facebook.com/restserver.php?method=links.getStats&format=json&urls=" + urls;
        // make connectURL to url
        String[] resultURL = urlUtils.connectURL(url, "Get Facebook", false);
        if (resultURL[0].equals("")) {
            return listFacebookResult;
        }
        try {
            Document doc = Jsoup.parse(resultURL[0], url);
            String resultJson = doc.text();
            JSONArray array = new JSONArray(resultJson);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                FacebookDTO facebook = new FacebookDTO();
                facebook.setUrl(obj.getString("url"));
                facebook.setShare(obj.getInt("share_count"));
                facebook.setLike(obj.getInt("like_count"));
                facebook.setComment(obj.getInt("comment_count"));
                facebook.setClick(obj.getInt("click_count"));
                System.out.println("FB" + obj.getInt("share_count"));
                listFacebookResult.add(facebook);
            }
        } catch (Exception e) {
            System.out.println(resultURL[0]);
        }
        return listFacebookResult;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="SOCIAL FACEBOOK">
    public int getFacebook(String urls) throws InterruptedException {
        int result = 0;
        String url = "http://api.facebook.com/restserver.php?method=links.getStats&format=json&urls=" + urls;
        // make connectURL to url
        String[] resultURL = urlUtils.connectURL(url, "Get Facebook", false);
        if (resultURL[0].equals("")) {
            return result;
        } else if (resultURL[0].contains("error") && resultURL[0].contains("\"code\": 190")) {
            Thread.sleep((long) 1.44e+7);
            return getFacebook(urls);
        }
        try {
            Document doc = Jsoup.parse(resultURL[0], url);
            String resultJson = doc.text();
            JSONArray array = new JSONArray(resultJson);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                FacebookDTO facebook = new FacebookDTO();
                return obj.getInt("share_count");
            }
        } catch (Exception e) {
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="SOCIAL GOOGLE +">
    public int getGooglePlus(String urls) throws InterruptedException {
        int result = 0;
        String url = "https://apis.google.com/_/+1/fastbutton?url=" + urls;
        // make connectURL to url
        String[] resultURL = urlUtils.connectURL(url, "Get GooglePlus", false);
        if (resultURL[0].equals("")) {
            return result;
        } else if (resultURL[0].equals("limit")) {
            Thread.sleep((long) 1.44e+7);
            return getGooglePlus(urls);
        }
        try {
            Document doc = Jsoup.parse(resultURL[0], url);
            Element counter = doc.select("script").get(3);
            String a = counter.html();
            Pattern p = Pattern.compile("\\{c: (.*)[ ]");
            Matcher m = p.matcher(counter.html());
            if (m.find()) {
                SocialDTO social = new SocialDTO();
                return Math.round(Float.parseFloat(m.group(1)));
            } else {
                Elements counters = doc.select("div #aggregateCount");
                return Integer.parseInt(counters.text());
            }
        } catch (Exception e) {
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="SOCIAL HATENA">
    public int getHatena(String urls) throws InterruptedException {
        int result = 0;
        String url = "http://api.b.st-hatena.com/entry.counts?url=" + urls;
        // make connectURL to url
        String[] resultURL = urlUtils.connectURL(url, "Get Hatena", false);
        if (resultURL[0].equals("")) {
            return result;
        } else if (resultURL[0].equals("limit")) {
            Thread.sleep((long) 1.44e+7);
            return getHatena(urls);
        }
        try {
            Document doc = Jsoup.parse(resultURL[0], url);
            Elements counter = doc.select("body");
            JSONObject jSONObject = new JSONObject(counter.text());
            Iterator<String> keys = jSONObject.keys();
            String key = keys.next();
            return jSONObject.getInt(key);

        } catch (Exception e) {
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="SOCIAL PINTEREST">
    public int getPinterest(String urls) throws InterruptedException {
        int result = 0;
        String url = "http://api.pinterest.com/v1/urls/count.json?url=" + urls;
        // make connectURL to url
        String[] resultURL = urlUtils.connectURL(url, "Get Pinterest", false);
        if (resultURL[0].equals("") || resultURL[0].contains("Invalid Url")) {
            return result;
        } else if (resultURL[0].equals("limit")) {
            Thread.sleep(600000);
            return getPinterest(urls);
        }
        try {
            Document doc = Jsoup.parse(resultURL[0], url);
            Elements counter = doc.select("body");
            Pattern p = Pattern.compile("\\((.*[^)])");
            Matcher m = p.matcher(counter.text());
            if (m.find()) {
                JSONObject jSONObject = new JSONObject(m.group(1));
                return jSONObject.getInt("count");
            }

        } catch (Exception e) {
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="SOCIAL LINKEDIN">
    public int getLinkedin(String urls) throws InterruptedException {
        int result = 0;
        String url = "https://www.linkedin.com/countserv/count/share?url=" + urls + "&format=json";
        // make connectURL to url
        String[] resultURL = urlUtils.connectURL(url, "Get Linkedin", false);
        if (resultURL[0].equals("")) {
            return result;
        } else if (resultURL[0].equals("limit")) {
            Thread.sleep((long) 1.44e+7);
            return getLinkedin(urls);
        }
        try {
            Document doc = Jsoup.parse(resultURL[0], url);
            Elements counter = doc.select("body");
            JSONObject jSONObject = new JSONObject(counter.text());
            return jSONObject.getInt("count");
        } catch (Exception e) {
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="SOCIAL BUFFER">
    public int getBuffer(String urls) throws InterruptedException {
        int result = 0;
        String url = "https://api.bufferapp.com/1/links/shares.json?url=" + urls;
        // make connectURL to url
        String[] resultURL = urlUtils.connectURL(url, "Get Buffer", false);
        if (resultURL[0].equals("")) {
            return result;
        } else if (resultURL[0].equals("limit") || resultURL[0].contains("Invalid url")) {
            Thread.sleep(15000);
            return getBuffer(urls);
        }
        try {
            Document doc = Jsoup.parse(resultURL[0], url);
            Elements counter = doc.select("body");
            JSONObject jSONObject = new JSONObject(counter.text());
            Iterator<String> keys = jSONObject.keys();
            return jSONObject.getInt("shares");
        } catch (Exception e) {
        }
        return result;
    }
    //</editor-fold>

    @Override
    public void start() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }

    @Override
    public void run() {
        switch (threadName) {
            case "Get Social":
                for (int i = 0; i < UrlList.size(); i++) {
                    try {
                        String url = UrlList.get(i);
                        Facebook.add(getFacebook(url));
                        GooglePlus.add(getGooglePlus(url));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Social.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            case "Get Buffer":
                for (int i = 0; i < UrlList.size(); i++) {
                    try {
                        Buffer.add(getBuffer(UrlList.get(i)));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Social.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                System.out.println("Buffer finish");
                break;
            case "Get Pinterest,Hatena":
                for (int i = 0; i < UrlList.size(); i++) {
                    try {
                        Pinterest.add(getPinterest(UrlList.get(i)));
                        Hatena.add(getHatena(UrlList.get(i)));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Social.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                System.out.println("Pinterest finish");
                break;
            case "Get Linkedin":
                for (int i = 0; i < UrlList.size(); i++) {
                    try {
                        Linkedin.add(getLinkedin(urlUtils.changeToEncodedUrl(UrlList.get(i))));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Social.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                System.out.println("Linkedin finish");
                break;
            default:
                break;
        }
    }
}
