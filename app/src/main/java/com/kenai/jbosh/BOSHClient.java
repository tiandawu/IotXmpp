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

import com.kenai.jbosh.ComposableBody.Builder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * BOSH Client session instance.  Each communication session with a remote
 * connection manager is represented and handled by an instance of this
 * class.  This is the main entry point for client-side communications.
 * To create a new session, a client configuration must first be created
 * and then used to create a client instance:
 * <pre>
 * BOSHClientConfig cfg = BOSHClientConfig.Builder.create(
 *         "http://server:1234/httpbind", "jabber.org")
 *     .setFrom("user@jabber.org")
 *     .build();
 * BOSHClient client = BOSHClient.create(cfg);
 * </pre>
 * Additional client configuration options are available.  See the
 * {@code BOSHClientConfig.Builder} class for more information.
 * <p/>
 * Once a {@code BOSHClient} instance has been created, communication with
 * the remote connection manager can begin.  No attempt will be made to
 * establish a connection to the connection manager until the first call
 * is made to the {@code send(ComposableBody)} method.  Note that it is
 * possible to send an empty body to cause an immediate connection attempt
 * to the connection manager.  Sending an empty message would look like
 * the following:
 * <pre>
 * client.send(ComposableBody.builder().build());
 * </pre>
 * For more information on creating body messages with content, see the
 * {@code ComposableBody.Builder} class documentation.
 * <p/>
 * Once a session has been successfully started, the client instance can be
 * used to send arbitrary payload data.  All aspects of the BOSH
 * protocol involving setting and processing attributes in the BOSH
 * namespace will be handled by the client code transparently and behind the
 * scenes.  The user of the client instance can therefore concentrate
 * entirely on the content of the message payload, leaving the semantics of
 * the BOSH protocol to the client implementation.
 * <p/>
 * To be notified of incoming messages from the remote connection manager,
 * a {@code BOSHClientResponseListener} should be added to the client instance.
 * All incoming messages will be published to all response listeners as they
 * arrive and are processed.  As with the transmission of payload data via
 * the {@code send(ComposableBody)} method, there is no need to worry about
 * handling of the BOSH attributes, since this is handled behind the scenes.
 * <p/>
 * If the connection to the remote connection manager is terminated (either
 * explicitly or due to a terminal condition of some sort), all connection
 * listeners will be notified.  After the connection has been closed, the
 * client instance is considered dead and a new one must be created in order
 * to resume communications with the remote server.
 * <p/>
 * Instances of this class are thread-safe.
 *
 * @see BOSHClientConfig.Builder
 * @see BOSHClientResponseListener
 * @see BOSHClientConnListener
 * @see ComposableBody.Builder
 */
public final class BOSHClient {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(
            BOSHClient.class.getName());

    /**
     * Value of the 'type' attribute used for session termination.
     */
    private static final String TERMINATE = "terminate";
    
    /**
     * Value of the 'type' attribute used for recoverable errors.
     */
    private static final String ERROR = "error";

    /**
     * Message to use for interrupted exceptions.
     */
    private static final String INTERRUPTED = "Interrupted";

    /**
     * Message used for unhandled exceptions.
     */
    private static final String UNHANDLED = "Unhandled Exception";

    /**
     * Message used whena null listener is detected.
     */
    private static final String NULL_LISTENER = "Listener may not b enull";

    /**
     * Default empty request delay.
     */
    private static final int DEFAULT_EMPTY_REQUEST_DELAY = 100;

    /**
     * Amount of time to wait before sending an empty request, in
     * milliseconds.
     */
    private static final int EMPTY_REQUEST_DELAY = Integer.getInteger(
            BOSHClient.class.getName() + ".emptyRequestDelay",
            DEFAULT_EMPTY_REQUEST_DELAY);

    /**
     * Default value for the pause margin.
     */
    private static final int DEFAULT_PAUSE_MARGIN = 500;

    /**
     * The amount of time in milliseconds which will be reserved as a
     * safety margin when scheduling empty requests against a maxpause
     * value.   This should give us enough time to build the message
     * and transport it to the remote host.
     */
    private static final int PAUSE_MARGIN = Integer.getInteger(
            BOSHClient.class.getName() + ".pauseMargin",
            DEFAULT_PAUSE_MARGIN);
    
    /**
     * Flag indicating whether or not we want to perform assertions.
     */
    private static final boolean ASSERTIONS;

    /**
     * Connection listeners.
     */
    private final Set<BOSHClientConnListener> connListeners =
            new CopyOnWriteArraySet<BOSHClientConnListener>();

    /**
     * Request listeners.
     */
    private final Set<BOSHClientRequestListener> requestListeners =
            new CopyOnWriteArraySet<BOSHClientRequestListener>();

    /**
     * Response listeners.
     */
    private final Set<BOSHClientResponseListener> responseListeners =
            new CopyOnWriteArraySet<BOSHClientResponseListener>();

    /**
     * Lock instance.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Condition indicating that there are messages to be exchanged.
     */
    private final Condition notEmpty = lock.newCondition();

