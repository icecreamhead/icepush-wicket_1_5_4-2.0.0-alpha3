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

package org.icepush.http.standard;

import org.icepush.http.Request;
import org.icepush.http.ResponseHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;


public class RequestProxy implements Request {
    protected Request request;

    public RequestProxy(Request request) {
        this.request = request;
    }

    public String getMethod() {
        return request.getMethod();
    }

    public URI getURI() {
        return request.getURI();
    }

    public String[] getHeaderNames() {
        return request.getHeaderNames();
    }

    public String getHeader(String name) {
        return request.getHeader(name);
    }

    public String[] getHeaderAsStrings(String name) {
        return request.getHeaderAsStrings(name);
    }

    public Date getHeaderAsDate(String name) {
        return request.getHeaderAsDate(name);
    }

    public int getHeaderAsInteger(String name) {
        return request.getHeaderAsInteger(name);
    }

    public boolean containsParameter(String name) {
        return request.containsParameter(name);
    }

    public String[] getParameterNames() {
        return request.getParameterNames();
    }

    public String getParameter(String name) {
        return request.getParameter(name);
    }

    public String[] getParameterAsStrings(String name) {
        return request.getParameterAsStrings(name);
    }

    public int getParameterAsInteger(String name) {
        return request.getParameterAsInteger(name);
    }

    public boolean getParameterAsBoolean(String name) {
        return request.getParameterAsBoolean(name);
    }

    public String getParameter(String name, String defaultValue) {
        return request.getParameter(name, defaultValue);
    }

    public int getParameterAsInteger(String name, int defaultValue) {
        return request.getParameterAsInteger(name, defaultValue);
    }

    public boolean getParameterAsBoolean(String name, boolean defaultValue) {
        return request.getParameterAsBoolean(name, defaultValue);
    }

    public String getLocalAddr() {
        return request.getLocalAddr();
    }

    public String getLocalName() {
        return request.getLocalName();
    }

    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    public String getRemoteHost() {
        return request.getRemoteHost();
    }

    public String getServerName() {
        return request.getServerName();
    }

    public InputStream readBody() throws IOException {
        return request.readBody();
    }

    public void readBodyInto(OutputStream out) throws IOException {
        request.readBodyInto(out);
    }

    public void respondWith(ResponseHandler handler) throws Exception {
        request.respondWith(handler);
    }

    public void detectEnvironment(Environment environment) throws Exception {
        request.detectEnvironment(environment);
    }
}
