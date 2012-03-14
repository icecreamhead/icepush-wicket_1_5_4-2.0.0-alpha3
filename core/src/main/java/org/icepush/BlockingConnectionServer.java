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

package org.icepush;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icepush.http.Request;
import org.icepush.http.Response;
import org.icepush.http.ResponseHandler;
import org.icepush.http.Server;
import org.icepush.http.standard.FixedXMLContentHandler;
import org.icepush.http.standard.ResponseHandlerServer;

public class BlockingConnectionServer extends TimerTask implements Server, Observer {
    private static final Logger log = Logger.getLogger(BlockingConnectionServer.class.getName());
    private static final ResponseHandler CloseResponse = new ResponseHandler() {
        public void respond(Response response) throws Exception {
            //let the bridge know that this blocking connection should not be re-initialized
            response.setHeader("X-Connection", "close");
            response.setHeader("Content-Length", 0);

            if (log.isLoggable(Level.FINEST)) {
                log.finest("Close current blocking connection.");
            }
        }
    };
    //Define here to avoid classloading problems after application exit
    private static final ResponseHandler NoopHandler = new FixedXMLContentHandler() {
        public void writeTo(Writer writer) throws IOException {
            writer.write("<noop/>");

            if (log.isLoggable(Level.FINEST)) {
                log.finest("Sending NoOp.");
            }
        }
    };
    private static final Server AfterShutdown = new ResponseHandlerServer(CloseResponse);

    private final BlockingQueue pendingRequest = new LinkedBlockingQueue(1);
    private final long timeoutInterval;
    private long responseTimeoutTime;
    private Server activeServer;
    private ConcurrentLinkedQueue notifiedPushIDs = new ConcurrentLinkedQueue();
    private List participatingPushIDs = Collections.emptyList();
    private PushGroupManager pushGroupManager;

    private String lastWindow = "";
    private String[] lastNotifications = new String[]{};

    public BlockingConnectionServer(final PushGroupManager pushGroupManager, final Timer monitorRunner, Configuration configuration) {
        this.timeoutInterval = configuration.getAttributeAsLong("blockingConnectionTimeout", 50000);
        this.pushGroupManager = pushGroupManager;
        //add monitor
        monitorRunner.scheduleAtFixedRate(this, 0, 1000);
        this.pushGroupManager.addObserver(this);

        //define blocking server
        activeServer = new Server() {
            public void service(final Request request) throws Exception {
                resetTimeout();
                respondIfPendingRequest(CloseResponse);

                //resend notifications if the window owning the blocking connection has changed
                String currentWindow = request.getHeader("ice.push.window");
                currentWindow = currentWindow == null ? "" : currentWindow;
                boolean resend = !lastWindow.equals(currentWindow);
                lastWindow = currentWindow;

                pendingRequest.put(request);
                try {
                    participatingPushIDs = Arrays.asList(request.getParameterAsStrings("ice.pushid"));
                    if (log.isLoggable(Level.FINEST)) {
                        log.finest("Participating pushIds: " + participatingPushIDs + ".");
                    }

                    if (resend) {
                        resendLastNotifications();
                    } else {
                        respondIfNotificationsAvailable();
                    }
                    pushGroupManager.notifyObservers(new ArrayList(participatingPushIDs));
                } catch (RuntimeException e) {
                    log.fine("Request does not contain pushIDs.");
                    respondIfPendingRequest(NoopHandler);
                }
            }

            public void shutdown() {
                //avoid creating new blocking connections after shutdown
                activeServer = AfterShutdown;
                respondIfPendingRequest(CloseResponse);
            }
        };
    }

    public void update(Observable observable, Object o) {
        sendNotifications((String[]) o);
    }

    public void service(final Request request) throws Exception {
        activeServer.service(request);
    }

    public void shutdown() {
        cancel();
        pushGroupManager.deleteObserver(this);
        activeServer.shutdown();
    }

    public void run() {
        if ((System.currentTimeMillis() > responseTimeoutTime) && (!pendingRequest.isEmpty())) {
            respondIfPendingRequest(NoopHandler);
        }
    }

    private void sendNotifications(String[] ids) {
        //stop sending notifications if pushID are not used anymore by the browser
        List pushIDs = new ArrayList(Arrays.asList(ids));
        pushIDs.retainAll(participatingPushIDs);
        if (!pushIDs.isEmpty()) {
            notifiedPushIDs.addAll(pushIDs);
            resetTimeout();
            respondIfNotificationsAvailable();
        }
    }

    private void resendLastNotifications() {
        sendNotifications(lastNotifications);
    }

    private synchronized void respondIfNotificationsAvailable() {
        if (!notifiedPushIDs.isEmpty()) {
            //save notifications, maybe they will need to be resent when blocking connection switches to another window 
            lastNotifications = (String[]) notifiedPushIDs.toArray(new String[0]);
            respondIfPendingRequest(new NotificationHandler(lastNotifications) {
                public void writeTo(Writer writer) throws IOException {
                    super.writeTo(writer);

                    if (log.isLoggable(Level.FINEST)) {
                        log.finest("Sending notifications for " + notifiedPushIDs + ".");
                    }
                    notifiedPushIDs.removeAll(Arrays.asList(lastNotifications));
                }
            });
        }
    }

    private void resetTimeout() {
        responseTimeoutTime = System.currentTimeMillis() + timeoutInterval;
    }

    private void respondIfPendingRequest(ResponseHandler handler) {
        Request previousRequest = (Request) pendingRequest.poll();
        if (previousRequest != null) {
            try {
                previousRequest.respondWith(handler);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class NotificationHandler extends FixedXMLContentHandler {
        private String[] pushIDs;

        private NotificationHandler(String[] pushIDs) {
            this.pushIDs = pushIDs;
        }

        public void writeTo(Writer writer) throws IOException {
            writer.write("<notified-pushids>");
            for (int i = 0; i < pushIDs.length; i++) {
                String id = pushIDs[i];
                if (i > 0) {
                    writer.write(' ');
                }
                writer.write(id);
            }
            writer.write("</notified-pushids>");
        }
    }
}