    /**
     * Condition indicating that there are available slots for sending
     * messages.
     */
    private final Condition notFull = lock.newCondition();

    /**
     * Condition indicating that there are no outstanding connections.
     */
    private final Condition drained = lock.newCondition();

    /**
     * Session configuration.
     */
    private final BOSHClientConfig cfg;

    /**
     * Processor thread runnable instance.
     */
    private final Runnable procRunnable = new Runnable() {
        /**
         * Process incoming messages.
         */
        public void run() {
            processMessages();
        }
    };

    /**
     * Processor thread runnable instance.
     */
    private final Runnable emptyRequestRunnable = new Runnable() {
        /**
         * Process incoming messages.
         */
        public void run() {
            sendEmptyRequest();
        }
    };

    /**
     * HTTPSender instance.
     */
    private final HTTPSender httpSender =
            new ApacheHTTPSender();

    /**
     * Storage for test hook implementation.
     */
    private final AtomicReference<ExchangeInterceptor> exchInterceptor =
            new AtomicReference<ExchangeInterceptor>();

    /**
     * Request ID sequence to use for the session.
     */
    private final RequestIDSequence requestIDSeq = new RequestIDSequence();

    /**
     * ScheduledExcecutor to use for deferred tasks.
     */
    private final ScheduledExecutorService schedExec =
            Executors.newSingleThreadScheduledExecutor();

    /************************************************************
     * The following vars must be accessed via the lock instance.
     */

    /**
     * Thread which is used to process responses from the connection
     * manager.  Becomes null when session is terminated.
     */
    private Thread procThread;

    /**
     * Future for sending a deferred empty request, if needed.
     */
    private ScheduledFuture emptyRequestFuture;

    /**
     * Connection Manager session parameters.  Only available when in a
     * connected state.
     */
    private CMSessionParams cmParams;

    /**
     * List of active/outstanding requests.
     */
    private Queue<HTTPExchange> exchanges = new LinkedList<HTTPExchange>();

    /**
     * Set of RIDs which have been received, for the purpose of sending
     * response acknowledgements.
     */
    private SortedSet<Long> pendingResponseAcks = new TreeSet<Long>();
    
    /**
     * The highest RID that we've already received a response for.  This value
     * is used to implement response acks.
     */
    private Long responseAck = Long.valueOf(-1L);

    /**
     * List of requests which have been made but not yet acknowledged.  This
     * list remains unpopulated if the CM is not acking requests.
     */
    private List<ComposableBody> pendingRequestAcks =
            new ArrayList<ComposableBody>();

    ///////////////////////////////////////////////////////////////////////////
    // Classes:

    /**
     * Class used in testing to dynamically manipulate received exchanges
     * at test runtime.
     */
    abstract static class ExchangeInterceptor {
        /**
         * Limit construction.
         */
        ExchangeInterceptor() {
            // Empty;
        }

        /**
         * Hook to manipulate an HTTPExchange as is is about to be processed.
         *
         * @param exch original exchange that would be processed
         * @return replacement exchange instance, or {@code null} to skip
         *  processing of this exchange
         */
        abstract HTTPExchange interceptExchange(final HTTPExchange exch);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Constructors:

    /**
     * Determine whether or not we should perform assertions.  Assertions
     * can be specified via system property explicitly, or defaulted to
     * the JVM assertions status.
     */
    static {
        final String prop =
                BOSHClient.class.getSimpleName() + ".assertionsEnabled";
        boolean enabled = false;
        if (System.getProperty(prop) == null) {
            assert enabled = true;
        } else {
            enabled = Boolean.getBoolean(prop);
        }
        ASSERTIONS = enabled;
    }

