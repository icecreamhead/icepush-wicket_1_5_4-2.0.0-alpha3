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

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ICEpushServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(MainServlet.class.getName());

    private PseudoServlet mainServlet;

    public void init(final ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        mainServlet = new MainServlet(servletConfig.getServletContext());
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            mainServlet.service(request, response);
        } catch (ServletException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void destroy() {
        mainServlet.shutdown();
    }
}
