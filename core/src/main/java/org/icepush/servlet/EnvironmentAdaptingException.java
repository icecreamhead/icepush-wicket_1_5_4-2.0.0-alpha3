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

import javax.servlet.ServletException;

public class EnvironmentAdaptingException extends ServletException {
    public EnvironmentAdaptingException() {
        super();
    }

    public EnvironmentAdaptingException(final String message) {
        super(message);
    }

    public EnvironmentAdaptingException(final String message, final Throwable rootCause) {
        super(message, rootCause);
    }

    public EnvironmentAdaptingException(final Throwable rootCause) {
        super(rootCause);
    }
}
