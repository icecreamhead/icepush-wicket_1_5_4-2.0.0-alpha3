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

function identifier(element) {
    return element.id;
}

function tag(element) {
    return toLowerCase(element.tagName);
}

function property(element, name) {
    return element[name];
}

function parents(element) {
    return Stream(function(cellConstructor) {
        function parentStream(e) {
            if (e == null || e == document) return null;
            return function() {
                return cellConstructor(e, parentStream(e.parentNode));
            };
        }

        return parentStream(element.parentNode);
    });
}

function enclosingForm(element) {
    return element.form || detect(parents(element), function(e) {
        return tag(e) == 'form';
    }, function() {
        throw 'cannot find enclosing form';
    });
}

function enclosingBridge(element) {
    return property(detect(parents(element), function(e) {
        return property(e, 'bridge') != null;
    }, function() {
        throw 'cannot find enclosing bridge';
    }), 'bridge');
}

function serializeElementOn(element, query) {
    var tagName = tag(element);
    switch (tagName) {
        case 'a':
            var name = element.name || element.id;
            if (name) addNameValue(query, name, name);
            break;
        case 'input':
            switch (element.type) {
                case 'image':
                case 'submit':
                case 'button': addNameValue(query, element.name, element.value); break;
            }
            break;
        case 'button':
            if (element.type == 'submit') addNameValue(query, element.name, element.value);
            break;
        default:
        //do not serialize other elements
    }
}

function $elementWithID(id) {
    return document.getElementById(id);
}