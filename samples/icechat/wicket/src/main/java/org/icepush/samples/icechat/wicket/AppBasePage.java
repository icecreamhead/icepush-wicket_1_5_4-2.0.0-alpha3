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

import javax.inject.Inject;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.ContextImage;
import org.icepush.samples.icechat.cdi.service.ChatServiceApplicationBean;

public class AppBasePage extends WebPage {

	@Inject
	ChatServiceApplicationBean chatService;

	public AppBasePage() {
		super();
		add(new ContextImage("headerImage", "./img/ICEpush-Wicket.png"));
		add(new ContextImage("wicketLogo", "./img/logos/wicket.png"));
	}
}
