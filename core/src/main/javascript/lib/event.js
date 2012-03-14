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

var cancel = operator();
var cancelBubbling = operator();
var cancelDefaultAction = operator();
var isKeyEvent = operator();
var isMouseEvent = operator();
var capturedBy = operator();
var triggeredBy = operator();
var serializeEventOn = operator();
var serializePositionOn = operator();
var type = operator();

var yes = any;
var no = none;

function Event(event, capturingElement) {
    return object(function(method) {
        method(cancel, function(self) {
            cancelBubbling(self);
            cancelDefaultAction(self);
        });

        method(isKeyEvent, no);

        method(isMouseEvent, no);

        method(type, function(self) {
            return event.type;
        });

        method(triggeredBy, function(self) {
            return capturingElement;
        });

        method(capturedBy, function(self) {
            return capturingElement;
        });

        method(serializeEventOn, function(self, query) {
            serializeElementOn(capturingElement, query);
            addNameValue(query, 'ice.event.target', identifier(triggeredBy(self)));
            addNameValue(query, 'ice.event.captured', identifier(capturedBy(self)));
            addNameValue(query, 'ice.event.type', 'on' + type(self));
        });

        method(serializeOn, curry(serializeEventOn));
    });
}

function IEEvent(event, capturingElement) {
    return objectWithAncestors(function(method) {
        method(triggeredBy, function(self) {
            return event.srcElement ? event.srcElement : null;
        });

        method(cancelBubbling, function(self) {
            event.cancelBubble = true;
        });

        method(cancelDefaultAction, function(self) {
            event.returnValue = false;
        });

        method(asString, function(self) {
            return 'IEEvent[' + type(self) + ']';
        });
    }, Event(event, capturingElement));
}

function NetscapeEvent(event, capturingElement) {
    return objectWithAncestors(function(method) {
        method(triggeredBy, function(self) {
            return event.target ? event.target : null;
        });

        method(cancelBubbling, function(self) {
            event.stopPropagation();
        });

        method(cancelDefaultAction, function(self) {
            event.preventDefault();
        });

        method(asString, function(self) {
            return 'NetscapeEvent[' + type(self) + ']';
        });
    }, Event(event, capturingElement));
}

var isAltPressed = operator();
var isCtrlPressed = operator();
var isShiftPressed = operator();
var isMetaPressed = operator();
var serializeKeyOrMouseEventOn = operator();
function KeyOrMouseEvent(event) {
    return object(function(method) {
        method(isAltPressed, function(self) {
            return event.altKey;
        });

        method(isCtrlPressed, function(self) {
            return event.ctrlKey;
        });

        method(isShiftPressed, function(self) {
            return event.shiftKey;
        });

        method(isMetaPressed, function(self) {
            return event.metaKey;
        });

        method(serializeKeyOrMouseEventOn, function(self, query) {
            addNameValue(query, 'ice.event.alt', isAltPressed(self));
            addNameValue(query, 'ice.event.ctrl', isCtrlPressed(self));
            addNameValue(query, 'ice.event.shift', isShiftPressed(self));
            addNameValue(query, 'ice.event.meta', isMetaPressed(self));
        });
    });
}

var isLeftButton = operator();
var isRightButton = operator();
var positionX = operator();
var positionY = operator();
var serializeMouseEventOn = operator();
function MouseEvent(event) {
    return objectWithAncestors(function(method) {
        method(isMouseEvent, yes);

        method(serializeMouseEventOn, function(self, query) {
            serializeKeyOrMouseEventOn(self, query);
            addNameValue(query, 'ice.event.x', positionX(self));
            addNameValue(query, 'ice.event.y', positionY(self));
            addNameValue(query, 'ice.event.left', isLeftButton(self));
            addNameValue(query, 'ice.event.right', isRightButton(self));
        });

    }, KeyOrMouseEvent(event));
}

function MouseEventTrait(method) {
    method(serializeOn, function(self, query) {
        serializeEventOn(self, query);
        serializeMouseEventOn(self, query);
    });
}

