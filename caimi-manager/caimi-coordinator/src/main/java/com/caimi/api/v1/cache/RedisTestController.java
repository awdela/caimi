package com.caimi.api.v1.cache;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.caimi.api.v1.RestControllerConstants;
import com.caimi.service.cache.RedisCacheManager;

@RestController
public class RedisTestController {

    @Autowired
    private RedisCacheManager redisClient;

    private static final String URL_PREFIX = RestControllerConstants.URI_Cache;

    @RequestMapping(path = URL_PREFIX
            + "/save",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> save(@RequestBody String jsonData) throws Exception {
        JSONObject json = new JSONObject(jsonData);
        String key = json.getString("key");
        String value = json.getString("value");
        boolean b = redisClient.set(key, value, -1);
        if (b) {
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.ok("fail");
        }
    }
}
