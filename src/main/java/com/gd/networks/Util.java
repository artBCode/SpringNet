package com.gd.networks;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    private static final Logger logger = LoggerFactory.getLogger(Util.class);
    static final Pattern IPV_4_6_NETWORK_SCREENING_PATTERN = Pattern.compile("([\\da-fA-F:.]+)/(\\d{1,3})");


    private Util() {
        throw new UnsupportedOperationException("no constructor for you");
    }

    public static byte[] convertStringIpToBytes(String strIp) {
        if (Strings.isBlank(strIp)) {
            return null;
        }
        IPAddressString addrStr = new IPAddressString(strIp);
        if (!addrStr.isValid()) {
            return null;
        }
        IPAddress addr = addrStr.getAddress();
        return addr.getBytes();

    }

    public static byte[] mapNetworkToBytes(String potentialIp, String potentialMask) {
        if (Strings.isBlank(potentialIp) || Strings.isBlank(potentialMask)) {
            return null;
        }
        IPAddressString addrStr = new IPAddressString(potentialIp);
        if (!addrStr.isValid()) {
            return null;
        }

        Integer mask;

        try {
            mask = Integer.valueOf(potentialMask);
        } catch (Exception exception) {
            return null;
        }

        //appropriate mask for ipv4
        if (addrStr.isIPv4() && mask <= 32 && mask >= 0) {
            return binaryIpAndBinaryMask(addrStr.getAddress().getBytes(), mask);
        }
        if (addrStr.isIPv6() && mask <= 128 && mask >= 0) {
            return binaryIpAndBinaryMask(addrStr.getAddress().getBytes(), mask);
        }
        return null;
    }

    private static byte[] binaryIpAndBinaryMask(byte[] binaryIp, int mask) {
        byte lastSignificant = (byte) (mask & 0xFF);
        byte nextSignificant = (byte) ((mask >> 8) & 0xFF);

        return ArrayUtils.addAll(binaryIp, nextSignificant, lastSignificant);

    }

    public static Pair<String, Integer> mapToIpAndMask(byte[] ipMask) {
        if (ipMask.length < 3) {
            return null;
        }
        // the last 2 bytes represent the mask
        int mask = (ipMask[ipMask.length - 1] & 0xFF) | (ipMask[ipMask.length - 2] & 0xFF) << 8;

        // the remaining ones are the ip
        try {
            InetAddress inetAddress = InetAddress.getByAddress(ArrayUtils.subarray(ipMask, 0, ipMask.length - 2));
            return Pair.of(inetAddress.getHostAddress(), mask);
        } catch (UnknownHostException e) {
            logger.warn("cannot reconstruct the ip from {}", ipMask, e);
            return null;
        }
    }

    public static boolean isValidIpAndMask(String ipMask){
        Matcher matcher = IPV_4_6_NETWORK_SCREENING_PATTERN.matcher(ipMask);
        if(!matcher.matches()){
            return false;
        }
        // TODO make this check less expensive
        return mapNetworkToBytes(matcher.group(1), matcher.group(2)) != null;

    }

    public static boolean belongsToNet(String ip, String subnet) {
        if (Strings.isBlank(ip) || Strings.isBlank(subnet)) {
            return false;
        }
        IpAddressMatcher ipAddressMatcher = new IpAddressMatcher(subnet);
        return ipAddressMatcher.matches(ip);
    }
}
