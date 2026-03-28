package com.bupt.ta.util;

import com.bupt.ta.model.Role;

public final class RouteUtil {
    private RouteUtil() {
    }

    public static String getDashboardPath(Role role) {
        if (role == Role.TA_APPLICANT) {
            return "/applicant/dashboard";
        }
        if (role == Role.MODULE_ORGANISER) {
            return "/mo/dashboard";
        }
        return "/admin/dashboard";
    }
}
