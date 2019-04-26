/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.qpid.proton4j.engine.impl;

import org.apache.qpid.proton4j.amqp.transport.DeliveryState;
import org.apache.qpid.proton4j.buffer.ProtonBuffer;
import org.apache.qpid.proton4j.buffer.ProtonByteBufferAllocator;
import org.apache.qpid.proton4j.engine.OutgoingDelivery;
import org.apache.qpid.proton4j.engine.Sender;

/**
 * Proton outgoing delivery implementation
 */
public class ProtonOutgoingDelivery implements OutgoingDelivery {

    private static final long DELIVERY_INACTIVE = -1;
    private static final long DELIVERY_ABORTED = -2;

    private final ProtonContext context = new ProtonContext();
    private final ProtonSender link;

    private long deliveryId = DELIVERY_INACTIVE;

    private byte[] deliveryTag;
    private boolean complete;
    private int messageFormat;
    private boolean aborted;

    private DeliveryState localState;
    private boolean locallySettled;

    private DeliveryState remoteState;
    private boolean remotelySettled;

    private final ProtonBuffer payload = ProtonByteBufferAllocator.DEFAULT.allocate();

    public ProtonOutgoingDelivery(ProtonSender link) {
        this.link = link;
    }

    @Override
    public Sender getLink() {
        return link;
    }

    @Override
    public ProtonContext getContext() {
        return context;
    }

    @Override
    public byte[] getTag() {
        return deliveryTag;
    }

    @Override
    public DeliveryState getLocalState() {
        return localState;
    }

    @Override
    public DeliveryState getRemoteState() {
        return remoteState;
    }

    @Override
    public int getMessageFormat() {
        return messageFormat;
    }

    @Override
    public OutgoingDelivery setMessageFormat(int messageFormat) {
        this.messageFormat = messageFormat;
        return this;
    }

    @Override
    public boolean isPartial() {
        return !complete && !aborted;
    }

    @Override
    public boolean isAborted() {
        return aborted;
    }

    @Override
    public boolean isSettled() {
        return locallySettled;
    }

    @Override
    public boolean isRemotelySettled() {
        return remotelySettled;
    }

    @Override
    public OutgoingDelivery disposition(DeliveryState state) {
        return disposition(state, false);
    }

    @Override
    public OutgoingDelivery disposition(DeliveryState state, boolean settle) {
        if (locallySettled) {
            throw new IllegalStateException("Cannot update disposition or settle and already settled Delivery");
        }

        this.locallySettled = settle;
        this.localState = state;

        // If no transfers initiated yet we just store the state and transmit in the first transfer
        if (deliveryId > DELIVERY_INACTIVE) {
            link.disposition(this);
        }

        return this;
    }

    @Override
    public OutgoingDelivery settle() {
        return disposition(localState, true);
    }

    @Override
    public void writeBytes(ProtonBuffer buffer) {
        checkCompleteOrAborted();
        payload.writeBytes(buffer);  // TODO don't copy if we can
        complete = true;
        link.send(this, buffer);
    }

    @Override
    public void streamBytes(ProtonBuffer buffer) {
        streamBytes(buffer, false);
    }

    @Override
    public void streamBytes(ProtonBuffer buffer, boolean complete) {
        checkCompleteOrAborted();
        payload.writeBytes(buffer);  // TODO don't copy if we can
        this.complete = complete;
        link.send(this, buffer);
    }

    @Override
    public OutgoingDelivery abort() {
        checkCompleteOrAborted();

        // Cannot abort when nothing has been sent so far.
        if (deliveryId > DELIVERY_INACTIVE) {
            aborted = true;
            link.abort(this);
            deliveryId = DELIVERY_ABORTED;
        }

        return this;
    }

    //----- Internal methods meant only for use by Proton resources

    long getDeliveryId() {
        return deliveryId;
    }

    void setDeliveryId(byte deliveryId) {
        this.deliveryId = deliveryId;
    }

    void afterTransferWritten() {
        // TODO - Perform any cleanup needed like reclaiming buffer space if there
        //        is a composite or other complex buffer type in use.
    }

    //----- Private helper methods

    private void checkCompleteOrAborted() {
        if (complete || aborted) {
            throw new IllegalArgumentException("Cannot write to a delivery already marked as complete or has been aborted.");
        }
    }
}
