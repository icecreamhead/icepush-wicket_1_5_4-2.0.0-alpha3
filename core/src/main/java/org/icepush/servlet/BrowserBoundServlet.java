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

import java.util.Timer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.icepush.BlockingConnectionServer;
import org.icepush.Configuration;
import org.icepush.PushContext;
import org.icepush.PushGroupManager;

public class BrowserBoundServlet extends PathDispatcher {
    private PushContext pushContext;

    public BrowserBoundServlet(PushContext pushContext, final PushGroupManager pushGroupManager, final Timer monitorRunner, Configuration configuration) {
        this.pushContext = pushContext;

        dispatchOn(".*listen\\.icepush", new EnvironmentAdaptingServlet(new BlockingConnectionServer(pushGroupManager, monitorRunner, configuration), configuration));
        dispatchOn(".*create-push-id\\.icepush", new CreatePushID());
        dispatchOn(".*notify\\.icepush", new NotifyPushID());
        dispatchOn(".*add-group-member\\.icepush", new AddGroupMember());
        dispatchOn(".*remove-group-member\\.icepush", new RemoveGroupMember());
    }

    private class CreatePushID extends AbstractPseudoServlet {
        public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
            response.setContentType("text/plain");
            response.getWriter().write(pushContext.createPushId(request, response));
        }
    }

    private class NotifyPushID extends AbstractPseudoServlet {
        public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
            String id = request.getParameter("id");
            pushContext.push(id);
            response.setContentType("text/plain");
            response.setContentLength(0);
        }
    }

    private class AddGroupMember extends AbstractPseudoServlet {
        public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
            String group = request.getParameter("group");
            String pushID = request.getParameter("id");
            pushContext.addGroupMember(group, pushID);
            response.setContentType("text/plain");
            response.setContentLength(0);
        }
    }

    private class RemoveGroupMember extends AbstractPseudoServlet {
        public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
            String group = request.getParameter("group");
            String pushID = request.getParameter("id");
            pushContext.removeGroupMember(group, pushID);
            response.setContentType("text/plain");
            response.setContentLength(0);
        }
    }
}
