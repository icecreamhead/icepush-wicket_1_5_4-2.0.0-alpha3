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

import org.icepush.Configuration;
import org.icepush.ConfigurationException;

import javax.servlet.ServletContext;

public class ServletContextConfiguration extends Configuration {
    private final String name;
    private ServletContext context;

    public ServletContextConfiguration(String prefix, ServletContext context) {
        this.name = prefix;
        this.context = context;
    }

    public String getName() {
        return name;
    }

    public Configuration getChild(String child) throws ConfigurationException {
        String childName = postfixWith(child);
        String value = context.getInitParameter(childName);
        if (value == null) {
            throw new ConfigurationException("Cannot find parameter: " + childName);
        } else {
            return new ServletContextConfiguration(childName, context);
        }
    }

    public Configuration[] getChildren(String name) throws ConfigurationException {
        return new Configuration[]{getChild(name)};
    }

    public String getAttribute(String paramName) throws ConfigurationException {
        String attributeName = postfixWith(paramName);
        String value = context.getInitParameter(attributeName);
        if (value == null) {
            throw new ConfigurationException("Cannot find parameter: " + attributeName);
        } else {
            return value;
        }
    }

    public String getValue() throws ConfigurationException {
        String value = context.getInitParameter(name);
        if (value == null) {
            throw new ConfigurationException("Cannot find parameter: " + name);
        } else {
            return value;
        }
    }

    private String postfixWith(String child) {
        return name + '.' + child;
    }
}
