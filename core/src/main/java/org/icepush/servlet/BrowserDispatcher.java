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

package org.icepush.servlet;

import org.icepush.Configuration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BrowserDispatcher implements PseudoServlet {
    private final static Logger log = Logger.getLogger(BrowserDispatcher.class.getName());
    private final Map browserBoundServlets = new HashMap();
    private final long browserTimeout;

    public BrowserDispatcher(Configuration configuration) {
        this.browserTimeout = configuration.getAttributeAsLong("browserTimeout", 10 * 60 * 1000);
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        discardUnusedServlets();
        String browserID = getBrowserIDFromCookie(request);
        checkSession(browserID);
        lookupServer(browserID).service(request, response);
    }

    public void shutdown() {
        Iterator i = browserBoundServlets.values().iterator();
        while (i.hasNext()) {
            PseudoServlet servlet = (PseudoServlet) i.next();
            servlet.shutdown();
        }
    }

    protected abstract PseudoServlet newServer(String browserID) throws Exception;

    protected void checkSession(String browserID) throws Exception {
        synchronized (browserBoundServlets) {
            if (!browserBoundServlets.containsKey(browserID)) {
                browserBoundServlets.put(browserID, new BrowserEntry(browserID, this.newServer(browserID)));
            }
        }
    }

    protected PseudoServlet lookupServer(final String browserID) {
        return (PseudoServlet) browserBoundServlets.get(browserID);
    }

    private static String getBrowserIDFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("ice.push.browser".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private void discardUnusedServlets() {
        Iterator i = new ArrayList(browserBoundServlets.values()).iterator();
        while (i.hasNext()) {
            BrowserEntry entry = (BrowserEntry) i.next();
            entry.discardIfExpired();
        }
    }

    private class BrowserEntry implements PseudoServlet {
        private String id;
        private PseudoServlet servlet;
        private long lastAccess = System.currentTimeMillis();

        private BrowserEntry(String id, PseudoServlet servlet) {
            this.id = id;
            this.servlet = servlet;
            if (log.isLoggable(Level.FINEST)) {
                log.finest("New browser detected, assigning ID '" + id + "'.");
            }
        }

        public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
            lastAccess = System.currentTimeMillis();
            servlet.service(request, response);
        }

        public void shutdown() {
            servlet.shutdown();
        }

        public void discardIfExpired() {
            if (lastAccess + browserTimeout < System.currentTimeMillis()) {
                try {
                    if (log.isLoggable(Level.FINEST)) {
                        log.finest("Discard browser with ID '" + id + "' since is no longer used.");
                    }
                    servlet.shutdown();
                } catch (Throwable t) {
                    log.fine("Failed to discard browser bound server for ID=" + id);
                } finally {
                    browserBoundServlets.remove(id);
                    log.fine("Discarded browser bound server for ID=" + id);
                }
            }
        }
    }
}
