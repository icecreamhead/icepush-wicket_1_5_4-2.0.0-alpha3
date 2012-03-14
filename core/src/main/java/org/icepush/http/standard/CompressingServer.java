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
import org.icepush.http.Response;
import org.icepush.http.ResponseHandler;
import org.icepush.http.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class CompressingServer implements Server {
    private Server server;

    public CompressingServer(Server server) {
        this.server = server;
    }

    public void service(Request request) throws Exception {
        String acceptEncodingHeader = request.getHeader("Accept-Encoding");
        if (acceptEncodingHeader != null && (acceptEncodingHeader.indexOf("gzip") >= 0 || acceptEncodingHeader.indexOf("compress") >= 0)) {
            server.service(new CompressingRequest(request));
        } else {
            server.service(request);
        }
    }

    public void shutdown() {
        server.shutdown();
    }

    private class CompressingRequest extends RequestProxy {
        public CompressingRequest(Request request) {
            super(request);
        }

        public void respondWith(final ResponseHandler handler) throws Exception {
            request.respondWith(new ResponseHandler() {
                public void respond(Response response) throws Exception {
                    CompressingResponse compressingResponse = new CompressingResponse(response);
                    handler.respond(compressingResponse);
                    compressingResponse.finishCompression();
                }
            });
        }
    }

    private class CompressingResponse extends ResponseProxy {
        private GZIPOutputStream output;

        public CompressingResponse(Response response) {
            super(response);
            response.setHeader("Content-Encoding", "gzip");
        }

        public OutputStream writeBody() throws IOException {
            return output = new GZIPOutputStream(response.writeBody());
        }

        public void writeBodyFrom(InputStream in) throws IOException {
            try {
                copy(in, writeBody());
            } finally {
                in.close();
            }
        }

        public void finishCompression() throws IOException {
            if (output != null) {
                output.finish();
            }
        }
    }

    private static void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buf = new byte[4096];
        int len;
        while ((len = input.read(buf)) > -1) output.write(buf, 0, len);
    }
}
