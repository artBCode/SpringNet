package com.gd.networks;

import org.springframework.stereotype.Component;

@Component
public class InsertNetworkOutput {
    private static final String OK_JSON = "{\"op-status\": \"done\"}";

    public String ok() {
        return OK_JSON;
    }

}
