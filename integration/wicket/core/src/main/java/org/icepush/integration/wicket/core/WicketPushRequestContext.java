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
 * The Initial Developer of the Original Code is ICEsoft Technologies Canada, 
 * Corp. Portions created by ICEsoft are Copyright (C) 2004-2010 ICEsoft 
 * Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
*/

package org.icepush.integration.wicket.core;

import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.icepush.PushContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * A WicketPushRequestContext is created for each PushPanel.
 */
public class WicketPushRequestContext implements Serializable{

    private String currentPushId;

    public WicketPushRequestContext(WebRequest webRequest, WebResponse webResponse){

            intializePushContext((HttpServletRequest)webRequest.getContainerRequest(),
                            (HttpServletResponse)webResponse.getContainerResponse());
    }


    public String getCurrentPushId() {
            return currentPushId;
    }

    protected void intializePushContext(HttpServletRequest request,
                    HttpServletResponse response) throws IllegalArgumentException {
            if (request == null){
                throw new IllegalArgumentException("HttpServletRequest is null");
            }
            if (response == null){
                    throw new IllegalArgumentException("HttpServletResponse is null");
            }
            currentPushId = PushContext.getInstance(request.getSession().getServletContext()).createPushId(request, response);
    }
}
