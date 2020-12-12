package com.gd.networks;

import java.util.Iterator;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class NetworkController {
    private static final String NETWORKS_KEY = "networks";
    private static final String ADDRESS_KEY = "address";
    private static final Pattern IPV_4_6_NETWORK_SCREENING_PATTERN = Pattern.compile("[\\da-fA-F:.]+/\\d{1,3}");

    private Logger logger = LoggerFactory.getLogger(NetworkController.class);



    @PostMapping(path = "/insert-network", consumes = "application/json", produces = "application/json")
    public String insertNetwork(@RequestBody String insertNetworkInput) {
        Optional<Object> jsonInput = validateAndConvertInsertNetworkInput(insertNetworkInput);

        if (jsonInput.isPresent()) {
            // for safety we first extract the networks
            List<JSONObject> collectedNetworks = new LinkedList<>();
            navigateJsonAndCollectNetworks(jsonInput.get(), collectedNetworks);

            // now we parse the IPs in each network
            Multimap<String, String> networkIps = networkIps(collectedNetworks);

            return networkIps.toString();
        }

        return insertNetworkInput;
    }


    private Optional<Object> validateAndConvertInsertNetworkInput(String input) {
        // only json objects and json arrays are accepted
        try {
            JSONObject jsonObject = new JSONObject(input);
            return Optional.of(jsonObject);
        } catch (JSONException err) {
            try {
                JSONArray jsonArray = new JSONArray(input);
                return Optional.of(jsonArray);
            } catch (JSONException err2) {
                logger.warn("Invalid InsertNetwork input. Not a jsonObj not jsonArray.", err);
                return Optional.empty();
            }
        }
    }

    private Multimap<String, String> networkIps(List<JSONObject> collectedNetworks) {
        Multimap<String, String> networkIps = HashMultimap.create();
        for (JSONObject collectedNetwork : collectedNetworks) {
            Iterator keys = collectedNetwork.keys();
            while (keys.hasNext()) {
                String ipMask = (String) keys.next();
                //todo check
                Object listAddresses = collectedNetwork.get(ipMask);
                if (listAddresses instanceof JSONArray) {
                    for (int i = 0; i < ((JSONArray) listAddresses).length(); i++) {
                        JSONObject addrDetails = ((JSONArray) listAddresses).getJSONObject(i);
                        if (addrDetails.has(ADDRESS_KEY)) {
                            networkIps.put(ipMask, addrDetails.getString(ADDRESS_KEY));
                        }
                    }
                } else {
                    logger.warn("incorrect network {} format", ipMask);
                }

            }
        }
        return networkIps;
    }

    /**
     * All the networks found in the {@code input} are accumulated in the {@code networks} list
     *
     * @param input    the input
     * @param networks a mutable list in which the networks will be stored.
     * @throws JSONException
     */
    private void navigateJsonAndCollectNetworks(Object input, List<JSONObject> networks) throws JSONException {

        if (input instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) input;
            Iterator<?> keys = jsonObject.keys();

            while (keys.hasNext()) {

                String key = (String) keys.next();
                Object value = jsonObject.get(key);
                if (NETWORKS_KEY.equalsIgnoreCase(key) && value instanceof JSONObject) {
                    networks.add((JSONObject) value);
                }

                if (!(value instanceof JSONArray)) {
                    if (value instanceof JSONObject) {
                        navigateJsonAndCollectNetworks(value, networks);
                    }
                } else {
                    navigateJsonAndCollectNetworks(new JSONArray(value.toString()), networks);
                }
            }
        }

        if (input instanceof JSONArray) {
            for (int i = 0; i < ((JSONArray) input).length(); i++) {
                JSONObject a = ((JSONArray) input).getJSONObject(i);
                navigateJsonAndCollectNetworks(a, networks);
            }
        }

    }
}
