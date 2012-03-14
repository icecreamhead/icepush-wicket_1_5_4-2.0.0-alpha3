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

package org.icepush.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

public class AnnotationScanner {
    private static final Logger LOGGER = Logger.getLogger(AnnotationScanner.class.getName());

    private static final char DOT = '.';
    private static final char SLASH = '/';

    private static final String CLASS_EXTENSION = ".class";
    private static final String JAR_EXTENSION = ".jar";
    private static final String JAR_SCHEME = "jar:";
    private static final String JAR_SCHEME_POSTFIX = "!/";
    private static final String META_INF_PREFIX = "META-INF/";
    private static final String WEB_INF_LIB_PREFIX = "/WEB-INF/lib/";

    private final ByteCodeAnnotationFilter filter = new ByteCodeAnnotationFilter();

    private final Set<String> annotationSet;
    private final ServletContext servletContext;

    public AnnotationScanner(final Set<String> annotationSet, final ServletContext servletContext)
    throws IllegalArgumentException {
        if (annotationSet == null) {
            throw new IllegalArgumentException("Illegal value of 'annotationSet' parameter: null");
        }
        if (annotationSet.size() == 0) {
            throw new IllegalArgumentException("Illegal value of 'annotationSet' parameter: empty");
        }
        if (servletContext == null) {
            throw new IllegalArgumentException("Illegal value of 'servletContext' parameter: null");
        }
        this.annotationSet = Collections.unmodifiableSet(annotationSet);
        this.servletContext = servletContext;
    }

    public Class[] getClasses()
    throws IOException {
        List<Class> _classList = new ArrayList<Class>();
        List<JarFile> _webArchiveList = getWebArchives();
        for (JarFile _jarFile : _webArchiveList) {
            _classList.addAll(getClasses(_jarFile));
        }
        return _classList.toArray(new Class[_classList.size()]);
    }

    private List<Class> getClasses(final JarFile jarFile) {
        List<Class> _classList = new ArrayList<Class>();
        ClassLoader _classLoader = getContextClassLoader();
        if (_classLoader == null) {
            _classLoader = this.getClass().getClassLoader();
        }
        Enumeration<JarEntry> _jarEntries = jarFile.entries();
        while (_jarEntries.hasMoreElements()) {
            JarEntry _jarEntry = _jarEntries.nextElement();
            if (_jarEntry.isDirectory()) {
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.log(Level.FINEST, "Skip directory: [" + _jarEntry.getName() + "]");
                }
                continue;
            }
            String _name = _jarEntry.getName();
            if (_name.startsWith(META_INF_PREFIX)) {
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.log(Level.FINEST, "Skip META-INF: [" + _name + "]");
                }
                continue;
            }
            if (!_name.endsWith(CLASS_EXTENSION)) {
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.log(Level.FINEST, "Skip non-class: [" + _name + "]");
                }
                continue;
            }
            DataInputStream _in = null;
            boolean _containsAnnotation = false;
            try {
                // throws IOException
                _in = new DataInputStream(jarFile.getInputStream(_jarEntry));
                _containsAnnotation = filter.containsAnnotation(_in, annotationSet);
            } catch (IOException exception) {
                _containsAnnotation = true;
            } finally {
                if (_in != null) {
                    try {
                        // throws IOException
                        _in.close();
                    } catch (IOException exception) {
                        // Do nothing.
                    }
                }
            }
            if (_containsAnnotation) {
                Class _class = null;
                try {
                    _class =
                        _classLoader.loadClass(
                            _name.substring(0, _name.length() - CLASS_EXTENSION.length()).replace(SLASH, DOT));
                } catch (ClassNotFoundException exception) {
                    // Do nothing.
                }
                if (_class != null) {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE, "Add class: [" + _class.getName() + "]");
                    }
                    _classList.add(_class);
                }
            }
        }
        return _classList;
    }

    private static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() != null) {
            try {
                return
                    // throws PrivilegedActionException
                    //            if the specified action's run method threw a checked exception
                    AccessController.doPrivileged(
                        new PrivilegedExceptionAction<ClassLoader>() {
                            public ClassLoader run()
                            throws Exception {
                                return Thread.currentThread().getContextClassLoader();
                            }
                        });
            } catch (PrivilegedActionException exception) {
                // todo: I don't exactly know what to do here.
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, "An error occurred while getting the context class loader.");
                }
                throw new RuntimeException(exception);
            }
        } else {
            return Thread.currentThread().getContextClassLoader();
        }
    }

    private List<JarFile> getWebArchives() {
        List<JarFile> _webArchiveList = new ArrayList<JarFile>();
        Set<String> _resourcePaths = servletContext.getResourcePaths(WEB_INF_LIB_PREFIX);
        if (_resourcePaths != null) {
            for (String _resourcePath : _resourcePaths) {
                if (!_resourcePath.endsWith(JAR_EXTENSION)) {
                    if (LOGGER.isLoggable(Level.FINEST)) {
                        LOGGER.log(Level.FINEST, "Skip non-jar: [" + _resourcePath + "]");
                    }
                    continue;
                }
                try {
                    // throws MalformedURLException, IOException.
                    JarFile _jarFile =
                        ((JarURLConnection)
                            new URL(
                                JAR_SCHEME + servletContext.getResource(_resourcePath).toString() + JAR_SCHEME_POSTFIX).
                                    openConnection()
                        ).getJarFile();
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE, "Add jar: [" + _resourcePath + "]");
                    }
                    _webArchiveList.add(_jarFile);
                } catch (IOException exception) {
                    // todo: Is this the right thing to do here?
                    if (LOGGER.isLoggable(Level.FINEST)) {
                        LOGGER.log(Level.FINEST, "Skip due to I/O error: [" + _resourcePath + "]");
                    }
                }
            }
        }
        return _webArchiveList;
    }
}
