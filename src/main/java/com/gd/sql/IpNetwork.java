package com.gd.sql;

import javax.persistence.*;
import java.util.Arrays;

/**
 * The ips and the networks are stored in binary format. This saves a lot of data.
 * By saving an IPv4 in binary we save up to 11 bytes per IP compared to the string representation. (15 - 4)
 * By saving an IPv6 in binary we save up to 23 bytes per IP compared to the string representation. (39 - 16)
 * The keys are VARBINARY because bigInteger data type only supports 2^64 different values. VARBINARY is also handy
 * when performing queries.
 */
@Entity
@IdClass(IpNetworkKey.class)
@Table(name = "ip_network")
public class IpNetwork {
    // a network has multiple IPs and an Ip can belong to more than one network
    @Id
    @Column(columnDefinition = "VARBINARY(16)")
    private byte[] ip;

    @Id
    @Column(columnDefinition = "VARBINARY(18)")
    private byte[] network;

    public IpNetwork(byte[] ip, byte[] network) {
        this.ip = ip;
        this.network = network;
    }

    public IpNetwork() {
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
        if (!(o instanceof IpNetwork)) return false;
        IpNetwork ipNetwork = (IpNetwork) o;
        return Arrays.equals(getIp(), ipNetwork.getIp()) &&
                Arrays.equals(getNetwork(), ipNetwork.getNetwork());
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(getIp());
        result = 31 * result + Arrays.hashCode(getNetwork());
        return result;
    }

    @Override
    public String toString() {
        return "IpNetwork{" +
                "ip=" + Arrays.toString(ip) +
                ", network=" + Arrays.toString(network) +
                '}';
    }
}
