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

function apply(fun, arguments) {
    return fun.apply(fun, arguments);
}

function withArguments() {
    var args = arguments;
    return function(fun) {
        apply(fun, args);
    };
}

function let(definition) {
    return function() {
        return apply(definition, arguments);
    };
}

function curry() {
    var args = arguments;
    return function() {
        var curriedArguments = [];
        var fun = args[0];
        for (var i = 1; i < args.length; i++) curriedArguments.push(args[i]);
        for (var j = 0; j < arguments.length; j++) curriedArguments.push(arguments[j]);
        return apply(fun, curriedArguments);
    };
}

function $witch(tests, defaultRun) {
    return function(val) {
        var args = arguments;
        var conditions = [];
        var runs = [];
        tests(function(condition, run) {
            conditions.push(condition);
            runs.push(run);
        });
        var size = conditions.length;
        for (var i = 0; i < size; i++) {
            if (apply(conditions[i], args)) {
                return apply(runs[i], args);
            }
        }
        if (defaultRun) apply(defaultRun, args);
    };
}

function identity(arg) {
    return arg;
}

function negate(b) {
    return !b;
}

function greater(a, b) {
    return a > b;
}

function less(a, b) {
    return a < b;
}

function not(a) {
    return !a;
}

function multiply(a, b) {
    return a * b;
}

function plus(a, b) {
    return a + b;
}

function max(a, b) {
    return a > b ? a : b;
}

function increment(value, step) {
    return value + (step ? step : 1);
}

function decrement(value, step) {
    return value - (step ? step : 1);
}

function any() {
    return true;
}

function none() {
    return false;
}

function noop() {
}