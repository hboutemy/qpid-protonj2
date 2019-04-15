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
package org.apache.qpid.proton4j.amqp.driver.expectations;

import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Map;

import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedInteger;
import org.apache.qpid.proton4j.amqp.UnsignedShort;
import org.apache.qpid.proton4j.amqp.driver.AMQPTestDriver;
import org.apache.qpid.proton4j.amqp.driver.actions.BeginInjectAction;
import org.apache.qpid.proton4j.amqp.driver.codec.ListDescribedType;
import org.apache.qpid.proton4j.amqp.driver.codec.transport.Begin;
import org.apache.qpid.proton4j.amqp.driver.matchers.transport.BeginMatcher;
import org.apache.qpid.proton4j.buffer.ProtonBuffer;
import org.hamcrest.Matcher;

/**
 * Scripted expectation for the AMQP Begin performative
 */
public class BeginExpectation extends AbstractExpectation<Begin> {

    private final BeginMatcher matcher = new BeginMatcher();

    private BeginInjectAction response;

    public BeginExpectation(AMQPTestDriver driver) {
        super(driver);
    }

    @Override
    public BeginExpectation onChannel(int channel) {
        super.onChannel(channel);
        return this;
    }

    public BeginInjectAction respond() {
        response = new BeginInjectAction();
        driver.addScriptedElement(response);
        return response;
    }

    //----- Handle the performative and configure response is told to respond

    @Override
    public void handleBegin(Begin begin, ProtonBuffer payload, int channel, AMQPTestDriver context) {
        super.handleBegin(begin, payload, channel, context);

        if (response == null) {
            return;
        }

        // Input was validated now populate response with auto values where not configured
        // to say otherwise by the test.
        if (response.onChannel() == BeginInjectAction.CHANNEL_UNSET) {
            // TODO - We could track session in the driver and therefore allocate
            //        free channels based on activity during the test.  For now we
            //        are simply mirroring the channels back.
            response.onChannel(channel);
        }

        // Populate the remote channel with that of the incoming begin unless scripted to send
        // otherwise which can indicate error handling test.
        if (response.getPerformative().getRemoteChannel() == null) {
            response.withRemoteChannel(channel);
        }

        if (response.getPerformative().getNextOutgoingId() == null) {
            response.withNextOutgoingId(begin.getNextOutgoingId());
        }
        if (response.getPerformative().getIncomingWindow() == null) {
            response.withIncomingWindow(begin.getIncomingWindow());
        }
        if (response.getPerformative().getOutgoingWindow() == null) {
            response.withOutgoingWindow(begin.getOutgoingWindow());
        }
        if (response.getPerformative().getHandleMax() == null) {
            response.withHandleMax(begin.getHandleMax());
        }

        // The remainder of the values are left not set unless set in the test script
    }

    //----- Type specific with methods that perform simple equals checks

    public BeginExpectation withRemoteChannel(int remoteChannel) {
        return withRemoteChannel(equalTo(UnsignedShort.valueOf((short) remoteChannel)));
    }

    public BeginExpectation withRemoteChannel(UnsignedShort remoteChannel) {
        return withRemoteChannel(equalTo(remoteChannel));
    }

    public BeginExpectation withNextOutgoingId(int nextOutgoingId) {
        return withNextOutgoingId(equalTo(UnsignedInteger.valueOf(nextOutgoingId)));
    }

    public BeginExpectation withNextOutgoingId(long nextOutgoingId) {
        return withNextOutgoingId(equalTo(UnsignedInteger.valueOf(nextOutgoingId)));
    }

    public BeginExpectation withNextOutgoingId(UnsignedInteger nextOutgoingId) {
        return withNextOutgoingId(equalTo(nextOutgoingId));
    }

    public BeginExpectation withIncomingWindow(int incomingWindow) {
        return withIncomingWindow(equalTo(UnsignedInteger.valueOf(incomingWindow)));
    }

    public BeginExpectation withIncomingWindow(long incomingWindow) {
        return withIncomingWindow(equalTo(UnsignedInteger.valueOf(incomingWindow)));
    }

    public BeginExpectation withIncomingWindow(UnsignedInteger incomingWindow) {
        return withIncomingWindow(equalTo(incomingWindow));
    }

    public BeginExpectation withOutgoingWindow(int outgoingWindow) {
        return withOutgoingWindow(equalTo(UnsignedInteger.valueOf(outgoingWindow)));
    }

    public BeginExpectation withOutgoingWindow(long outgoingWindow) {
        return withOutgoingWindow(equalTo(UnsignedInteger.valueOf(outgoingWindow)));
    }

    public BeginExpectation withOutgoingWindow(UnsignedInteger outgoingWindow) {
        return withOutgoingWindow(equalTo(outgoingWindow));
    }

    public BeginExpectation withHandleMax(int handleMax) {
        return withHandleMax(equalTo(UnsignedInteger.valueOf(handleMax)));
    }

    public BeginExpectation withHandleMax(long handleMax) {
        return withHandleMax(equalTo(UnsignedInteger.valueOf(handleMax)));
    }

    public BeginExpectation withHandleMax(UnsignedInteger handleMax) {
        return withHandleMax(equalTo(handleMax));
    }

    public BeginExpectation withOfferedCapabilities(Symbol... offeredCapabilities) {
        return withOfferedCapabilities(equalTo(offeredCapabilities));
    }

    public BeginExpectation withDesiredCapabilities(Symbol... desiredCapabilities) {
        return withDesiredCapabilities(equalTo(desiredCapabilities));
    }

    public BeginExpectation withProperties(Map<Symbol, Object> properties) {
        return withProperties(equalTo(properties));
    }

    //----- Matcher based with methods for more complex validation

    public BeginExpectation withRemoteChannel(Matcher<?> m) {
        matcher.addFieldMatcher(Begin.Field.REMOTE_CHANNEL, m);
        return this;
    }

    public BeginExpectation withNextOutgoingId(Matcher<?> m) {
        matcher.addFieldMatcher(Begin.Field.NEXT_OUTGOING_ID, m);
        return this;
    }

    public BeginExpectation withIncomingWindow(Matcher<?> m) {
        matcher.addFieldMatcher(Begin.Field.INCOMING_WINDOW, m);
        return this;
    }

    public BeginExpectation withOutgoingWindow(Matcher<?> m) {
        matcher.addFieldMatcher(Begin.Field.OUTGOING_WINDOW, m);
        return this;
    }

    public BeginExpectation withHandleMax(Matcher<?> m) {
        matcher.addFieldMatcher(Begin.Field.HANDLE_MAX, m);
        return this;
    }

    public BeginExpectation withOfferedCapabilities(Matcher<?> m) {
        matcher.addFieldMatcher(Begin.Field.OFFERED_CAPABILITIES, m);
        return this;
    }

    public BeginExpectation withDesiredCapabilities(Matcher<?> m) {
        matcher.addFieldMatcher(Begin.Field.DESIRED_CAPABILITIES, m);
        return this;
    }

    public BeginExpectation withProperties(Matcher<?> m) {
        matcher.addFieldMatcher(Begin.Field.PROPERTIES, m);
        return this;
    }

    @Override
    protected Matcher<ListDescribedType> getExpectationMatcher() {
        return matcher;
    }

    @Override
    protected Object getFieldValue(Begin begin, Enum<?> performativeField) {
        return begin.getFieldValue(performativeField.ordinal());
    }

    @Override
    protected Enum<?> getFieldEnum(int fieldIndex) {
        return Begin.Field.values()[fieldIndex];
    }

    @Override
    protected Class<Begin> getExpectedTypeClass() {
        return Begin.class;
    }
}
