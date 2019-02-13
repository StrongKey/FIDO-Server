/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License, as published by the Free Software Foundation and
 * available at http://www.fsf.org/licensing/licenses/lgpl.html,
 * version 2.1 or above.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001-2018 StrongAuth, Inc.
 *
 * $Date$
 * $Revision$
 * $Author$
 * $URL$
 *
 * *********************************************
 *                    888
 *                    888
 *                    888
 *  88888b.   .d88b.  888888  .d88b.  .d8888b
 *  888 "88b d88""88b 888    d8P  Y8b 88K
 *  888  888 888  888 888    88888888 "Y8888b.
 *  888  888 Y88..88P Y88b.  Y8b.          X88
 *  888  888  "Y88P"   "Y888  "Y8888   88888P'
 *
 * *********************************************
 * 
 */

package com.strongauth.skfe.client.impl;

import com.strongauth.skceclient.common.Constants;
import java.io.IOException;
import java.net.URI;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

@ClientEndpoint
public class WsFidoClientEndPoint extends Thread {

    private static Queue<Session> queue;

    private static Object waitLock;

    static WebSocketContainer container = null;
    static Session session = null;
    test t;

    public WsFidoClientEndPoint() {
        t = new test();
        waitLock = new Object();
        queue = new ConcurrentLinkedQueue<>();
    }

    @OnMessage
    public void onMessage(Session session, String msg) {
        try {
            t.setRes(true);
            t.setResponse(msg);
        } catch (Exception e) {
        }
    }

    private void wait4TerminateSignal() throws IOException, InterruptedException {
        synchronized (waitLock) {
            try {
                waitLock.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    @OnOpen
    public void open(Session session) {
        queue.add(session);
    }

    @OnError
    public void error(Session session, Throwable t) {
        queue.remove(session);
    }

    @OnClose
    public void closedConnection(Session session) {
        queue.remove(session);
    }

    public boolean getres() {
        return t.isRes();
    }

    public String getresponse() {
        return t.getResponse();
    }

    public void setres(boolean b) {
        t.setRes(b);
    }

    public void sendmsg(String hosturl, String wsmessage) {
        try {
            container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(WsFidoClientEndPoint.class, URI.create(hosturl + Constants.WEBSOCKET_ENDPOINT));
            session.getBasicRemote().sendText(wsmessage);
        } catch (DeploymentException | IOException ex) {
            Logger.getLogger(WsFidoClientEndPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            wait4TerminateSignal();
        } catch (IOException | InterruptedException e) {
            System.out.println("\n Exception : " + e.getLocalizedMessage());
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (IOException e) {
                    System.out.println("\n Exception : " + e.getLocalizedMessage());
                }
            }
        }
    }
}

class test {

    private static boolean res = false;
    private static String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        test.response = response;
    }

    public boolean isRes() {
        return res;
    }

    public void setRes(boolean res) {
        test.res = res;
    }

}
