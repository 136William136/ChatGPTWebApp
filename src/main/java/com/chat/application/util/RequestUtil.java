package com.chat.application.util;

import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.HttpServletRequest;

public class RequestUtil {

    public static String getRequestIp(){
        VaadinServletRequest vaadinRequest = (VaadinServletRequest) VaadinService.getCurrentRequest();
        HttpServletRequest httpServletRequest = vaadinRequest.getHttpServletRequest();
        String ipAddress = httpServletRequest.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = httpServletRequest.getRemoteAddr();
        }
        return ipAddress;
    }

}
