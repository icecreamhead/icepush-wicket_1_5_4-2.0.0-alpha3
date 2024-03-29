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

import javax.servlet.ServletContext;

import org.icepush.http.Request;
import org.icepush.http.Response;
import org.icepush.http.ResponseHandler;
import org.icepush.http.Server;

public class ConfigurationServer implements Server, ResponseHandler {
    private String configCode;

    public ConfigurationServer(final ServletContext servletContext) {
        String uriPrefix = (String)servletContext.getAttribute("uriPrefix");
        if (uriPrefix == null) {
            uriPrefix = "";
        }
        String uriSuffix = (String)servletContext.getAttribute("uriSuffix");
        if (uriSuffix == null) {
            uriSuffix = "";
        }
        configCode =
                "ice.push.configuration.uriSuffix='" + uriSuffix + "';" +
                        "ice.push.configuration.uriPrefix='" + uriPrefix + "';";
    }

    public void service(Request request) throws Exception {
        request.respondWith(this);
    }

    public void respond(Response response) throws Exception {
        response.setHeader("Content-Type", "text/javascript");
        response.writeBody().write(configCode.getBytes("UTF-8"));
    }

    public void shutdown() {
    }
}