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

package org.icepush.samples.icechat;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.icepush.PushContext;

public abstract class AbstractPushRequestContext implements Serializable,
		IPushRequestContext {

	protected String currentPushId;
	private transient PushContext pushContext;

	public String getCurrentPushId() {
		return currentPushId;
	}

	public PushContext getPushContext() {
		return pushContext;
	}

	protected void intializePushContext(HttpServletRequest request,
			HttpServletResponse response) throws IllegalArgumentException {
		if (request == null)
			throw new IllegalArgumentException("HttpServletRequest is null");
		if (response == null)
			throw new IllegalArgumentException("HttpServletResponse is null");
		pushContext = PushContext.getInstance(request.getSession()
				.getServletContext());
		currentPushId = pushContext.createPushId(request, response);
	}


}
