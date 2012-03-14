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

import org.icepush.http.Response;
import org.icepush.http.ResponseHandler;
import org.icepush.http.standard.ResponseHandlerServer;

import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;

public class CodeServer extends ResponseHandlerServer {
    public CodeServer(final ServletContext context) {
        super(new ResponseHandler() {
            public void respond(Response response) throws Exception {
                response.setHeader("Content-Type", "text/javascript");
                InputStream code = CodeServer.class.getResourceAsStream("/icepush.js");
                InputStream configuration = new ByteArrayInputStream(("ice.push.configuration.contextPath='" + context.getContextPath() + "';").getBytes("UTF-8"));

                response.writeBodyFrom(new SequenceInputStream(code, configuration));
            }
        });
    }
}