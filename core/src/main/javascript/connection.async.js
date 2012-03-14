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

var send = operator();
var onSend = operator();
var onReceive = operator();
var onServerError = operator();
var whenDown = operator();
var whenTrouble = operator();
var shutdown = operator();
var resetConnection = operator();

function AsyncConnection(logger, windowID, receiveURI) {
    var logger = childLogger(logger, 'async-connection');
    var channel = Client(false);
    var onReceiveListeners = [];
    var onServerErrorListeners = [];
    var connectionDownListeners = [];
    var connectionTroubleListeners = [];

    var listener = object(function(method) {
        method(close, noop);
        method(abort, noop);
    });
    var listening = object(function(method) {
        method(remove, noop);
    });

    //clear connectionDownListeners to avoid bogus connection lost messages
    onBeforeUnload(window, function() {
        connectionDownListeners = [];
    });

    var serverErrorCallback = broadcaster(onServerErrorListeners);
    var receiveCallback = broadcaster(onReceiveListeners);
    var sendXWindowCookie = noop;
    var receiveXWindowCookie = function (response) {
        var xWindowCookie = getHeader(response, "X-Set-Window-Cookie");
        if (xWindowCookie) {
            sendXWindowCookie = function(request) {
                setHeader(request, "X-Window-Cookie", xWindowCookie);
            };
        }
    };

    //remove the blocking connection marker so that everytime a new
    //bridge instance is created the blocking connection will
    //be re-established
    //this strategy is mainly employed to fix the window.onunload issue
    //in Opera -- see http://jira.icefaces.org/browse/ICE-1872
    try {
        listening = lookupCookie('ice.connection.running');
        remove(listening);
    } catch (e) {
        //do nothing
    }

    //build up retry actions
    function timedRetryAbort(retryAction, abortAction, timeouts) {
        var index = 0;
        var errorActions = inject(timeouts, [abortAction], function(actions, interval) {
            return insert(actions, curry(runOnce, Delay(retryAction, interval)));
        });
        return function() {
            if (index < errorActions.length) {
                apply(errorActions[index], arguments);
                index++;
            }
        };
    }

    function registeredPushIds() {
        try {
            return split(lookupCookieValue('ice.pushids'), ' ');
        } catch (e) {
            return [];
        }
    }

    var lastSentPushIds = registeredPushIds();

    function connect() {
        try {
            debug(logger, "closing previous connection...");
            close(listener);

            lastSentPushIds = registeredPushIds();
            if (isEmpty(lastSentPushIds)) {
                //mark blocking connection as not started, with current window as first candidate to re-initiate the connection 
                offerCandidature();
            } else {
                debug(logger, "connect...");
                listener = postAsynchronously(channel, receiveURI, function(q) {
                    each(lastSentPushIds, curry(addNameValue, q, 'ice.pushid'));
                }, function(request) {
                    FormPost(request);
                    sendXWindowCookie(request);
                    setHeader(request, "ice.push.window", namespace.windowID);
                }, $witch(function (condition) {
                    condition(OK, function(response) {
                        var reconnect = getHeader(response, 'X-Connection') != 'close';
                        var nonEmptyResponse = notEmpty(contentAsText(response));

                        if (reconnect) {
                            if (not(nonEmptyResponse)) warn(logger, 'empty response received');
                        } else {
                            info(logger, 'blocking connection stopped at server\'s request...');
                        }
                        if (nonEmptyResponse) receiveCallback(response);
                        receiveXWindowCookie(response);
                        if (reconnect) connect();
                    });
                    condition(ServerInternalError, retryOnServerError);
                }));
            }
        } catch (e) {
            error(logger, 'failed to re-initiate blocking connection', e);
        }
    }

    var configuration = namespace.push.configuration;
    //build callbacks only after 'connection' function was defined
    var retryOnServerError = timedRetryAbort(connect, serverErrorCallback, configuration.serverErrorRetryTimeouts || [1000, 2000, 4000]);

    //todo: implement client based timout alert for server side hearbeat to help detect when server does not respond anymore
    var heartbeatTimeout = configuration.heartbeat && configuration.heartbeat.timeout ? configuration.heartbeat.timeout : 30000;

    function initializeConnection() {
        connect();
    }

    //monitor if the blocking connection needs to be started
    var pollingPeriod = 1000;
    var contextPath = namespace.push.configuration.contextPath;

    var leaseCookie = lookupCookie('ice.connection.lease', function() {
        return Cookie('ice.connection.lease', asString((new Date).getTime()));
    });
    var connectionCookie = listening = lookupCookie('ice.connection.running', function() {
        return Cookie('ice.connection.running', '');
    });
    var contextPathCookie = lookupCookie('ice.connection.contextpath', function() {
        return Cookie('ice.connection.contextpath', contextPath);
    });

    function updateLease() {
        update(leaseCookie, (new Date).getTime() + pollingPeriod * 2);
    }

    function isLeaseExpired() {
        return asNumber(value(leaseCookie)) < (new Date).getTime();
    }

    function shouldEstablishBlockingConnection() {
        return !existsCookie('ice.connection.running') || isEmpty(lookupCookieValue('ice.connection.running'));
    }

    function offerCandidature() {
        update(connectionCookie, windowID);
    }

    function isWinningCandidate() {
        return startsWith(value(connectionCookie), windowID);
    }

    function markAsOwned() {
        update(connectionCookie, windowID + ':acquired');
        update(contextPathCookie, contextPath);
    }

    function isOwner() {
        return value(connectionCookie) == (windowID + ':acquired');
    }

    function hasOwner() {
        return endsWith(value(connectionCookie), ':acquired');
    }

    function nonMatchingContextPath() {
        return value(contextPathCookie) != contextPath;
    }

    //force candidancy so that last opened window belonging to a different servlet context will own the blocking connection
    if (nonMatchingContextPath()) {
        offerCandidature();
        info(logger, 'Blocking connection cannot be shared among multiple web-contexts.\nInitiating blocking connection for "' + contextPath + '"  web-context...');
    }
    var blockingConnectionMonitor = run(Delay(function() {
        if (shouldEstablishBlockingConnection()) {
            offerCandidature();
            info(logger, 'blocking connection not initialized...candidate for its creation');
        } else {
            if (isWinningCandidate()) {
                if (!hasOwner()) {
                    markAsOwned();
                    //start blocking connection since no other window has started it
                    //but only when at least one pushId is registered
                    if (notEmpty(registeredPushIds())) {
                        initializeConnection();
                    }
                }
                updateLease();
            }
            if (hasOwner() && isLeaseExpired()) {
                offerCandidature();
                info(logger, 'blocking connection lease expired...candidate for its creation');
            }
        }

        if (isOwner()) {
            var ids = registeredPushIds();
            if ((size(ids) != size(lastSentPushIds)) || notEmpty(complement(ids, lastSentPushIds))) {
                //reconnect to send the current list of pushIDs
                //abort the previous blocking connection in case is still alive
                abort(listener);
                connect();
            }
        } else {
            //ensure that only one blocking connection exists
            abort(listener);
        }
    }, pollingPeriod));

    info(logger, 'asynchronous mode');

    return object(function(method) {
        method(onReceive, function(self, callback) {
            append(onReceiveListeners, callback);
        });

        method(onServerError, function(self, callback) {
            append(onServerErrorListeners, callback);
        });

        method(whenDown, function(self, callback) {
            append(connectionDownListeners, callback);
        });

        method(whenTrouble, function(self, callback) {
            append(connectionTroubleListeners, callback);
        });

        method(shutdown, function(self) {
            try {
                //shutdown once
                method(shutdown, noop);
                connect = noop;
            } catch (e) {
                error(logger, 'error during shutdown', e);
                //ignore, we really need to shutdown
            } finally {
                onReceiveListeners = connectionDownListeners = onServerErrorListeners = [];
                abort(listener);
                stop(blockingConnectionMonitor);
                remove(listening);
            }
        });
    });
}
