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

var indexOf = function(s, substring) {
    var index = s.indexOf(substring);
    if (index >= 0) {
        return index;
    } else {
        throw '"' + s + '" does not contain "' + substring + '"';
    }
};

var lastIndexOf = function(s, substring) {
    var index = s.lastIndexOf(substring);
    if (index >= 0) {
        return index;
    } else {
        throw 'string "' + s + '" does not contain "' + substring + '"';
    }
};

var startsWith = function(s, pattern) {
    return s.indexOf(pattern) == 0;
};

var endsWith = function(s, pattern) {
    return s.lastIndexOf(pattern) == s.length - pattern.length;
};

var containsSubstring = function(s, substring) {
    return s.indexOf(substring) >= 0;
};

var blank = function(s) {
    return /^\s*$/.test(s);
};

var split = function(s, separator) {
    return s.length == 0 ? [] : s.split(separator);
};

var replace = function(s, regex, replace) {
    return s.replace(regex, replace);
};

var toLowerCase = function(s) {
    return s.toLowerCase();
};

var toUpperCase = function(s) {
    return s.toUpperCase();
};

var substring = function(s, from, to) {
    return s.substring(from, to);
};

var asNumber = Number;

var asBoolean = function(s) {
    return 'true' == s || 'any' == s;
};

var asRegexp = function(s) {
    return new RegExp(s);
};



