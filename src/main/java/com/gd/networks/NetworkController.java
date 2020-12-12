package com.gd.networks;

import com.gd.sql.*;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This require that MySQL is running:
 * Assuming a docker container has been started with:
 * docker container stop network-db-container ; docker container rm network-db-container ; docker run -p 3306:3306 --name network-db-container -e MYSQL_ROOT_PASSWORD=network-pass -e MYSQL_DATABASE=db_network -e MYSQL_USER=springuser -e MYSQL_PASSWORD=SpringUserPassword -d mysql:8
 */
@RestController
public class NetworkController {
    private static final Logger logger = LoggerFactory.getLogger(NetworkController.class);

    private static final String NETWORKS_KEY = "networks";
    private static final String ADDRESS_KEY = "address";
    private static final Pattern IPV_4_6_NETWORK_SCREENING_PATTERN = Pattern.compile("([\\da-fA-F:.]+)/(\\d{1,3})");

    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private IpNetworkRepository ipNetworkRepository;


    @PostMapping(path = "/insert-network", consumes = "application/json", produces = "application/json")
    public String insertNetwork(@RequestBody String insertNetworkInput) {
        Optional<Object> jsonInput = validateAndConvertInsertNetworkInput(insertNetworkInput);

        if (jsonInput.isPresent()) {
            // for safety we first extract the networks
            List<JSONObject> collectedNetworks = new LinkedList<>();
            navigateJsonAndCollectNetworks(jsonInput.get(), collectedNetworks);

            // now we parse the IPs in each network
            Multimap<String, String> networkIps = networkIps(collectedNetworks);

            //preparing what needs to be written in the DB
            List<IpNetwork> ipNetworks = new LinkedList<>();
            for (Map.Entry<String, Collection<String>> entry : networkIps.asMap().entrySet()) {
                String networkId = entry.getKey().trim();
                Matcher matcher = IPV_4_6_NETWORK_SCREENING_PATTERN.matcher(networkId);
                if (!matcher.matches()) {
                    continue;
                }
                String potentialIp = matcher.group(1);
                String potentialMask = matcher.group(2);
                byte[] binaryIpAndBynaryMask = Util.mapToNetworkToBytes(potentialIp, potentialMask);
                if (binaryIpAndBynaryMask == null) {
                    logger.warn("invalid {} network. Going to ignore it", networkId);
                    continue;
                }

                for (String ip : entry.getValue()) {
                    byte[] binaryIp = Util.convertStringIpToBytes(ip);
                    if (binaryIp != null) {
                        ipNetworks.add(new IpNetwork(binaryIp, binaryIpAndBynaryMask));
                    }
                }
            }

            // writing it all
            ipNetworkRepository.saveAll(ipNetworks);

            return "done";
        }

        return insertNetworkInput;
    }


    @GetMapping("/get-networks-by-ip")
    public String getNetworksByIP(@RequestParam(value = "IP", defaultValue = "") String ip) {
        // first convert into bytes
        byte[] bytesIp = Util.convertStringIpToBytes(ip);

        if (bytesIp == null) {
            return "invalid ip={}" + ip;
        }
        Collection<byte[]> networksIpBelongsTo = ipNetworkRepository.findNetworksIpBelongsTo(bytesIp);

        //convert the result from bytes into Strings
        return networksIpBelongsTo.stream()
                .map(Util::mapToIpAndMask)
                .filter(Objects::nonNull)
                .map(pair -> pair.getLeft() + "/" + pair.getRight())
                .collect(Collectors.joining(",\n"));

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
                if (!IPV_4_6_NETWORK_SCREENING_PATTERN.matcher(ipMask).matches()) {
                    continue;
                }
                Object listAddresses = collectedNetwork.get(ipMask);
                if (listAddresses instanceof JSONArray) {
                    for (int i = 0; i < ((JSONArray) listAddresses).length(); i++) {
                        JSONObject addrDetails = ((JSONArray) listAddresses).getJSONObject(i);
                        if (addrDetails.has(ADDRESS_KEY)) {
                            String ipString = addrDetails.getString(ADDRESS_KEY).trim();
                            byte[] binaryIp = Util.convertStringIpToBytes(ipString);
                            if (binaryIp != null && Util.belongsToNet(ipString, ipMask)) {
                                networkIps.put(ipMask, ipString);
                            }
                        }
                    }
                } else {
                    logger.warn("incorrect network {} format. Ignoring it", ipMask);
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
