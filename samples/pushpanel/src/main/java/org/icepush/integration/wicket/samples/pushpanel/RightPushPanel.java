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

package org.icepush.integration.wicket.samples.pushpanel;

import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.icepush.integration.wicket.core.PushPanel;

/**
 * Example ICEpush integration.
 * This class extends the PushPanel class to to create a push group with the
 * same name as the component id.
 *
 * STEPS TO IMPLEMENT PUSH FOR THIS PANEL:
 * 1. Add push javascript to the html page:
 *    <script wicket:id="pushJavascript" type="text/javascript">
 *    </script>
 *    WARNING: Do not nest this script in a component that is updated via AJAX
 *             This will result in the script being executed multiple times.
 * 2. Add push call(s) to this panel:
 *    push();
 * 3. Implement pushCallback(AjaxRequestTarget target) method to update your
 *    model and render the appropriate components on callback.
 *
 * In this example, a click from "rightButton" will push an update out to all
 * members of the "rightPushPanel" group.  The model update can be found in the
 * pushCallback() method.  In this case, we simply add a String stating the
 * source of the push.
 */
public final class RightPushPanel extends PushPanel {

    Form rightForm;

    final ListView pushListView;
    private List pushList = new ArrayList();

    boolean isPushMine = false;

    public RightPushPanel(String id) {
        super (id);
        this.setOutputMarkupId(true);

        rightForm = new Form("rightForm");
        rightForm.setOutputMarkupId(true);

        rightForm.add(new AjaxButton("rightButton") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                // PUSH CALL
                push();
                isPushMine=true;
                target.addComponent(this.getParent());
        }
        });

        // Button used to clear push source list
        rightForm.add(new AjaxButton("rightClear") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                pushList.clear();
                target.addComponent(this.getParent());
            }
        });

        // List containing the source of each push
        rightForm.add(pushListView = new ListView("pushListView", pushList){
            public void populateItem(final ListItem listItem){
                listItem.add(new Label("pushSource",(String)listItem.getModelObject()));
            }
        });

        add(rightForm);
    
    }

    // PUSH CALLBACK
    protected void pushCallback(AjaxRequestTarget target) {
        if(isPushMine){
            pushList.add("My Push.");
        }else{
            pushList.add("Pushed From Another User.");
        }
        isPushMine=false;
        pushListView.modelChanged();
        target.addComponent(rightForm);
    }

}
