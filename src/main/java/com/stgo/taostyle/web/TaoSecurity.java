package com.stgo.taostyle.web;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.stgo.taostyle.domain.orders.TaxonomyMaterial;

public class TaoSecurity {

    public static boolean isHecker(
            HttpServletRequest request) {
        HttpSession session = request.getSession();
        String ip = request.getRemoteAddr();

        // out-region ip range can be added into dangerous_ip of for_demo user.
        Object dangerous_ip = session.getAttribute("dangerous_ip");
        if (dangerous_ip != null) {
            dangerous_ip = dangerous_ip.toString().split(",");
            for (String forbiddenIp : (String[]) dangerous_ip) {
                if (ip.startsWith(forbiddenIp)) {
                    return true;
                }
            }
        }

        // internal visit can be trusted. so employ can visit any time.
        if (ip.startsWith("192.168.")) {
            return false;
        }

        // some ip might be set limitation, if not set, each ip can order 3 times a day. if set to 0, then sorry~
        int limit = 4;
        Object limitation = session.getAttribute(ip);
        if (limitation != null) {
            try {
                limit = Integer.valueOf(limitation.toString());
                if (limit <= 0) {
                    return true; // some IP is set to 0????? ok, while the ip can be used by others.....
                }
            } catch (Exception e) {
            }
        }

        List<TaxonomyMaterial> taxonomyMaterials = TaxonomyMaterial.findTaxonomyMaterialByLocation(ip);
        if (taxonomyMaterials == null || taxonomyMaterials.size() < limit) {
            return false;
        } else {
            TaxonomyMaterial taxonomyMaterial = taxonomyMaterials.get(limit - 1);
            Date date = taxonomyMaterial.getLogtime();
            if (date == null) {
                return false;
            } else if (taxonomyMaterial.getLogtime().getDay() != new Date().getDate()) {
                return false;
            }
        }

        return true;
    }
}
