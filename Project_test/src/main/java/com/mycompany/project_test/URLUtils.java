package com.mycompany.project_test;

//<editor-fold defaultstate="collapsed" desc="IMPORT">
import java.net.URL;
import java.net.URLEncoder;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
//</editor-fold>

/**
 *
 * @author Nguyen Ngoc Hoang
 */
public class URLUtils {

    //<editor-fold defaultstate="collapsed" desc="CONNECT URL">
    public String[] connectURL(String url, String username, String function, boolean isFollowRedirect) {
        String[] result = {"", "408", ""};
        System.getProperties().put("proxySet", "true");
        System.getProperties().put("proxyHost", "host");
        System.getProperties().put("proxyPort", "port");
        try {
            Response res = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31").timeout(30000).ignoreContentType(true).ignoreHttpErrors(true).followRedirects(isFollowRedirect).execute();
            result[1] = res.statusCode() + "";
            result[2] = res.contentType();
            if (res.statusCode() == 200) {
                result[0] = res.parse().html();
            } else if (res.statusCode() == 429) {
                result[0] = "limit";
                System.out.println("limit");
            }
        } catch (Exception e) {
            //connect by CURL
            return connectUrlByCurl(url);
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="CONNECT URL BY CURL">
    public String[] connectUrlByCurl(String url) {
        String[] result = {"", "408", ""};
        //Runtime reading command line
        Runtime rt = Runtime.getRuntime();
        RuntimeExec rte = new RuntimeExec();
        RuntimeExec.StreamWrapper output;
        try {
            Process proc_score = rt.exec(new String[]{"sh", "-c", "curl " + url});
            output = rte.getStreamWrapper(proc_score.getInputStream(), "UTF-8");
            output.start();
            output.join(30000);
            result[0] = output.getMessage().toString();
            result[1] = "200";
            result[2] = "text/html";

        } catch (Exception e) {
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="CONNECT URL 123SERVER GET EUB">
    public String[] connectURL123ServerGetEUB(String domain123Server, String ip, String htmlMain, String[] htmlInternalList, String username) {
        String[] result = {"", "408", ""};
        System.getProperties().put("proxySet", "true");
        System.getProperties().put("proxyHost", "host");
        System.getProperties().put("proxyPort", "port");
        try {
            Response res = Jsoup.connect("http://" + domain123Server + "/getEUB2.cgi").userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31")
                    .data("ip", ip)
                    .data("htmlMain", htmlMain)
                    .data("html1", htmlInternalList[0])
                    .data("html2", htmlInternalList[1])
                    .data("html3", htmlInternalList[2])
                    .data("html4", htmlInternalList[3])
                    .data("html5", htmlInternalList[4])
                    .data("type", "html")
                    .method(Method.POST)
                    .timeout(120000).ignoreContentType(true).ignoreHttpErrors(true).followRedirects(true).execute();
            result[1] = res.statusCode() + "";
            result[2] = res.contentType();
            if (res.statusCode() == 200) {
                result[0] = res.parse().toString();
            }
        } catch (Exception e) {
//            logFileUtils.writeLog(username, ExceptionUtils.getStackTrace(e));
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="CHANGE CODE">
    public String changeToPunycode(String s) {
        try {
            URL url = new URL(s);
            String oldHost = url.getHost();
            String newHost = java.net.IDN.toASCII(oldHost);
            s = s.replaceFirst(oldHost, newHost);
        } catch (Exception e) {
        }
        return s;
    }

    public String changeToEncodedUrl(String s) {
        try {
            URL url = new URL(s);
            String oldPath = url.getPath();
            String newPath = URLEncoder.encode(oldPath, "UTF-8");
            s = s.replaceFirst(oldPath, newPath).replaceAll("%2F", "/");
        } catch (Exception e) {
        }
        return s;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="GET URL MOBILE">
    public String getUrlMobile(String url) {
        try {
            Response res = Jsoup.connect(url).userAgent("Mozilla/5.0 (Linux; U; Android 4.3; en-us; SM-N900T Build/JSS15J) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30").timeout(30000).ignoreContentType(true).ignoreHttpErrors(true).followRedirects(true).execute();
            return res.url().toString();
        } catch (Exception e) {
        }
        return url;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="DECODE HOST URL">
    public String decodeHostURL(String strURL) {
        try {
            URL url = new URL(strURL);
            String oldHost = url.getHost();
            String newHost = java.net.IDN.toUnicode(oldHost);
            strURL = strURL.replaceFirst(oldHost, newHost);
        } catch (Exception e) {
        }
        return strURL;
    }
    //</editor-fold>
}
