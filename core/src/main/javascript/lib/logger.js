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

var debug = operator();
var info = operator();
var warn = operator();
var error = operator();
var childLogger = operator();
var log = operator();
var threshold = operator();
var enable = operator();
var disable = operator();
var toggle = operator();

function Logger(category, handler) {
    return object(function(method) {
        each([debug, info, warn, error], function(priorityOperator) {
            method(priorityOperator, function(self, message, exception) {
                log(handler, priorityOperator, category, message, exception);
            });
        });

        method(childLogger, function(self, categoryName, newHandler) {
            return Logger(append(copy(category), categoryName), newHandler || handler);
        });

        method(asString, function(self) {
            return 'Logger[' + join(category, '.') + ']';
        });
    });
}

function FirebugLogHandler(priority) {
    function formatOutput(category, message) {
        return join(['[', join(category, '.'), '] ', message], '');
    }

    function debugPrimitive(self, category, message, exception) {
        exception ? console.debug(formatOutput(category, message), exception) : console.debug(formatOutput(category, message));
    }

    function infoPrimitive(self, category, message, exception) {
        exception ? console.info(formatOutput(category, message), exception) : console.info(formatOutput(category, message));
    }

    function warnPrimitive(self, category, message, exception) {
        exception ? console.warn(formatOutput(category, message), exception) : console.warn(formatOutput(category, message));
    }

    function errorPrimitive(self, category, message, exception) {
        exception ? console.error(formatOutput(category, message), exception) : console.error(formatOutput(category, message));
    }

    var handlers = [
        Cell(debug, object(function(method) {
            method(debug, debugPrimitive);
            method(info, infoPrimitive);
            method(warn, warnPrimitive);
            method(error, errorPrimitive);
        })),
        Cell(info, object(function(method) {
            method(debug, noop);
            method(info, infoPrimitive);
            method(warn, warnPrimitive);
            method(error, errorPrimitive);
        })),
        Cell(warn, object(function(method) {
            method(debug, noop);
            method(info, noop);
            method(warn, warnPrimitive);
            method(error, errorPrimitive);
        })),
        Cell(error, object(function(method) {
            method(debug, noop);
            method(info, noop);
            method(warn, noop);
            method(error, errorPrimitive);
        }))
    ];
    var handler;

    function selectHandler(p) {
        handler = value(detect(handlers, function(cell) {
            return key(cell) == p;
        }));
    }

    selectHandler(priority || debug);

    return object(function (method) {
        method(threshold, function(self, priority) {
            selectHandler(priority);
        });

        method(log, function(self, operation, category, message, exception) {
            operation(handler, category, message, exception);
        });
    });
}

