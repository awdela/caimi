package com.caimi.util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ResourceUtil {

    public static List<URL> loadResource(String source, ClassLoader classLoaders) throws IOException {
        List<URL> urls = new ArrayList<>();
        Enumeration<URL> urlElem = classLoaders.getResources(source);
        while (urlElem.hasMoreElements()) {
            URL url = urlElem.nextElement();
            if (!urls.contains(url)) {
                urls.add(url);
            }
        }
        urlElem = classLoaders.getResources("/" + source);
        while (urlElem.hasMoreElements()) {
            URL url = urlElem.nextElement();
            if (!urls.contains(url)) {
                urls.add(url);
            }
        }
        return urls;
    }

}
