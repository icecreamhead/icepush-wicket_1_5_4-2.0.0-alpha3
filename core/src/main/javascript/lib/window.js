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

function registerListener(eventType, obj, listener) {
    var previousListener = obj[eventType];
    if (previousListener) {
        obj[eventType] = function() {
            apply(previousListener, arguments);
            apply(listener, arguments);
        };
    } else {
        obj[eventType] = listener;
    }
}

var onLoad = curry(registerListener, 'onload');
var onUnload = curry(registerListener, 'onunload');
var onBeforeUnload = curry(registerListener, 'onbeforeunload');
var onResize = curry(registerListener, 'onresize');
var onKeyPress = curry(registerListener, 'onkeypress');
var onKeyUp = curry(registerListener, 'onkeyup');

window.width = function() {
    return window.innerWidth ? window.innerWidth : (document.documentElement && document.documentElement.clientWidth) ? document.documentElement.clientWidth : document.body.clientWidth;
};

window.height = function() {
    return window.innerHeight ? window.innerHeight : (document.documentElement && document.documentElement.clientHeight) ? document.documentElement.clientHeight : document.body.clientHeight;
};