    /**
     * Prevent direct construction.
     */
    private BOSHClient(final BOSHClientConfig sessCfg) {
        cfg = sessCfg;
        init();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Public methods:

    /**
     * Create a new BOSH client session using the client configuration
     * information provided.
     *
     * @param clientCfg session configuration
     * @return BOSH session instance
     */
    public static BOSHClient create(final BOSHClientConfig clientCfg) {
        if (clientCfg == null) {
            throw(new IllegalArgumentException(
                    "Client configuration may not be null"));
        }
        return new BOSHClient(clientCfg);
    }

    /**
     * Get the client configuration that was used to create this client
     * instance.
     *
     * @return client configuration
     */
    public BOSHClientConfig getBOSHClientConfig() {
        return cfg;
    }

    /**
     * Adds a connection listener to the session.
     *
     * @param listener connection listener to add, if not already added
     */
    public void addBOSHClientConnListener(
            final BOSHClientConnListener listener) {
        if (listener == null) {
            throw(new IllegalArgumentException(NULL_LISTENER));
        }
        connListeners.add(listener);
    }

    /**
     * Removes a connection listener from the session.
     *
     * @param listener connection listener to remove, if previously added
     */
    public void removeBOSHClientConnListener(
            final BOSHClientConnListener listener) {
        if (listener == null) {
            throw(new IllegalArgumentException(NULL_LISTENER));
        }
        connListeners.remove(listener);
    }

    /**
     * Adds a request message listener to the session.
     *
     * @param listener request listener to add, if not already added
     */
    public void addBOSHClientRequestListener(
            final BOSHClientRequestListener listener) {
        if (listener == null) {
            throw(new IllegalArgumentException(NULL_LISTENER));
        }
        requestListeners.add(listener);
    }

    /**
     * Removes a request message listener from the session, if previously
     * added.
     *
     * @param listener instance to remove
     */
    public void removeBOSHClientRequestListener(
            final BOSHClientRequestListener listener) {
        if (listener == null) {
            throw(new IllegalArgumentException(NULL_LISTENER));
        }
        requestListeners.remove(listener);
    }

    /**
     * Adds a response message listener to the session.
     *
     * @param listener response listener to add, if not already added
     */
    public void addBOSHClientResponseListener(
            final BOSHClientResponseListener listener) {
        if (listener == null) {
            throw(new IllegalArgumentException(NULL_LISTENER));
        }
        responseListeners.add(listener);
    }

    /**
     * Removes a response message listener from the session, if previously
     * added.
     *
     * @param listener instance to remove
     */
    public void removeBOSHClientResponseListener(
            final BOSHClientResponseListener listener) {
        if (listener == null) {
            throw(new IllegalArgumentException(NULL_LISTENER));
        }
        responseListeners.remove(listener);
    }

    /**
     * Send the provided message data to the remote connection manager.  The
     * provided message body does not need to have any BOSH-specific attribute
     * information set.  It only needs to contain the actual message payload
     * that should be delivered to the remote server.
     * <p/>
     * The first call to this method will result in a connection attempt
     * to the remote connection manager.  Subsequent calls to this method
     * will block until the underlying session state allows for the message
     * to be transmitted.  In certain scenarios - such as when the maximum
     * number of outbound connections has been reached - calls to this method
     * will block for short periods of time.
     *
     * @param body message data to send to remote server
     * @throws BOSHException on message transmission failure
     */
    public void send(final ComposableBody body) throws BOSHException {
        assertUnlocked();
        if (body == null) {
            throw(new IllegalArgumentException(
                    "Message body may not be null"));
        }

        HTTPExchange exch;
        CMSessionParams params;
        lock.lock();
        try {
            blockUntilSendable(body);
            if (!isWorking() && !isTermination(body)) {
                throw(new BOSHException(
                        "Cannot send message when session is closed"));
            }
            
            long rid = requestIDSeq.getNextRID();
            ComposableBody request = body;
            params = cmParams;
            if (params == null && exchanges.isEmpty()) {
                // This is the first message being sent
                request = applySessionCreationRequest(rid, body);
            } else {
                request = applySessionData(rid, body);
                if (cmParams.isAckingRequests()) {
                    pendingRequestAcks.add(request);
                }
            }
            exch = new HTTPExchange(request);
            exchanges.add(exch);
            notEmpty.signalAll();
            clearEmptyRequest();
        } finally {
            lock.unlock();
        }
        AbstractBody finalReq = exch.getRequest();
        HTTPResponse resp = httpSender.send(params, finalReq);
        exch.setHTTPResponse(resp);
        fireRequestSent(finalReq);
    }

    /**
     * Attempt to pause the current session.  When supported by the remote
     * connection manager, pausing the session will result in the connection
     * manager closing out all outstanding requests (including the pause
     * request) and increases the inactivity timeout of the session.  The
     * exact value of the temporary timeout is dependent upon the connection
     * manager.  This method should be used if a client encounters an
     * exceptional temporary situation during which it will be unable to send
     * requests to the connection manager for a period of time greater than
     * the maximum inactivity period.
     *
     * The session will revert back to it's normal, unpaused state when the
     * client sends it's next message.
     *
     * @return {@code true} if the connection manager supports session pausing,
     *  {@code false} if the connection manager does not support session
     *  pausing or if the session has not yet been established
     */
    public boolean pause() {
        assertUnlocked();
        lock.lock();
        AttrMaxPause maxPause = null;
        try {
            if (cmParams == null) {
                return false;
            }

            maxPause = cmParams.getMaxPause();
            if (maxPause == null) {
                return false;
            }
        } finally {
            lock.unlock();
        }
        try {
            send(ComposableBody.builder()
                    .setAttribute(Attributes.PAUSE, maxPause.toString())
                    .build());
        } catch (BOSHException boshx) {
            LOG.log(Level.FINEST, "Could not send pause", boshx);
        }
        return true;
    }

    /**
     * End the BOSH session by disconnecting from the remote BOSH connection
     * manager.
     *
     * @throws BOSHException when termination message cannot be sent
     */
    public void disconnect() throws BOSHException {
        disconnect(ComposableBody.builder().build());
    }

    /**
     * End the BOSH session by disconnecting from the remote BOSH connection
     * manager, sending the provided content in the final connection
     * termination message.
     *
     * @param msg final message to send
     * @throws BOSHException when termination message cannot be sent
     */
    public void disconnect(final ComposableBody msg) throws BOSHException {
        if (msg == null) {
            throw(new IllegalArgumentException(
                    "Message body may not be null"));
        }

        Builder builder = msg.rebuild();
        builder.setAttribute(Attributes.TYPE, TERMINATE);
        send(builder.build());
    }

    /**
     * Forcibly close this client session instance.  The preferred mechanism
     * to close the connection is to send a disconnect message and wait for
     * organic termination.  Calling this method simply shuts down the local
     * session without sending a termination message, releasing all resources
     * associated with the session.
     */
    public void close() {
        dispose(new BOSHException("Session explicitly closed by caller"));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Package-private methods:

    /**
     * Get the current CM session params.
     *
     * @return current session params, or {@code null}
     */
    CMSessionParams getCMSessionParams() {
        lock.lock();
        try {
            return cmParams;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Wait until no more messages are waiting to be processed.
     */
    void drain() {
        lock.lock();
        try {
            LOG.finest("Waiting while draining...");
            while (isWorking()
                    && (emptyRequestFuture == null
                    || emptyRequestFuture.isDone())) {
                try {
                    drained.await();
                } catch (InterruptedException intx) {
                    LOG.log(Level.FINEST, INTERRUPTED, intx);
                }
            }
            LOG.finest("Drained");
        } finally {
            lock.unlock();
        }
    }

    /**
     * Test method used to forcibly discard next exchange.
     *
     * @param interceptor exchange interceptor
     */
    void setExchangeInterceptor(final ExchangeInterceptor interceptor) {
        exchInterceptor.set(interceptor);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Private methods:

    /**
     * Initialize the session.  This initializes the underlying HTTP
     * transport implementation and starts the receive thread.
     */
    private void init() {
        assertUnlocked();
        
        lock.lock();
        try {
            httpSender.init(cfg);
            procThread = new Thread(procRunnable);
            procThread.setDaemon(true);
            procThread.setName(BOSHClient.class.getSimpleName()
                    + "[" + System.identityHashCode(this)
                    + "]: Receive thread");
            procThread.start();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Destroy this session.
     *
     * @param cause the reason for the session termination, or {@code null}
     *  for normal termination
     */
    private void dispose(final Throwable cause) {
        assertUnlocked();
        
        lock.lock();
        try {
            if (procThread == null) {
                // Already disposed
                return;
            }
            procThread = null;
        } finally {
            lock.unlock();
        }

        if (cause == null) {
            fireConnectionClosed();
        } else {
            fireConnectionClosedOnError(cause);
        }

        lock.lock();
        try {
            clearEmptyRequest();
            exchanges = null;
            cmParams = null;
            pendingResponseAcks = null;
            pendingRequestAcks = null;
            notEmpty.signalAll();
            notFull.signalAll();
            drained.signalAll();
        } finally {
            lock.unlock();
        }
        
        httpSender.destroy();
        schedExec.shutdownNow();
    }

    /**
     * Determines if the message body specified indicates a request to
     * pause the session.
     *
     * @param msg message to evaluate
     * @return {@code true} if the message is a pause request, {@code false}
     *  otherwise
     */
    private static boolean isPause(final AbstractBody msg) {
        return msg.getAttribute(Attributes.PAUSE) != null;
    }
    
    /**
     * Determines if the message body specified indicates a termination of
     * the session.
     *
     * @param msg message to evaluate
     * @return {@code true} if the message is a session termination,
     *  {@code false} otherwise
     */
    private static boolean isTermination(final AbstractBody msg) {
        return TERMINATE.equals(msg.getAttribute(Attributes.TYPE));
    }

    /**
     * Evaluates the HTTP response code and response message and returns the
     * terminal binding condition that it describes, if any.
     *
     * @param respCode HTTP response code
     * @param respBody response body
     * @return terminal binding condition, or {@code null} if not a terminal
     *  binding condition message
     */
    private TerminalBindingCondition getTerminalBindingCondition(
            final int respCode,
            final AbstractBody respBody) {
        assertLocked();

        if (isTermination(respBody)) {
            String str = respBody.getAttribute(Attributes.CONDITION);
            return TerminalBindingCondition.forString(str);
        }
        // Check for deprecated HTTP Error Conditions
        if (cmParams != null && cmParams.getVersion() == null) {
            return TerminalBindingCondition.forHTTPResponseCode(respCode);
        }
        return null;
    }

    /**
     * Determines if the message specified is immediately sendable or if it
     * needs to block until the session state changes.
     *
     * @param msg message to evaluate
     * @return {@code true} if the message can be immediately sent,
     *  {@code false} otherwise
     */
    private boolean isImmediatelySendable(final AbstractBody msg) {
        assertLocked();

        if (cmParams == null) {
            // block if we're waiting for a response to our first request
            return exchanges.isEmpty();
        }

        AttrRequests requests = cmParams.getRequests();
        if (requests == null) {
            return true;
        }
        int maxRequests = requests.intValue();
        if (exchanges.size() < maxRequests) {
            return true;
        }
        if (exchanges.size() == maxRequests
                && (isTermination(msg) || isPause(msg))) {
            // One additional terminate or pause message is allowed
            return true;
        }
        return false;
    }

    /**
     * Determines whether or not the session is still active.
     *
     * @return {@code true} if it is, {@code false} otherwise
     */
    private boolean isWorking() {
        assertLocked();

        return procThread != null;
    }

    /**
     * Blocks until either the message provided becomes immediately
     * sendable or until the session is terminated.
     *
     * @param msg message to evaluate
     */
    private void blockUntilSendable(final AbstractBody msg) {
        assertLocked();

        while (isWorking() && !isImmediatelySendable(msg)) {
            try {
                notFull.await();
            } catch (InterruptedException intx) {
                LOG.log(Level.FINEST, INTERRUPTED, intx);
            }
        }
    }

    /**
     * Modifies the specified body message such that it becomes a new
     * BOSH session creation request.
     *
     * @param rid request ID to use
     * @param orig original body to modify
     * @return modified message which acts as a session creation request
     */
    private ComposableBody applySessionCreationRequest(
            final long rid, final ComposableBody orig) throws BOSHException {
        assertLocked();
        
        Builder builder = orig.rebuild();
        builder.setAttribute(Attributes.TO, cfg.getTo());
        builder.setAttribute(Attributes.XML_LANG, cfg.getLang());
        builder.setAttribute(Attributes.VER,
                AttrVersion.getSupportedVersion().toString());
        builder.setAttribute(Attributes.WAIT, "60");
        builder.setAttribute(Attributes.HOLD, "1");
        builder.setAttribute(Attributes.RID, Long.toString(rid));
        applyRoute(builder);
        applyFrom(builder);
        builder.setAttribute(Attributes.ACK, "1");

        // Make sure the following are NOT present (i.e., during retries)
        builder.setAttribute(Attributes.SID, null);
        return builder.build();
    }

    /**
     * Applies routing information to the request message who's builder has
     * been provided.
     *
     * @param builder builder instance to add routing information to
     */
    private void applyRoute(final Builder builder) {
        assertLocked();
        
        String route = cfg.getRoute();
        if (route != null) {
            builder.setAttribute(Attributes.ROUTE, route);
        }
    }

    /**
     * Applies the local station ID information to the request message who's
     * builder has been provided.
     *
     * @param builder builder instance to add station ID information to
     */
    private void applyFrom(final Builder builder) {
        assertLocked();

        String from = cfg.getFrom();
        if (from != null) {
            builder.setAttribute(Attributes.FROM, from);
        }
    }

    /**
     * Applies existing session data to the outbound request, returning the
     * modified request.
     *
     * This method assumes the lock is currently held.
     *
     * @param rid request ID to use
     * @param orig original/raw request
     * @return modified request with session information applied
     */
    private ComposableBody applySessionData(
            final long rid,
            final ComposableBody orig) throws BOSHException {
        assertLocked();

        Builder builder = orig.rebuild();
        builder.setAttribute(Attributes.SID,
                cmParams.getSessionID().toString());
        builder.setAttribute(Attributes.RID, Long.toString(rid));
        applyResponseAcknowledgement(builder, rid);
        return builder.build();
    }

    /**
     * Sets the 'ack' attribute of the request to the value of the highest
     * 'rid' of a request for which it has already received a response in the
     * case where it has also received all responses associated with lower
     * 'rid' values.  The only exception is that, after its session creation
     * request, the client SHOULD NOT include an 'ack' attribute in any request
     * if it has received responses to all its previous requests.
     *
     * @param builder message builder
     * @param rid current request RID
     */
    private void applyResponseAcknowledgement(
            final Builder builder,
            final long rid) {
        assertLocked();

        if (responseAck.equals(Long.valueOf(-1L))) {
            // We have not received any responses yet
            return;
        }

        Long prevRID = Long.valueOf(rid - 1L);
        if (responseAck.equals(prevRID)) {
            // Implicit ack
            return;
        }
        
        builder.setAttribute(Attributes.ACK, responseAck.toString());
    }

    /**
     * While we are "connected", process received responses.
     *
     * This method is run in the processing thread.
     */
    private void processMessages() {
        LOG.log(Level.FINEST, "Processing thread starting");
        try {
            HTTPExchange exch;
            do {
                exch = nextExchange();
                if (exch == null) {
                    break;
                }

                // Test hook to manipulate what the client sees:
                ExchangeInterceptor interceptor = exchInterceptor.get();
                if (interceptor != null) {
                    HTTPExchange newExch = interceptor.interceptExchange(exch);
                    if (newExch == null) {
                        LOG.log(Level.FINE, "Discarding exchange on request "
                                + "of test hook: RID="
                                + exch.getRequest().getAttribute(
                                    Attributes.RID));
                        lock.lock();
                        try {
                            exchanges.remove(exch);
                        } finally {
                            lock.unlock();
                        }
                        continue;
                    }
                    exch = newExch;
                }

                processExchange(exch);
            } while (true);
        } finally {
            LOG.log(Level.FINEST, "Processing thread exiting");
        }

    }

    /**
     * Get the next message exchange to process, blocking until one becomes
     * available if nothing is already waiting for processing.
     *
     * @return next available exchange to process, or {@code null} if no
     *  exchanges are immediately available
     */
    private HTTPExchange nextExchange() {
        assertUnlocked();

        final Thread thread = Thread.currentThread();
        HTTPExchange exch = null;
        lock.lock();
        try {
            do {
                if (!thread.equals(procThread)) {
                    break;
                }
                exch = exchanges.peek();
                if (exch == null) {
                    try {
                        notEmpty.await();
                    } catch (InterruptedException intx) {
                        LOG.log(Level.FINEST, INTERRUPTED, intx);
                    }
                }
            } while (exch == null);
        } finally {
            lock.unlock();
        }
        return exch;
    }

    /**
     * Process the next, provided exchange.  This is the main processing
     * method of the receive thread.
     *
     * @param exch message exchange to process
     */
    private void processExchange(final HTTPExchange exch) {
        assertUnlocked();

        HTTPResponse resp;
        AbstractBody body;
        int respCode;
        try {
            resp = exch.getHTTPResponse();
            body = resp.getBody();
            respCode = resp.getHTTPStatus();
        } catch (BOSHException boshx) {
            LOG.log(Level.FINEST, "Could not obtain response", boshx);
            dispose(boshx);
            return;
        } catch (InterruptedException intx) {
            LOG.log(Level.FINEST, INTERRUPTED, intx);
            dispose(intx);
            return;
        }
        fireResponseReceived(body);

        // Process the message with the current session state
        AbstractBody req = exch.getRequest();
        CMSessionParams params;
        List<HTTPExchange> toResend = null;
        lock.lock();
        try {
            // Check for session creation response info, if needed
            if (cmParams == null) {
                cmParams = CMSessionParams.fromSessionInit(req, body);

                // The following call handles the lock. It's not an escape.
                fireConnectionEstablished();
            }
            params = cmParams;

            checkForTerminalBindingConditions(body, respCode);
            if (isTermination(body)) {
                // Explicit termination
                lock.unlock();
                dispose(null);
                return;
            }
            
            if (isRecoverableBindingCondition(body)) {
                // Retransmit outstanding requests
                if (toResend == null) {
                    toResend = new ArrayList<HTTPExchange>(exchanges.size());
                }
                for (HTTPExchange exchange : exchanges) {
                    HTTPExchange resendExch =
                            new HTTPExchange(exchange.getRequest());
                    toResend.add(resendExch);
                }
                for (HTTPExchange exchange : toResend) {
                    exchanges.add(exchange);
                }
            } else {
                // Process message as normal
                processRequestAcknowledgements(req, body);
                processResponseAcknowledgementData(req);
                HTTPExchange resendExch =
                        processResponseAcknowledgementReport(body);
                if (resendExch != null && toResend == null) {
                    toResend = new ArrayList<HTTPExchange>(1);
                    toResend.add(resendExch);
                    exchanges.add(resendExch);
                }
            }
        } catch (BOSHException boshx) {
            LOG.log(Level.FINEST, "Could not process response", boshx);
            lock.unlock();
            dispose(boshx);
            return;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                try {
                    exchanges.remove(exch);
                    if (exchanges.isEmpty()) {
                        scheduleEmptyRequest(processPauseRequest(req));
                    }
                    notFull.signalAll();
                } finally {
                    lock.unlock();
                }
            }
        }

        if (toResend != null) {
            for (HTTPExchange resend : toResend) {
                HTTPResponse response =
                        httpSender.send(params, resend.getRequest());
                resend.setHTTPResponse(response);
                fireRequestSent(resend.getRequest());
            }
        }
    }
    
    /**
     * Clears any scheduled empty requests.
     */
    private void clearEmptyRequest() {
        assertLocked();

        if (emptyRequestFuture != null) {
            emptyRequestFuture.cancel(false);
            emptyRequestFuture = null;
        }
    }

    /**
     * Calculates the default empty request delay/interval to use for the
     * active session.
     *
     * @return delay in milliseconds
     */
    private long getDefaultEmptyRequestDelay() {
        assertLocked();
        
        // Figure out how long we should wait before sending an empty request
        AttrPolling polling = cmParams.getPollingInterval();
        long delay;
        if (polling == null) {
            delay = EMPTY_REQUEST_DELAY;
        } else {
            delay = polling.getInMilliseconds();
        }
        return delay;
    }

    /**
     * Schedule an empty request to be sent if no other requests are
     * sent in a reasonable amount of time.
     */
    private void scheduleEmptyRequest(long delay) {
        assertLocked();
        if (delay < 0L) {
            throw(new IllegalArgumentException(
                    "Empty request delay must be >= 0 (was: " + delay + ")"));
        }

        clearEmptyRequest();
        if (!isWorking()) {
            return;
        }
        
        // Schedule the transmission
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("Scheduling empty request in " + delay + "ms");
        }
        try {
            emptyRequestFuture = schedExec.schedule(emptyRequestRunnable,
                    delay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException rex) {
            LOG.log(Level.FINEST, "Could not schedule empty request", rex);
        }
        drained.signalAll();
    }

    /**
     * Sends an empty request to maintain session requirements.  If a request
     * is sent within a reasonable time window, the empty request transmission
     * will be cancelled.
     */
    private void sendEmptyRequest() {
        assertUnlocked();
        // Send an empty request
        LOG.finest("Sending empty request");
        try {
            send(ComposableBody.builder().build());
        } catch (BOSHException boshx) {
            dispose(boshx);
        }
    }

    /**
     * Assert that the internal lock is held.
     */
    private void assertLocked() {
        if (ASSERTIONS) {
            if (!lock.isHeldByCurrentThread()) {
                throw(new AssertionError("Lock is not held by current thread"));
            }
            return;
        }
    }

    /**
     * Assert that the internal lock is *not* held.
     */
    private void assertUnlocked() {
        if (ASSERTIONS) {
            if (lock.isHeldByCurrentThread()) {
                throw(new AssertionError("Lock is held by current thread"));
            }
            return;
        }
    }

    /**
     * Checks to see if the response indicates a terminal binding condition
     * (as per XEP-0124 section 17).  If it does, an exception is thrown.
     *
     * @param body response body to evaluate
     * @param code HTTP response code
     * @throws BOSHException if a terminal binding condition is detected
     */
    private void checkForTerminalBindingConditions(
            final AbstractBody body,
            final int code)
            throws BOSHException {
        TerminalBindingCondition cond =
                getTerminalBindingCondition(code, body);
        if (cond != null) {
            throw(new BOSHException(
                    "Terminal binding condition encountered: "
                    + cond.getCondition() + "  ("
                    + cond.getMessage() + ")"));
        }
    }

    /**
     * Determines whether or not the response indicates a recoverable
     * binding condition (as per XEP-0124 section 17).
     *
     * @param resp response body
     * @return {@code true} if it does, {@code false} otherwise
     */
    private static boolean isRecoverableBindingCondition(
            final AbstractBody resp) {
        return ERROR.equals(resp.getAttribute(Attributes.TYPE));
    }

    /**
     * Process the request to determine if the empty request delay
     * can be determined by looking to see if the request is a pause
     * request.  If it can, the request's delay is returned, otherwise
     * the default delay is returned.
     * 
     * @return delay in milliseconds that should elapse prior to an
     *  empty message being sent
     */
    private long processPauseRequest(
            final AbstractBody req) {
        assertLocked();

        if (cmParams != null && cmParams.getMaxPause() != null) {
            try {
                AttrPause pause = AttrPause.createFromString(
                        req.getAttribute(Attributes.PAUSE));
                if (pause != null) {
                    long delay = pause.getInMilliseconds() - PAUSE_MARGIN;
                    if (delay < 0) {
                        delay = EMPTY_REQUEST_DELAY;
                    }
                    return delay;
                }
            } catch (BOSHException boshx) {
                LOG.log(Level.FINEST, "Could not extract", boshx);
            }
        }

        return getDefaultEmptyRequestDelay();
    }

    /**
     * Check the response for request acknowledgements and take appropriate
     * action.
     *
     * This method assumes the lock is currently held.
     *
     * @param req request
     * @param resp response
     */
    private void processRequestAcknowledgements(
            final AbstractBody req, final AbstractBody resp) {
        assertLocked();
        
        if (!cmParams.isAckingRequests()) {
            return;
        }

        // If a report or time attribute is set, we aren't acking anything
        if (resp.getAttribute(Attributes.REPORT) != null) {
            return;
        }

        // Figure out what the highest acked RID is
        String acked = resp.getAttribute(Attributes.ACK);
        Long ackUpTo;
        if (acked == null) {
            // Implicit ack of all prior requests up until RID
            ackUpTo = Long.parseLong(req.getAttribute(Attributes.RID));
        } else {
            ackUpTo = Long.parseLong(acked);
        }

        // Remove the acked requests from the list
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Removing pending acks up to: " + ackUpTo);
        }
        Iterator<ComposableBody> iter = pendingRequestAcks.iterator();
        while (iter.hasNext()) {
            AbstractBody pending = iter.next();
            Long pendingRID = Long.parseLong(
                    pending.getAttribute(Attributes.RID));
            if (pendingRID.compareTo(ackUpTo) <= 0) {
                iter.remove();
            }
        }
    }

    /**
     * Process the response in order to update the response acknowlegement
     * data.
     *
     * This method assumes the lock is currently held.
     *
     * @param req request
     */
    private void processResponseAcknowledgementData(
            final AbstractBody req) {
        assertLocked();
        
        Long rid = Long.parseLong(req.getAttribute(Attributes.RID));
        if (responseAck.equals(Long.valueOf(-1L))) {
            // This is the first request
            responseAck = rid;
        } else {
            pendingResponseAcks.add(rid);
            // Remove up until the first missing response (or end of queue)
            Long whileVal = responseAck;
            while (whileVal.equals(pendingResponseAcks.first())) {
                responseAck = whileVal;
                pendingResponseAcks.remove(whileVal);
                whileVal = Long.valueOf(whileVal.longValue() + 1);
            }
        }
    }

    /**
     * Process the response in order to check for and respond to any potential
     * ack reports.
     *
     * This method assumes the lock is currently held.
     *
     * @param resp response
     * @return exchange to transmit if a resend is to be performed, or
     *  {@code null} if no resend is necessary
     * @throws BOSHException when a a retry is needed but cannot be performed
     */
    private HTTPExchange processResponseAcknowledgementReport(
            final AbstractBody resp)
            throws BOSHException {
        assertLocked();
        
        String reportStr = resp.getAttribute(Attributes.REPORT);
        if (reportStr == null) {
            // No report on this message
            return null;
        }
        
        Long report = Long.parseLong(reportStr);
        Long time = Long.parseLong(resp.getAttribute(Attributes.TIME));
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Received report of missing request (RID="
                    + report + ", time=" + time + "ms)");
        }

        // Find the missing request
        Iterator<ComposableBody> iter = pendingRequestAcks.iterator();
        AbstractBody req = null;
        while (iter.hasNext() && req == null) {
            AbstractBody pending = iter.next();
            Long pendingRID = Long.parseLong(
                    pending.getAttribute(Attributes.RID));
            if (report.equals(pendingRID)) {
                req = pending;
            }
        }

        if (req == null) {
            throw(new BOSHException("Report of missing message with RID '"
                    + reportStr
                    + "' but local copy of that request was not found"));
        }

        // Resend the missing request
        HTTPExchange exch = new HTTPExchange(req);
        exchanges.add(exch);
        notEmpty.signalAll();
        return exch;
    }

    /**
     * Notifies all request listeners that the specified request is being
     * sent.
     *
     * @param request request being sent
     */
    private void fireRequestSent(final AbstractBody request) {
        assertUnlocked();

        BOSHMessageEvent event = null;
        for (BOSHClientRequestListener listener : requestListeners) {
            if (event == null) {
                event = BOSHMessageEvent.createRequestSentEvent(this, request);
            }
            try {
                listener.requestSent(event);
            } catch (Exception ex) {
                LOG.log(Level.WARNING, UNHANDLED, ex);
            }
        }
    }

    /**
     * Notifies all response listeners that the specified response has been
     * received.
     *
     * @param response response received
     */
    private void fireResponseReceived(final AbstractBody response) {
        assertUnlocked();

        BOSHMessageEvent event = null;
        for (BOSHClientResponseListener listener : responseListeners) {
            if (event == null) {
                event = BOSHMessageEvent.createResponseReceivedEvent(
                        this, response);
            }
            try {
                listener.responseReceived(event);
            } catch (Exception ex) {
                LOG.log(Level.WARNING, UNHANDLED, ex);
            }
        }
    }

    /**
     * Notifies all connection listeners that the session has been successfully
     * established.
     */
    private void fireConnectionEstablished() {
        final boolean hadLock = lock.isHeldByCurrentThread();
        if (hadLock) {
            lock.unlock();
        }
        try {
            BOSHClientConnEvent event = null;
            for (BOSHClientConnListener listener : connListeners) {
                if (event == null) {
                    event = BOSHClientConnEvent
                            .createConnectionEstablishedEvent(this);
                }
                try {
                    listener.connectionEvent(event);
                } catch (Exception ex) {
                    LOG.log(Level.WARNING, UNHANDLED, ex);
                }
            }
        } finally {
            if (hadLock) {
                lock.lock();
            }
        }
    }

    /**
     * Notifies all connection listeners that the session has been
     * terminated normally.
     */
    private void fireConnectionClosed() {
        assertUnlocked();

        BOSHClientConnEvent event = null;
        for (BOSHClientConnListener listener : connListeners) {
            if (event == null) {
                event = BOSHClientConnEvent.createConnectionClosedEvent(this);
            }
            try {
                listener.connectionEvent(event);
            } catch (Exception ex) {
                LOG.log(Level.WARNING, UNHANDLED, ex);
            }
        }
    }

    /**
     * Notifies all connection listeners that the session has been
     * terminated due to the exceptional condition provided.
     *
     * @param cause cause of the termination
     */
    private void fireConnectionClosedOnError(
            final Throwable cause) {
        assertUnlocked();

        BOSHClientConnEvent event = null;
        for (BOSHClientConnListener listener : connListeners) {
            if (event == null) {
                event = BOSHClientConnEvent
                        .createConnectionClosedOnErrorEvent(
                        this, pendingRequestAcks, cause);
            }
            try {
                listener.connectionEvent(event);
            } catch (Exception ex) {
                LOG.log(Level.WARNING, UNHANDLED, ex);
            }
        }
    }

}