function WindowLogHandler(thresholdPriority, name) {
    var lineOptions = [25, 50, 100, 200, 400];
    var numberOfLines = lineOptions[3];
    var categoryMatcher = /.*/;
    var closeOnExit = true;
    var logContainer;
    var logEntry = noop;

    function trimLines() {
        var nodes = logContainer.childNodes;
        var trim = size(nodes) - numberOfLines;
        if (trim > 0) {
            each(copy(nodes), function(node, index) {
                if (index < trim) logContainer.removeChild(node);
            });
        }
    }

    function trimAllLines() {
        each(copy(logContainer.childNodes), function(node) {
            logContainer.removeChild(node);
        });
    }

    function toggle() {
        var disabled = logEntry == noop;
        logEntry = disabled ? displayEntry : noop;
        return !disabled;
    }

    function displayEntry(priorityName, colorName, category, message, exception) {
        var categoryName = join(category, '.');

        if (categoryMatcher.test(categoryName)) {
            var elementDocument = logContainer.ownerDocument;
            var timestamp = new Date();
            var completeMessage = join(['[', categoryName, '] : ', message, (exception ? join(['\n', exception.name, ' <', exception.message, '>'], '') : '')], '');
            each(split(completeMessage, '\n'), function(line) {
                if (/(\w+)/.test(line)) {
                    var eventNode = elementDocument.createElement('div');
                    eventNode.style.padding = '3px';
                    eventNode.style.color = colorName;
                    eventNode.setAttribute("title", timestamp + ' | ' + priorityName)
                    logContainer.appendChild(eventNode).appendChild(elementDocument.createTextNode(line));
                }
            });
            logContainer.scrollTop = logContainer.scrollHeight;
        }
        trimLines();
    }

    function showWindow() {
        var logWindow = window.open('', '_blank', 'scrollbars=1,width=800,height=680');
        try {
            var windowDocument = logWindow.document;
            var documentBody = windowDocument.body;
            //erase previous content!
            each(copy(documentBody.childNodes), function(e) {
                windowDocument.body.removeChild(e);
            });
            //create 'Close on exit' checkbox
            documentBody.appendChild(windowDocument.createTextNode(' Close on exit '));
            var closeOnExitCheckbox = windowDocument.createElement('input');
            closeOnExitCheckbox.style.margin = '2px';
            closeOnExitCheckbox.setAttribute('type', 'checkbox');
            closeOnExitCheckbox.defaultChecked = true;
            closeOnExitCheckbox.checked = true;
            closeOnExitCheckbox.onclick = function() {
                closeOnExit = closeOnExitCheckbox.checked;
            };
            documentBody.appendChild(closeOnExitCheckbox);
            //create 'Lines' drop down
            documentBody.appendChild(windowDocument.createTextNode(' Lines '));
            var lineCountDropDown = windowDocument.createElement('select');
            lineCountDropDown.style.margin = '2px';
            each(lineOptions, function(count, index) {
                var option = lineCountDropDown.appendChild(windowDocument.createElement('option'));
                if (numberOfLines == count) lineCountDropDown.selectedIndex = index;
                option.appendChild(windowDocument.createTextNode(asString(count)));
            });
            documentBody.appendChild(lineCountDropDown);
            //create 'Category' input text
            documentBody.appendChild(windowDocument.createTextNode(' Category '));
            var categoryInputText = windowDocument.createElement('input');
            categoryInputText.style.margin = '2px';
            categoryInputText.setAttribute('type', 'text');
            categoryInputText.setAttribute('value', categoryMatcher.source);
            categoryInputText.onchange = function() {
                categoryMatcher = new RegExp(categoryInputText.value);
            };
            documentBody.appendChild(categoryInputText);
            //create 'Level' drop down
            documentBody.appendChild(windowDocument.createTextNode(' Level '));
            var levelDropDown = windowDocument.createElement('select');
            levelDropDown.style.margin = '2px';
            var levels = [Cell('debug', debug), Cell('info', info), Cell('warn', warn), Cell('error', error)];
            each(levels, function(priority, index) {
                var option = levelDropDown.appendChild(windowDocument.createElement('option'));
                if (thresholdPriority == value(priority)) levelDropDown.selectedIndex = index;
                option.appendChild(windowDocument.createTextNode(key(priority)));
            });
            levelDropDown.onchange = function(event) {
                thresholdPriority = value(levels[levelDropDown.selectedIndex]);
            };
            documentBody.appendChild(levelDropDown);
            //create 'Start/Stop' button
            var startStopButton = windowDocument.createElement('input');
            startStopButton.style.margin = '2px';
            startStopButton.setAttribute('type', 'button');
            startStopButton.setAttribute('value', 'Stop');
            startStopButton.onclick = function() {
                startStopButton.setAttribute('value', toggle() ? 'Stop' : 'Start');
            };
            documentBody.appendChild(startStopButton);
            //create 'Clear' button
            var clearButton = windowDocument.createElement('input');
            clearButton.style.margin = '2px';
            clearButton.setAttribute('type', 'button');
            clearButton.setAttribute('value', 'Clear');
            documentBody.appendChild(clearButton);

            logContainer = documentBody.appendChild(windowDocument.createElement('pre'));
            logContainer.id = 'log-window';
            var logContainerStyle = logContainer.style;
            logContainerStyle.width = '100%';
            logContainerStyle.minHeight = '0';
            logContainerStyle.maxHeight = '550px';
            logContainerStyle.borderWidth = '1px';
            logContainerStyle.borderStyle = 'solid';
            logContainerStyle.borderColor = '#999';
            logContainerStyle.backgroundColor = '#ddd';
            logContainerStyle.overflow = 'scroll';

            lineCountDropDown.onchange = function(event) {
                numberOfLines = lineOptions[lineCountDropDown.selectedIndex];
                trimLines();
            };
            clearButton.onclick = trimAllLines;

            onUnload(window, function() {
                if (closeOnExit) {
                    logEntry = noop;
                    logWindow.close();
                }
            });
        } catch (e) {
            logWindow.close();
        }
    }

    onKeyPress(document, function(evt) {
        var event = $event(evt, document.documentElement);
        var key = keyCode(event);
        if ((key == 20 || key == 84) && isCtrlPressed(event) && isShiftPressed(event)) {
            showWindow();
            logEntry = displayEntry;
        }
    });

    return object(function(method) {
        method(threshold, function(self, priority) {
            thresholdPriority = priority;
        });

        method(log, function(self, operation, category, message, exception) {
            operation(self, category, message, exception);
        });

        method(debug, function(self, category, message, exception) {
            logEntry('debug', '#333', category, message, exception);
        });

        method(info, function(self, category, message, exception) {
            logEntry('info', 'green', category, message, exception);
        });

        method(warn, function(self, category, message, exception) {
            logEntry('warn', 'orange', category, message, exception);
        });

        method(error, function(self, category, message, exception) {
            logEntry('error', 'red', category, message, exception);
        });
    });
}