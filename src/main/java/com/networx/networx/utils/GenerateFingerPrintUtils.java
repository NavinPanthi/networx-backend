package com.networx.networx.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.DigestUtils;

public class GenerateFingerPrintUtils {

    public static String generateFingerprint(
            HttpServletRequest request
    ) {

        String userAgent =
                request.getHeader("User-Agent");

        String ipAddress =
                request.getRemoteAddr();

        return DigestUtils.sha256Hex(
                userAgent + ipAddress
        );
    }
}