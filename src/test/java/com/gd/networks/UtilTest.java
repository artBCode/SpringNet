package com.gd.networks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    @Test
    void convertStringIpToByteArray() {
        assertNotNull(Util.convertStringIpToBytes("192.168.0.1"));
        assertNotNull(Util.convertStringIpToBytes("2001:0db8:0000:0000:0000:8a2e:0370:7334"));
        assertNotNull(Util.convertStringIpToBytes("2001:db8::8a2e:370:7334"));

        // bad ones
        assertNull(Util.convertStringIpToBytes("2001:db8::8a2l:370:7334"));
        assertNull(Util.convertStringIpToBytes("rubbish"));
        assertNull(Util.convertStringIpToBytes(""));
        assertNull(Util.convertStringIpToBytes(null));
    }


}