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

import org.icepush.CodeServer;
import org.icepush.Configuration;
import org.icepush.ConfigurationServer;
import org.icepush.ProductInfo;
import org.icepush.PushContext;
import org.icepush.PushGroupManager;
import org.icepush.PushGroupManagerFactory;
import org.icepush.http.standard.CacheControlledServer;
import org.icepush.http.standard.CompressingServer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.SocketException;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainServlet
        implements PseudoServlet {
    private static Logger log = Logger.getLogger(MainServlet.class.getName());
    private PseudoServlet dispatcher;
    private Timer timer;

    public MainServlet(final ServletContext context) {
        log.info(new ProductInfo().toString());

        timer = new Timer(true);
        final Configuration configuration = new ServletContextConfiguration("org.icepush", context);
        final PushGroupManager pushGroupManager = PushGroupManagerFactory.newPushGroupManager(context);
        final PushContext pushContext = new PushContext(context, pushGroupManager);
        PathDispatcher pathDispatcher = new PathDispatcher();
        pathDispatcher.dispatchOn(".*code\\.icepush", new BasicAdaptingServlet(new CacheControlledServer(new CompressingServer(new CodeServer(context)))));
        pathDispatcher.dispatchOn(".*configuration\\.icepush", new BasicAdaptingServlet(new CacheControlledServer(new CompressingServer(new ConfigurationServer(context)))));
        pathDispatcher.dispatchOn(".*", new BrowserDispatcher(configuration) {
            protected PseudoServlet newServer(String browserID) {
                return new BrowserBoundServlet(pushContext, pushGroupManager, timer, configuration);
            }
        });

        dispatcher = pathDispatcher;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            dispatcher.service(request, response);
        } catch (SocketException e) {
            if ("Broken pipe".equals(e.getMessage())) {
                // client left the page
                if (log.isLoggable(Level.FINEST)) {
                    log.log(Level.FINEST, "Connection broken by client.", e);
                } else if (log.isLoggable(Level.FINE)) {
                    log.log(Level.FINE, "Connection broken by client: " + e.getMessage());
                }
            } else {
                throw new ServletException(e);
            }
        } catch (RuntimeException e) {
            //Tomcat won't properly redirect to the configured error-page.
            //So we need a new RuntimeException that actually includes a message.
            if (e.getMessage() == null) {
                throw new RuntimeException("wrapped Exception: " + e, e);
            } else {
                throw e;
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public void shutdown() {
        dispatcher.shutdown();
        timer.cancel();
    }
}
