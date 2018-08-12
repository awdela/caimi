package com.caimi.api.v1;

import java.io.IOException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.caimi.service.elasticsearch.ElasticSearchService;

/**
 * Restful WebService for ElasticSearch
 */
@RestController
public class ElasticSearchController {

    private Logger logger = LoggerFactory.getLogger(ElasticSearchController.class);

    public static final String URL_PREFIX = RestControllerConstants.URI_PREFIX + "/elasticsearch";

    @Autowired
    private ElasticSearchService esService;

    @RequestMapping(path = URL_PREFIX
            + "/nodes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getNodes() throws Exception {
        return esService.getNodesInfo();
    }

    @RequestMapping(path = URL_PREFIX
            + "/creat", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> creat(@RequestBody String contentString) {
        JSONObject json = new JSONObject(contentString);
        try {
            esService.creat(json);
            return ResponseEntity.ok("201");
        } catch (IOException e) {
            return ResponseEntity.ok("500");
        }
    }

    @RequestMapping(path = URL_PREFIX
            + "/get", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> get(@RequestBody String contentString) {
        JSONObject json = new JSONObject(contentString);
        String result = esService.search(json);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(path = URL_PREFIX
            + "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Long> update(@RequestBody String contentString) throws Exception {
        JSONObject json = new JSONObject(contentString);
        Long version = esService.update(json);
        return ResponseEntity.ok(version);
    }

}
