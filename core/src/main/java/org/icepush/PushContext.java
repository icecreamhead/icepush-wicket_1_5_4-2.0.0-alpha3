/*
 * Version: MPL 1.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2010 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 */

package org.icepush;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PushContext {
    private static final Logger log = Logger.getLogger(PushContext.class.getName());
    private static final String BrowserIDCookieName = "ice.push.browser";

    private final PushGroupManager pushGroupManager;

    private int browserCounter = 0;
    private int subCounter = 0;

    public PushContext(final ServletContext context, final PushGroupManager pushGroupManager) {
        this.pushGroupManager = pushGroupManager;
        context.setAttribute(PushContext.class.getName(), this);
    }

    public synchronized String createPushId(HttpServletRequest request, HttpServletResponse response) {
        String browserID = getBrowserIDFromCookie(request);
        if (browserID == null) {
            String currentBrowserID = (String)
                    request.getAttribute(BrowserIDCookieName);
            if (null == currentBrowserID) {
                browserID = generateBrowserID();
                Cookie cookie = new Cookie(BrowserIDCookieName, browserID);
                cookie.setPath("/");
                response.addCookie(cookie);
                request.setAttribute(BrowserIDCookieName, browserID);
            } else {
                browserID = currentBrowserID;
            }
        }

        String id = browserID + ":" + generateSubID();
        if (log.isLoggable(Level.FINEST)) {
            log.finest("Created new pushId '" + id + "'.");
        }
        return id;
    }

    public void push(final String groupName) {
        pushGroupManager.push(groupName);
    }

    public void addGroupMember(final String groupName, final String pushId) {
        pushGroupManager.addMember(groupName, pushId);
    }

    public void removeGroupMember(final String groupName, final String pushId) {
        pushGroupManager.removeMember(groupName, pushId);
    }

    public static synchronized PushContext getInstance(ServletContext context) {
        return (PushContext) context.getAttribute(PushContext.class.getName());
    }

    private static String getBrowserIDFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (BrowserIDCookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private synchronized String generateBrowserID() {
        return Long.toString(++browserCounter, 36) + Long.toString(System.currentTimeMillis(), 36);
    }

    private synchronized String generateSubID() {
        return Integer.toString((++subCounter) + (hashCode() / 10000), 36);
    }
}
