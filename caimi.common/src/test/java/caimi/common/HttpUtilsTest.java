package caimi.common;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import caimi.common.Utils.HttpUtils;

public class HttpUtilsTest {
	@Test
	public void HttpUtilsTest() throws IOException {
		String url = "http://192.168.122.1:8080/fcgi-bin/gettask.fcgi?vm_id=1&version=Lanysec_A";
		String url2 = "http://192.168.3.172:8980/opennms/api/v2/nodes";
		HttpUtils.httpGet(url2);
}
}
