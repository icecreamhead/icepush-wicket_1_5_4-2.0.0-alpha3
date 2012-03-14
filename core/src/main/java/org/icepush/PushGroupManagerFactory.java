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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import org.icepush.util.AnnotationScanner;

public class PushGroupManagerFactory {
    private static final Logger LOGGER = Logger.getLogger(PushGroupManagerFactory.class.getName());

    public static PushGroupManager newPushGroupManager(final ServletContext servletContext) {
        Object _scanning = servletContext.getAttribute("scanning");
        if (_scanning == null || (Boolean)_scanning) {
            Set<String> _annotationSet = new HashSet<String>();
            _annotationSet.add("Lorg/icepush/ExtendedPushGroupManager;");
            AnnotationScanner _annotationScanner = new AnnotationScanner(_annotationSet, servletContext);
            try {
                // throws IOException
                Class[] _classes = _annotationScanner.getClasses();
                if (_classes.length > 0) {
                    return
                        (PushGroupManager)
                            _classes[0].
                                // throws NoSuchMethodException, SecurityException
                                getConstructor(ServletContext.class).
                                // throws
                                //     IllegalAccessException, IllegalArgumentException, InstantiationException,
                                //     InvocationTargetException, ExceptionInInitializerError
                                newInstance(servletContext);
                }
            } catch (IOException exception) {
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.log(
                        Level.FINEST,
                        "An I/O error occurred while trying to get the classes.",
                        exception);
                }
                // Do nothing.
            } catch (NoSuchMethodException exception) {
                // Do nothing.
            } catch (SecurityException exception) {
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.log(
                        Level.FINEST,
                        "A security error occurred while trying to get the constructor.",
                        exception);
                }
                // Do nothing.
            } catch (IllegalAccessException exception) {
                // Do nothing.
            } catch (IllegalArgumentException exception) {
                // Do nothing.
            } catch (InstantiationException exception) {
                // Do nothing.
            } catch (InvocationTargetException exception) {
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.log(
                        Level.FINEST,
                        "An exception was thrown while trying to create a new instance.",
                        exception);
                }
                // Do nothing.
            } catch (ExceptionInInitializerError error) {
                // Do nothing.
            }
        }
        return new LocalPushGroupManager(servletContext);
    }
}
