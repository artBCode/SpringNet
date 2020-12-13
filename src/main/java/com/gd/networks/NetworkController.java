package com.gd.networks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This require that MySQL is running:
 * Assuming a docker container has been started with:
 * docker container stop network-db-container ; docker container rm network-db-container ; docker run -p 3306:3306 --name network-db-container -e MYSQL_ROOT_PASSWORD=network-pass -e MYSQL_DATABASE=db_network -e MYSQL_USER=springuser -e MYSQL_PASSWORD=SpringUserPassword -d mysql:8
 */
@RestController
public class NetworkController {
    private static final Logger logger = LoggerFactory.getLogger(NetworkController.class);

    @Autowired
    private InsertNetworkProcessor insertNetworkProcessor ;
    @Autowired
    private InsertNetworkOutput insertNetworkOutput ;

    @Autowired
    private GetNetworksByIpProcessor getNetworksByIpProcessor ;
    @Autowired
    private GetNetworksByIpOutput getNetworksByIpOutput ;


    @PostMapping(path = "/insert-network", consumes = "application/json", produces = "application/json")
    public String insertNetwork(@RequestBody String insertNetworkInput) {
        insertNetworkProcessor.addNetwork(insertNetworkInput);

        // if no exception print ok message
        return insertNetworkOutput.ok();
    }


    @GetMapping(path="/get-networks-by-ip", produces = "application/json")
    public String getNetworksByIP(@RequestParam(value = "IP", defaultValue = "") String ip) {
        List<String> networksContainingIp = getNetworksByIpProcessor.getNetworksContainingIp(ip);

        return getNetworksByIpOutput.render(networksContainingIp);


    }

}
