package com.caimi.api.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.caimi.service.elasticsearch.ElasticSearchService;

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

}
