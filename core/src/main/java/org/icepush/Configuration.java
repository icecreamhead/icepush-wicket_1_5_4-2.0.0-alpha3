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

package org.icepush;

public abstract class Configuration {

    public abstract String getName();

    public abstract Configuration getChild(String child) throws ConfigurationException;

    public abstract Configuration[] getChildren(String name) throws ConfigurationException;

    public abstract String getAttribute(String paramName) throws ConfigurationException;

    public abstract String getValue() throws ConfigurationException;

    public int getAttributeAsInteger(String name) throws ConfigurationException {
        return Integer.parseInt(getAttribute(name));
    }

    public long getAttributeAsLong(String name) throws ConfigurationException {
        return Long.parseLong(getAttribute(name));
    }

    public float getAttributeAsFloat(String name) throws ConfigurationException {
        return Float.parseFloat(getAttribute(name));
    }

    public double getAttributeAsDouble(String name) throws ConfigurationException {
        return Double.parseDouble(getAttribute(name));
    }

    public boolean getAttributeAsBoolean(String name) throws ConfigurationException {
        return Boolean.valueOf(getAttribute(name)).booleanValue();
    }

    public String getAttribute(String name, String defaultValue) {
        try {
            return getAttribute(name);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public int getAttributeAsInteger(String name, int defaultValue) {
        try {
            return getAttributeAsInteger(name);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public long getAttributeAsLong(String name, long defaultValue) {
        try {
            return getAttributeAsLong(name);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public float getAttributeAsFloat(String name, float defaultValue) {
        try {
            return getAttributeAsFloat(name);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public double getAttributeAsDouble(String name, double defaultValue) {
        try {
            return getAttributeAsDouble(name);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public boolean getAttributeAsBoolean(String name, boolean defaultValue) {
        try {
            return getAttributeAsBoolean(name);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public int getValueAsInteger() throws ConfigurationException {
        return Integer.parseInt(getValue());
    }

    public float getValueAsFloat() throws ConfigurationException {
        return Float.parseFloat(getValue());
    }

    public double getValueAsDouble() throws ConfigurationException {
        return Double.parseDouble(getValue());
    }

    public boolean getValueAsBoolean() throws ConfigurationException {
        return Boolean.valueOf(getValue()).booleanValue();
    }

    public long getValueAsLong() throws ConfigurationException {
        return Long.parseLong(getValue());
    }

    public String getValue(String defaultValue) {
        try {
            return getValue();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public int getValueAsInteger(int defaultValue) {
        try {
            return getValueAsInteger();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public long getValueAsLong(long defaultValue) {
        try {
            return getValueAsLong();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public float getValueAsFloat(float defaultValue) {
        try {
            return getValueAsFloat();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public double getValueAsDouble(double defaultValue) {
        try {
            return getValueAsDouble();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public boolean getValueAsBoolean(boolean defaultValue) {
        try {
            return getValueAsBoolean();
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
