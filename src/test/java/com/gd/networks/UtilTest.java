package com.gd.networks;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    @Test
    void convertStringIpToByteArray() {
        byte[] bytes = Util.convertStringIpToBytes("192.168.0.1");
        assertArrayEquals(new byte[]{(byte) -64, (byte) -88, 0, 1}, bytes);

        bytes = Util.convertStringIpToBytes("2001:0db8:0000:0000:0000:8a2e:0370:7334");
        assertArrayEquals(new byte[]{32, 1, 13, -72, 0, 0, 0, 0, 0, 0, -118, 46, 3, 112, 115, 52}, bytes);

        bytes = Util.convertStringIpToBytes("2001:0db8::8a2e:0370:7334");
        assertArrayEquals(new byte[]{32, 1, 13, -72, 0, 0, 0, 0, 0, 0, -118, 46, 3, 112, 115, 52}, bytes);

        // invalid input
        assertNull(Util.convertStringIpToBytes("2001:db8::8a2l:370:7334"));
        assertNull(Util.convertStringIpToBytes("rubbish"));
        assertNull(Util.convertStringIpToBytes(""));
        assertNull(Util.convertStringIpToBytes(null));
    }


    @Test
    void mapNetworkToBytesAndReverse() {
        byte[] bytes = Util.mapNetworkToBytes("192.168.0.1", "24");
        assertArrayEquals(new byte[]{(byte) -64, (byte) -88, 0, 1, 0, 24}, bytes);
        Pair<String, Integer> ipMask = Util.mapToIpAndMask(bytes); // reverse operation
        assertEquals(Pair.of("192.168.0.1",24), ipMask);


        bytes = Util.mapNetworkToBytes("2001:0db8::8a2e:0370:7334","64");
        assertArrayEquals(new byte[]{32, 1, 13, -72, 0, 0, 0, 0, 0, 0, -118, 46, 3, 112, 115, 52, 0, 64}, bytes);
        ipMask = Util.mapToIpAndMask(bytes); // reverse operation
        assertEquals(Pair.of("2001:db8:0:0:0:8a2e:370:7334",64), ipMask);

        // invalid input
        assertNull(Util.mapNetworkToBytes(null, null));
        assertNull(Util.mapNetworkToBytes(null, "24"));
        assertNull(Util.mapNetworkToBytes("", "24"));
        assertNull(Util.mapNetworkToBytes("192.168.0.1", ""));
        assertNull(Util.mapNetworkToBytes("192.168.0.1", null));
    }

    @Test
    void belongsToNet() {
        assertTrue(Util.belongsToNet("192.168.0.1","192.168.0.1/24"));
        assertFalse(Util.belongsToNet("192.168.1.1","192.168.0.1/24"));
        assertTrue(Util.belongsToNet("2001:0db8::8a2e:0370:7334","2001:0db8::/32"));
        assertFalse(Util.belongsToNet("2001:0db9::8a2e:0370:7334","2001:0db8::/32"));

        // invalid input
        assertFalse(Util.belongsToNet(null, null));
        assertFalse(Util.belongsToNet("",""));
    }

}