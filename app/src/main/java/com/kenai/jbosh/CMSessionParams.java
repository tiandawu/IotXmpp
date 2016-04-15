/*
 * Copyright 2009 Mike Cumings
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kenai.jbosh;

/**
 * A BOSH connection manager session instance.  This consolidates the
 * configuration knowledge related to the CM session and provides a
 * mechanism by which
 */
final class CMSessionParams {

    private final AttrSessionID sid;

    private final AttrWait wait;

    private final AttrVersion ver;

    private final AttrPolling polling;

    private final AttrInactivity inactivity;

    private final AttrRequests requests;

    private final AttrHold hold;

    private final AttrAccept accept;

    private final AttrMaxPause maxPause;

    private final AttrAck ack;

    private final AttrCharsets charsets;

    private final boolean ackingRequests;

    /**
     * Prevent direct construction.
     */
    private CMSessionParams(
            final AttrSessionID aSid,
            final AttrWait aWait,
            final AttrVersion aVer,
            final AttrPolling aPolling,
            final AttrInactivity aInactivity,
            final AttrRequests aRequests,
            final AttrHold aHold,
            final AttrAccept aAccept,
            final AttrMaxPause aMaxPause,
            final AttrAck aAck,
            final AttrCharsets aCharsets,
            final boolean amAckingRequests) {
        sid = aSid;
        wait = aWait;
        ver = aVer;
        polling = aPolling;
        inactivity = aInactivity;
        requests = aRequests;
        hold = aHold;
        accept = aAccept;
        maxPause = aMaxPause;
        ack = aAck;
        charsets = aCharsets;
        ackingRequests = amAckingRequests;
    }

    static CMSessionParams fromSessionInit(
            final AbstractBody req,
            final AbstractBody resp)
    throws BOSHException {
        AttrAck aAck = AttrAck.createFromString(
                resp.getAttribute(Attributes.ACK));
        String rid = req.getAttribute(Attributes.RID);
        boolean acking = (aAck != null && aAck.getValue().equals(rid));

        return new CMSessionParams(
            AttrSessionID.createFromString(
                getRequiredAttribute(resp, Attributes.SID)),
            AttrWait.createFromString(
                getRequiredAttribute(resp, Attributes.WAIT)),
            AttrVersion.createFromString(
                resp.getAttribute(Attributes.VER)),
            AttrPolling.createFromString(
                resp.getAttribute(Attributes.POLLING)),
            AttrInactivity.createFromString(
                resp.getAttribute(Attributes.INACTIVITY)),
            AttrRequests.createFromString(
                resp.getAttribute(Attributes.REQUESTS)),
            AttrHold.createFromString(
                resp.getAttribute(Attributes.HOLD)),
            AttrAccept.createFromString(
                resp.getAttribute(Attributes.ACCEPT)),
            AttrMaxPause.createFromString(
                resp.getAttribute(Attributes.MAXPAUSE)),
            aAck,
            AttrCharsets.createFromString(
                resp.getAttribute(Attributes.CHARSETS)),
            acking
            );
    }

    private static String getRequiredAttribute(
            final AbstractBody body,
            final BodyQName name)
    throws BOSHException {
        String attrStr = body.getAttribute(name);
        if (attrStr == null) {
            throw(new BOSHException(
                    "Connection Manager session creation response did not "
                    + "include required '" + name.getLocalPart()
                    + "' attribute"));
        }
        return attrStr;
    }

    AttrSessionID getSessionID() {
        return sid;
    }

    AttrWait getWait() {
        return wait;
    }

    AttrVersion getVersion() {
        return ver;
    }

    AttrPolling getPollingInterval() {
        return polling;
    }

    AttrInactivity getInactivityPeriod() {
        return inactivity;
    }

    AttrRequests getRequests() {
        return requests;
    }

    AttrHold getHold() {
        return hold;
    }

    AttrAccept getAccept() {
        return accept;
    }

    AttrMaxPause getMaxPause() {
        return maxPause;
    }

    AttrAck getAck() {
        return ack;
    }
    
    AttrCharsets getCharsets() {
        return charsets;
    }

    boolean isAckingRequests() {
        return ackingRequests;
    }

}
