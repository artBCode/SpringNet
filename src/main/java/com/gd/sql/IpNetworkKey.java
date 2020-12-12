package com.gd.sql;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Arrays;

@Embeddable
public class IpNetworkKey implements Serializable {
    private byte[] ip;
    private byte[] network;

    public IpNetworkKey(byte[] ip, byte[] network) {
        this.ip = ip;
        this.network = network;
    }

    public IpNetworkKey() {
    }

    public byte[] getIp() {
        return ip;
    }

    public void setIp(byte[] ip) {
        this.ip = ip;
    }

    public byte[] getNetwork() {
        return network;
    }

    public void setNetwork(byte[] network) {
        this.network = network;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IpNetworkKey)) return false;
        IpNetworkKey that = (IpNetworkKey) o;
        return Arrays.equals(getIp(), that.getIp()) &&
                Arrays.equals(getNetwork(), that.getNetwork());
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(getIp());
        result = 31 * result + Arrays.hashCode(getNetwork());
        return result;
    }

    @Override
    public String toString() {
        return "IpNetworkKey{" +
                "ip=" + Arrays.toString(ip) +
                ", network=" + Arrays.toString(network) +
                '}';
    }
}
