package com.gd.networks;

import com.gd.sql.IpNetworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class GetNetworksByIpProcessor {

    @Autowired
    private IpNetworkRepository ipNetworkRepository;

    public List<String> getNetworksContainingIp(String ip) {
        // first convert into bytes
        byte[] bytesIp = Util.convertStringIpToBytes(ip);

        if (bytesIp == null) {
            throw new IllegalStateException("invalid ip="+ip);
        }
        Collection<byte[]> networksIpBelongsTo = ipNetworkRepository.findNetworksIpBelongsTo(bytesIp);

        //convert the result from bytes into Strings
        return networksIpBelongsTo.stream()
                .map(Util::mapToIpAndMask)
                .filter(Objects::nonNull)
                .map(pair -> pair.getLeft() + "/" + pair.getRight())
                .collect(Collectors.toList());
    }
}