function IEMouseEvent(event, capturingElement) {
    return objectWithAncestors(function(method) {
        MouseEventTrait(method);

        method(positionX, function(self) {
            return event.clientX + (document.documentElement.scrollLeft || document.body.scrollLeft);
        });

        method(positionY, function(self) {
            return event.clientY + (document.documentElement.scrollTop || document.body.scrollTop);
        });

        method(isLeftButton, function(self) {
            return event.button == 1;
        });

        method(isRightButton, function(self) {
            return event.button == 2;
        });

        method(asString, function(self) {
            return 'IEMouseEvent[' + type(self) + ']';
        });
    }, IEEvent(event, capturingElement), MouseEvent(event));
}

function NetscapeMouseEvent(event, capturingElement) {
    return objectWithAncestors(function(method) {
        MouseEventTrait(method);

        method(positionX, function(self) {
            return event.pageX;
        });

        method(positionY, function(self) {
            return event.pageY;
        });

        method(isLeftButton, function(self) {
            return event.which == 1;
        });

        method(isRightButton, function(self) {
            return event.which == 2;
        });

        method(asString, function(self) {
            return 'NetscapeMouseEvent[' + type(self) + ']';
        });

    }, NetscapeEvent(event, capturingElement), MouseEvent(event));
}

var keyCharacter = operator();
var keyCode = operator();
var serializeKeyEventOn = operator();
function KeyEvent(event) {
    return objectWithAncestors(function(method) {
        method(isKeyEvent, yes);

        method(keyCharacter, function(self) {
            return String.fromCharCode(keyCode(self));
        });

        method(serializeKeyEventOn, function(self, query) {
            serializeKeyOrMouseEventOn(self, query);
            addNameValue(query, 'ice.event.keycode', keyCode(self));
        });
    }, KeyOrMouseEvent(event));
}

function KeyEventTrait(method) {
    method(serializeOn, function(self, query) {
        serializeEventOn(self, query);
        serializeKeyEventOn(self, query);
    });
}

function IEKeyEvent(event, capturingElement) {
    return objectWithAncestors(function(method) {
        KeyEventTrait(method);

        method(keyCode, function(self) {
            return event.keyCode;
        });

        method(asString, function(self) {
            return 'IEKeyEvent[' + type(self) + ']';
        });
    }, IEEvent(event, capturingElement), KeyEvent(event));
}

function NetscapeKeyEvent(event, capturingElement) {
    return objectWithAncestors(function(method) {
        KeyEventTrait(method);

        method(keyCode, function(self) {
            return event.which == 0 ? event.keyCode : event.which;
        });

        method(asString, function(self) {
            return 'NetscapeKeyEvent[' + type(self) + ']';
        });
    }, NetscapeEvent(event, capturingElement), KeyEvent(event));
}

function isEnterKey(event) {
    return isKeyEvent(event) && keyCode(event) == 13;
}

function isEscKey(event) {
    return isKeyEvent(event) && keyCode(event) == 27;
}

function UnknownEvent(capturingElement) {
    return objectWithAncestors(function(method) {
        method(cancelBubbling, noop);

        method(cancelDefaultAction, noop);

        method(type, function(self) {
            return 'unknown';
        });

        method(asString, function(self) {
            return 'UnkownEvent[]';
        });

    }, Event(null, capturingElement));
}

var MouseListenerNames = [ 'onclick', 'ondblclick', 'onmousedown', 'onmousemove', 'onmouseout', 'onmouseover', 'onmouseup' ];
var KeyListenerNames = [ 'onkeydown', 'onkeypress', 'onkeyup', 'onhelp' ];

function $event(e, element) {
    var capturedEvent = window.event || e;
    if (capturedEvent && capturedEvent.type) {
        var eventType = 'on' + capturedEvent.type;
        if (contains(KeyListenerNames, eventType)) {
            return window.event ? IEKeyEvent(event, element) : NetscapeKeyEvent(e, element);
        } else if (contains(MouseListenerNames, eventType)) {
            return window.event ? IEMouseEvent(event, element) : NetscapeMouseEvent(e, element);
        } else {
            return window.event ? IEEvent(event, element) : NetscapeEvent(e, element);
        }
    } else {
        return UnknownEvent(element);
    }
}