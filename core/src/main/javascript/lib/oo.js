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

function isArray(a) {
    return a && !!a.push;
}

function isString(s) {
    return typeof s == 'string';
}

function isNumber(s) {
    return typeof s == 'number';
}

function isIndexed(s) {
    return !!s.length;
}

function isObject(o) {
    return o.instanceTag == o;
}

var uid = (function() {
    var id = 0;
    return function() {
        return id++;
    };
})();

function operationNotSupported() {
    throw 'operation not supported';
}

function operator(defaultOperation) {
    return function() {
        var args = arguments;
        var instance = arguments[0];
        if (instance.instanceTag && instance.instanceTag == instance) {
            var method = instance(arguments.callee);
            if (method) {
                return method.apply(method, args);
            } else {
                operationNotSupported();
            }
        } else {
            return defaultOperation ? defaultOperation.apply(defaultOperation, args) : operationNotSupported();
        }
    };
}

var asString = operator(String);
var asNumber = operator(Number);
var hash = operator(function(o) {
    var s;
    if (isString(o)) {
        s = o;
    } else if (isNumber(o)) {
        return Math.abs(Math.round(o));
    } else {
        s = o.toString();
    }

    var h = 0;
    for (var i = 0, l = s.length; i < l; i++) {
        var c = parseInt(s[i], 36);
        if (!isNaN(c)) {
            h = c + (h << 6) + (h << 16) - h;
        }
    }
    return Math.abs(h);
});
var equal = operator(function(a, b) {
    return a == b;
});

function object(definition) {
    var operators = [];
    var methods = [];
    var unknown = null;
    var id = uid();
    operators.push(hash);
    methods.push(function(self) {
        return id;
    });
    operators.push(equal);
    methods.push(function(self, other) {
        return self == other;
    });
    operators.push(asString);
    methods.push(function(self) {
        return 'Object:' + id.toString(16);
    });
    definition(function(operator, method) {
        //replace method in case there was a previous one registered for the same operator
        var size = operators.length;
        for (var i = 0; i < size; i++) {
            if (operators[i] == operator) {
                methods[i] = method;
                return;
            }
        }
        operators.push(operator);
        methods.push(method);
    }, function(method) {
        unknown = method;
    });
    //create the message dispatcher of the instance
    function self(operator) {
        var size = operators.length;
        for (var i = 0; i < size; i++) {
            if (operators[i] == operator) {
                return methods[i];
            }
        }

        return unknown;
    }

    //tag function with itself to differentiate from normal functions that don't do message dispatching
    return self.instanceTag = self;
}

function objectWithAncestors() {
    var definition = arguments[0];
    var args = arguments;
    var o = object(definition);

    function self(operator) {
        var method = o(operator);
        if (method) {
            return method;
        } else {
            var size = args.length;
            for (var i = 1; i < size; i++) {
                var ancestor = args[i];
                var overriddenMethod = ancestor(operator);
                if (overriddenMethod) {
                    return overriddenMethod;
                }
            }

            return null;
        }
    }

    //tag function with itself to differentiate from normal functions that don't do message dispatching
    return self.instanceTag = self;
}