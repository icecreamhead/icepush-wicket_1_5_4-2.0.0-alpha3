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

package org.icepush.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;

public interface Request {

    String getMethod();

    URI getURI();

    String[] getHeaderNames();

    String getHeader(String name);

    String[] getHeaderAsStrings(String name);

    Date getHeaderAsDate(String name);

    int getHeaderAsInteger(String name);

    boolean containsParameter(String name);

    String[] getParameterNames();

    String getParameter(String name);

    String[] getParameterAsStrings(String name);

    int getParameterAsInteger(String name);

    boolean getParameterAsBoolean(String name);

    String getParameter(String name, String defaultValue);

    int getParameterAsInteger(String name, int defaultValue);

    boolean getParameterAsBoolean(String name, boolean defaultValue);

    String getLocalAddr();

    String getLocalName();

    String getRemoteAddr();

    String getRemoteHost();

    String getServerName();

    InputStream readBody() throws IOException;

    void readBodyInto(OutputStream out) throws IOException;

    void respondWith(ResponseHandler handler) throws Exception;

    void detectEnvironment(Environment environment) throws Exception;

    //avoid runtime dependency on Portlet interfaces,
    //and for the symmetry's sake, same for the Servlet interfaces
    interface Environment {

        void servlet(Object request, Object response) throws Exception;

        void portlet(Object request, Object response, Object config) throws Exception;
    }
}
