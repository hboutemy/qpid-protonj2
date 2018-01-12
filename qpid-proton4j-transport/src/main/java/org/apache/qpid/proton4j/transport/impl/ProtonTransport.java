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
package org.apache.qpid.proton4j.transport.impl;

import java.io.IOException;

import org.apache.qpid.proton4j.amqp.transport.AMQPHeader;
import org.apache.qpid.proton4j.buffer.ProtonBuffer;
import org.apache.qpid.proton4j.buffer.ProtonBufferAllocator;
import org.apache.qpid.proton4j.transport.FrameParser;
import org.apache.qpid.proton4j.transport.ProtocolTracer;
import org.apache.qpid.proton4j.transport.Transport;
import org.apache.qpid.proton4j.transport.TransportListener;

/**
 * The default Proton-J Transport implementation.
 */
public class ProtonTransport implements Transport {

    private int maxFrameSize;
    private int initialMaxFrameSize;

    private ProtonBufferAllocator bufferAllocator;
    private TransportListener transportListener;
    private ProtocolTracer tracer;

    private FrameParser currentParser = new AmqpHeaderParser(this);;

    @Override
    public void processIncoming(ProtonBuffer buffer) throws IOException {
        currentParser.parse(buffer);
    }

    @Override
    public void setBufferAllocator(ProtonBufferAllocator allocator) {
        this.bufferAllocator = allocator;
    }

    @Override
    public ProtonBufferAllocator getBufferAllocator() {
        return bufferAllocator;
    }

    @Override
    public void setTransportListener(TransportListener listener) {
        transportListener = listener;
    }

    @Override
    public TransportListener getTransportListener() {
        return transportListener;
    }

    @Override
    public void setProtocolTracer(ProtocolTracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public ProtocolTracer getProtocolTracer() {
        return tracer;
    }

    @Override
    public int getMaxFrameSize() {
        return maxFrameSize;
    }

    @Override
    public void setMaxFrameSize(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }

    public int getInitialMaxFrameSize() {
        return initialMaxFrameSize;
    }

    public void setInitialMaxFrameSize(int initialMaxFrameSize) {
        this.initialMaxFrameSize = initialMaxFrameSize;
    }

    /**
     * Handles receipt of an AMQP Header from the incoming data stream
     * <p>
     * The method is called on successful decode of an AMQP Header giving the
     * Transport instance a chance to alter its state to reflect the processing
     * that should occur following the type of header read.
     *
     * @param header
     *      The newly decoded AMQP Header instance from the data stream
     *
     * @throws IOException if an error occurs while processing the incoming header.
     */
    public void onAMQPHeader(AMQPHeader header) throws IOException {
        currentParser = new AmqpFrameParser(this);

        if (transportListener != null) {
            transportListener.onPerformative(header);
        }
    }
}
