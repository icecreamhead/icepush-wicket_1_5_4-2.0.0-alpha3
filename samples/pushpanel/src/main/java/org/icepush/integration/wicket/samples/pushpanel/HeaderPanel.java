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

import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @version
 */

public class HeaderPanel extends Panel {

    /**
     * Construct.
     * @param componentName name of the component
     */

    public HeaderPanel(String componentName)
    {
        super(componentName);
        add(new ContextImage("headerImage","./images/banner_hdr.jpg"));
        add(new ContextImage("wicketLogo","./images/wicket.png"));
    }

}
