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
 * The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2010 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
*/

package org.icepush.samples.icechat.wicket;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.icepush.samples.icechat.AbstractPushRequestContext;
import org.icepush.samples.icechat.IPushRequestContext;

/**
 *
 */
public abstract class PushPanel extends Panel {

    IPushRequestContext pushRequestContext;

    Label pushJavascript;
    String javascriptString;

    final AbstractDefaultAjaxBehavior behave;

    public PushPanel(String id) {
        super (id);

        // Push callback.  Called from wicketAjaxGet method.
        behave = new AbstractDefaultAjaxBehavior() {
            protected void respond(final AjaxRequestTarget target) {

                pushCallback(target);

            }
        };
        add(behave);

        // ICEpush code
        getPushRequestContext();
        javascriptString = "ice.push.register(['" + pushRequestContext.getCurrentPushId() + "'],function(){wicketAjaxGet('')});";
        pushJavascript = new Label("pushJavascript", new PropertyModel(this,"javascriptString"));
        pushJavascript.setEscapeModelStrings(false);
        add(pushJavascript);
        pushRequestContext.getPushContext().addGroupMember("CHAT_ROOMS", pushRequestContext.getCurrentPushId());
    }

    @Override
    protected void onBeforeRender(){
        String tempJavascriptString = "ice.push.register(['" + pushRequestContext.getCurrentPushId() + "'],function(){wicketAjaxGet('" + behave.getCallbackUrl() + "')});";
        if(!javascriptString.equals(tempJavascriptString)){
            javascriptString = tempJavascriptString;
            pushJavascript.modelChanged();
        }
        super.onBeforeRender();
    }

    public String getJavascriptString() {
        return javascriptString;
    }

    public void setJavascriptString(String javascriptString) {
        this.javascriptString = javascriptString;
    }

    abstract protected void pushCallback(AjaxRequestTarget target);

    class WicketPushRequestContextAdapter extends AbstractPushRequestContext{
            public WicketPushRequestContextAdapter(){
                    WebRequest webRequest = (WebRequest)getRequest();
                    WebResponse webResponse = (WebResponse)getResponse();

                    intializePushContext((HttpServletRequest)webRequest.getHttpServletRequest(),
                                    (HttpServletResponse)webResponse.getHttpServletResponse());
            }
    }

    public IPushRequestContext getPushRequestContext(){
            if( pushRequestContext == null ){
                    pushRequestContext = new WicketPushRequestContextAdapter();
            }
            return pushRequestContext;
    }
}
