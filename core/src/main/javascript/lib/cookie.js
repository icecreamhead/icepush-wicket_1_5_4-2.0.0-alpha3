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

function lookupCookieValue(name) {
    var tupleString = detect(split(asString(document.cookie), '; '), function(tuple) {
        return startsWith(tuple, name);
    }, function() {
        throw 'Cannot find value for cookie: ' + name;
    });

    return decodeURIComponent(contains(tupleString, '=') ? split(tupleString, '=')[1] : '');
}

function lookupCookie(name, failThunk) {
    try {
        return Cookie(name, lookupCookieValue(name));
    } catch (e) {
        if (failThunk) {
            return failThunk();
        } else {
            throw e;
        }
    }
}

function existsCookie(name) {
    var exists = true;
    lookupCookie(name, function() {
        exists = false;
    });
    return exists;
}

var update = operator();
var remove = operator();
function Cookie(name, val, path) {
    val = val || '';
    path = path || '/';
    document.cookie = name + '=' + val + '; path=' + path;

    return object(function(method) {
        method(value, function(self) {
            return lookupCookieValue(name);
        });

        method(update, function(self, val) {
            document.cookie = name + '=' + encodeURIComponent(val) + '; path=' + path;
            return self;
        });

        method(remove, function(self) {
            var date = new Date();
            date.setTime(date.getTime() - 24 * 60 * 60 * 1000);
            document.cookie = name + '=; expires=' + date.toGMTString() + '; path=' + path;
        });

        method(asString, function(self) {
            return 'Cookie[' + name + ', ' + value(self) + ', ' + path + ']';
        });
    });
}