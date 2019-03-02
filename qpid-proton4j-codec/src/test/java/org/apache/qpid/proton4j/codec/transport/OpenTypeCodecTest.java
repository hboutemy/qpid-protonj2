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
package org.apache.qpid.proton4j.codec.transport;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedInteger;
import org.apache.qpid.proton4j.amqp.transport.Open;
import org.apache.qpid.proton4j.buffer.ProtonBuffer;
import org.apache.qpid.proton4j.buffer.ProtonByteBufferAllocator;
import org.apache.qpid.proton4j.codec.CodecTestSupport;
import org.apache.qpid.proton4j.codec.legacy.LegacyCodecAdapter;
import org.apache.qpid.proton4j.codec.legacy.LegacyCodecSupport;
import org.apache.qpid.proton4j.codec.legacy.LegacyTypeAdapter;
import org.junit.Test;

public class OpenTypeCodecTest extends CodecTestSupport {

    @Test
    public void testEncodeAndDecode() throws IOException {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate();

        Symbol[] offeredCapabilities = new Symbol[] {Symbol.valueOf("Cap-1"), Symbol.valueOf("Cap-2")};
        Symbol[] desiredCapabilities = new Symbol[] {Symbol.valueOf("Cap-3"), Symbol.valueOf("Cap-4")};

        Open input = new Open();

        input.setContainerId("test");
        input.setHostname("localhost");
        input.setMaxFrameSize(UnsignedInteger.ONE);
        input.setIdleTimeOut(UnsignedInteger.ZERO);
        input.setOfferedCapabilities(offeredCapabilities);
        input.setDesiredCapabilities(desiredCapabilities);

        encoder.writeObject(buffer, encoderState, input);

        final Open result = (Open) decoder.readObject(buffer, decoderState);

        assertEquals("test", result.getContainerId());
        assertEquals("localhost", result.getHostname());
        assertEquals(UnsignedInteger.ONE, result.getMaxFrameSize());
        assertEquals(UnsignedInteger.ZERO, result.getIdleTimeOut());
        assertArrayEquals(offeredCapabilities, result.getOfferedCapabilities());
        assertArrayEquals(desiredCapabilities, result.getDesiredCapabilities());
    }

    @Test
    public void testEncodeUsingNewCodecAndDecodeWithLegacyCodec() throws Exception {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate();

        Symbol[] offeredCapabilities = new Symbol[] {Symbol.valueOf("Cap-1"), Symbol.valueOf("Cap-2")};
        Symbol[] desiredCapabilities = new Symbol[] {Symbol.valueOf("Cap-3"), Symbol.valueOf("Cap-4")};

        Open input = new Open();

        input.setContainerId("test");
        input.setHostname("localhost");
        input.setMaxFrameSize(UnsignedInteger.ONE);
        input.setIdleTimeOut(UnsignedInteger.ZERO);
        input.setOfferedCapabilities(offeredCapabilities);
        input.setDesiredCapabilities(desiredCapabilities);

        encoder.writeObject(buffer, encoderState, input);
        LegacyTypeAdapter<?, ?> result = legacyCodec.decodeLegacyType(buffer);

        assertNotNull(result);
        assertEquals(result, input);
    }

    @Test
    public void testEncodeUsingLegacyCodecAndDecodeWithNewCodec() throws Exception {
        Symbol[] offeredCapabilities = new Symbol[] {Symbol.valueOf("Cap-1"), Symbol.valueOf("Cap-2")};
        Symbol[] desiredCapabilities = new Symbol[] {Symbol.valueOf("Cap-3"), Symbol.valueOf("Cap-4")};

        Open input = new Open();

        input.setContainerId("test");
        input.setHostname("localhost");
        input.setMaxFrameSize(UnsignedInteger.ONE);
        input.setIdleTimeOut(UnsignedInteger.ZERO);
        input.setOfferedCapabilities(offeredCapabilities);
        input.setDesiredCapabilities(desiredCapabilities);

        org.apache.qpid.proton.amqp.transport.Open legacyOpen = LegacyCodecAdapter.transcribeToLegacyType(input);

        ProtonBuffer buffer = legacyCodec.encodeUsingLegacyEncoder(legacyOpen);
        assertNotNull(buffer);

        final Open result = (Open) decoder.readObject(buffer, decoderState);
        assertNotNull(result);

        assertTrue(LegacyCodecSupport.areEqual(legacyOpen, input));
        assertTrue(LegacyCodecSupport.areEqual(legacyOpen, result));
    }
}
