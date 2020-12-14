package com.gd.networks;

import com.gd.sql.IpNetwork;
import com.gd.sql.IpNetworkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class InsertNetworkProcessorTest {
    private final InsertNetworkProcessor insertNetworkProcessor = new InsertNetworkProcessor();
    private IpNetworkRepository ipNetworkRepository;

    @BeforeEach
    void setUp() {
        ipNetworkRepository = Mockito.mock(IpNetworkRepository.class);
        insertNetworkProcessor.setIpNetworkRepository(ipNetworkRepository);
    }

    @Test
    void addNetwork() {
        insertNetworkProcessor.addNetwork(IP_NET);
        ArgumentCaptor<Iterable<IpNetwork>> iterableArgumentCaptor = ArgumentCaptor.forClass(Iterable.class);

        verify(ipNetworkRepository, times(1)).saveAll(iterableArgumentCaptor.capture());
        assertNotNull(iterableArgumentCaptor.getValue());
        List<IpNetwork> writtenIpNets= new LinkedList<>();
        iterableArgumentCaptor.getValue().forEach(writtenIpNets::add);

        assertThat(writtenIpNets).containsExactlyInAnyOrder(
                ipNetwork("2001:0db8:0000:0000:0000:8a2e:0370:7334", "2001:0db8:0000:0000:0000:0000:0000:0000", 64),
                ipNetwork("192.168.203.20", "192.168.203.0", 24)
                );
    }

    private IpNetwork ipNetwork(String ip, String netIp, int mask) {
        byte[] ipBytes = Util.convertStringIpToBytes(ip); // safe to use it here as it has already been tested
        byte[] netBytes = Util.mapNetworkToBytes(netIp, String.valueOf(mask)); // safe to use it here as it has already been tested
        return new IpNetwork(ipBytes, netBytes);
    }


    private static final String IP_NET = "{\"XPAR-2\": {\n" +
            "            \"security_level\": 0,\n" +
            "            \"networks\": {\n" +
            "                \"2001:0db8:0000:0000:0000:0000:0000:0000/64\": [\n" +
            "                    {\n" +
            "                        \"address\": \"2001:0db8:0000:0000:0000:8a2e:0370:7334\",\n" +
            "                        \"available\": true,\n" +
            "                        \"last_used\": \"30/12/20 17:00:00\"\n" +
            "                    }\n" +
            "                ],\n" +

            "                \"192.168.203.0/24\": [\n" +
            "                    {\n" +
            "                        \"address\": \"192.168.203.20\",\n" +
            "                        \"available\": true,\n" +
            "                        \"last_used\": \"30/01/20 17:00:00\"\n" +
            "                    }\n" +
            "                ],"+

            "                \"2001:0fff:rubbish/64\": [\n" + // bad mask
            "                    {\n" +
            "                        \"address\": \"2001:0db8:0000:0000:0000:8a2e:0370:7334\",\n" +
            "                        \"available\": true,\n" +
            "                        \"last_used\": \"30/12/20 17:00:00\"\n" +
            "                    }\n" +
            "                ],"+

            "                \"192.168.111.rubbish/24\": [\n" + // bad mask
            "                    {\n" +
            "                        \"address\": \"192.168.111.1\",\n" +
            "                        \"available\": true,\n" +
            "                        \"last_used\": \"30/12/20 17:00:00\"\n" +
            "                    }\n" +
            "                ],"+

            "                \"192.168.222.0\": [\n" + // no mask
            "                    {\n" +
            "                        \"address\": \"192.168.222.0\",\n" +
            "                        \"available\": true,\n" +
            "                        \"last_used\": \"30/12/20 17:00:00\"\n" +
            "                    }\n" +
            "                ],"+

            "                \"192.168.333.0/24\": [\n" +
            "                    {\n" +
            "                        \"address\": \"192.168.444.1\",\n" + // not belonging to that net
            "                        \"available\": true,\n" +
            "                        \"last_used\": \"30/12/20 17:00:00\"\n" +
            "                    }\n" +
            "                ],"+

            "                \"192.168.444.0/24\": [\n" +
            "                    {\n" +
            "                        \"address\": \"192.rubbish.444.1\",\n" + // badIp
            "                        \"available\": true,\n" +
            "                        \"last_used\": \"30/12/20 17:00:00\"\n" +
            "                    }\n" +
            "                ]"+

            "                }\n" +
            "        }}";
}