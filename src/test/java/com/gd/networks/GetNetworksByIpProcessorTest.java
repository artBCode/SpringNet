package com.gd.networks;

import com.gd.sql.IpNetwork;
import com.gd.sql.IpNetworkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class GetNetworksByIpProcessorTest {
    private final GetNetworksByIpProcessor getNetworksByIpProcessor = new GetNetworksByIpProcessor();
    private IpNetworkRepository ipNetworkRepository;

    @BeforeEach
    void setUp() {
        ipNetworkRepository = Mockito.mock(IpNetworkRepository.class);
        getNetworksByIpProcessor.setIpNetworkRepository(ipNetworkRepository);
    }

    @Test
    void getNetworksContainingIp() {

        when(ipNetworkRepository.findNetworksIpBelongsTo(Util.convertStringIpToBytes("2001:0db8:0000:0000:0000:8a2e:0370:7334")))
                .thenReturn(Collections.singletonList(Util.mapNetworkToBytes("2001:0db8:0000:0000:0000:0000:0000:0000", "64")));

        List<String> networksContainingIp = getNetworksByIpProcessor.getNetworksContainingIp("2001:0db8:0000:0000:0000:8a2e:0370:7334");


        assertThat(networksContainingIp).containsExactlyInAnyOrder(
                "2001:db8:0:0:0:0:0:0/64"
        );
    }
}