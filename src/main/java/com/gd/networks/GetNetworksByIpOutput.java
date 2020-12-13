package com.gd.networks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetNetworksByIpOutput {

    public String render(List<String> networksContainingIp) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(networksContainingIp);
        } catch (JsonProcessingException e) {
            return e.toString();
        }
    }
}
