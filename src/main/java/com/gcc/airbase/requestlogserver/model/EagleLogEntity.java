package com.gcc.airbase.requestlogserver.model;

import cn.hutool.core.date.DateUtil;
import com.gcc.airbase.requestlogserver.enums.ItemType;
import lombok.Data;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.text.DecimalFormat;


@Data
public class EagleLogEntity implements Serializable {

    private String FORMAR_STR = "  INFO  [        task-x] EagleEye-Listener                        : ";

    private String itemStr = "=======================";

    private final static String START_STR = "【Request Start】";

    private String timeStr;

    private String uri;

    private String method;

    private String host;

    private String param;

    private String spanStr;

    private String endStr;

    private final static String END_STR = "【Request   End】";

    public EagleLogEntity(){}

    public EagleLogEntity(HttpServletRequest request){
      this.timeStr = DateUtil.now();
      this.host = getHost(request);
      this.uri = request.getRequestURI();
      this.method = request.getMethod();
      this.param = getParam();
    }


    public String getLogInfo(){
        return getLogInfo(null,true);
    }

    public String getLogInfo(String varflag,boolean spanflag){
        String item = itemStr;
        if(varflag == null){
           item = appendStr(varflag,23);
        }
        FORMAR_STR = "\n"+this.timeStr+FORMAR_STR;
        StringBuilder infoStr = new StringBuilder(FORMAR_STR);
        infoStr.append(item+START_STR+item)
                .append(FORMAR_STR)
                .append("request-host   :"+this.host)
                .append(FORMAR_STR)
                .append("request-method :"+this.method)
                .append(FORMAR_STR)
                .append("request-uri    :"+this.uri)
                .append(FORMAR_STR)
                .append("request-param  :"+this.param);
        if(spanflag){
                infoStr.append(FORMAR_STR).append("request-span   :"+this.spanStr);
        }
        infoStr.append(FORMAR_STR).append(item+END_STR+item);
        return infoStr.toString();
    }

    private String getHost(HttpServletRequest request){
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("0:0:0:0:0:0:0:1")) {
                return "127.0.0.1";
            }
            return ipAddress;
        }
        if (ipAddress != null && ipAddress.length() > 15) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    private String getParams(HttpServletRequest request){
        try {
            return new String(StreamUtils.copyToByteArray(request.getInputStream()), request.getCharacterEncoding()).trim();
        }catch (Exception e){
            return " get parameter error ";
        }
    }

    private String appendStr(String item,int size){
        if(ItemType.DEFAULT_STR != ItemType.valueOf(item)) {
            StringBuilder reStr = new StringBuilder(item);
            for (int i = 0; i < size; i++) {
                reStr.append(item);
            }
            return reStr.toString();
        }else {
            return itemStr;
        }
    }

    public void calculateTimeSpan(Boolean flag,Long startTime,Long endTime){
        if(flag) {
            long a = endTime -startTime;
            Double b = Double.parseDouble(""+a);
            Double span = b/1000;//DateUtil.between(DateUtil.date(startTime),DateUtil.date(endTime), DateUnit.SECOND);
            this.spanStr = new DecimalFormat("#.000").format(span) +"s";
        }
    }

}
