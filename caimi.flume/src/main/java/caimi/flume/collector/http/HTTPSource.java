package caimi.flume.collector.http;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.source.AbstractSource;
import org.mortbay.jetty.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import caimi.common.util.concurrent.DataProcessListener;
import caimi.common.util.concurrent.DataProcessor;
import caimi.common.util.concurrent.SequentialThreadedProcessor;

public class HTTPSource extends AbstractSource implements EventDrivenSource, Configurable, DataProcessor, DataProcessListener{

	private static final Logger LOG = LoggerFactory.getLogger(HTTPSource.class);
	
	private SequentialThreadedProcessor seqProcessor;
	private AsyncHTTPSourceHandler handler;
	
	private volatile Integer port;
	private volatile Server srv;
	private volatile String host;
	
	//SSL configuration variable
	private volatile String keyStorePath;
	private volatile String keyStorePasswd;
	private volatile Boolean sslEnable;
	private final List<String> excludedProtocols = new LinkedList<String>();

	
	public void configure(Context context) {
		//
		try {
			//ssl related config
			sslEnable = context.getBoolean(HTTPSourceConfigurationConstants.SSL_ENABLED,false);
			port = context.getInteger(HTTPSourceConfigurationConstants.CONFIG_PORT);
			host = context.getString(HTTPSourceConfigurationConstants.CONFIG_BIND, HTTPSourceConfigurationConstants.DEFAULT_BIND);
			Preconditions.checkState(host != null && !host.isEmpty(), "HTTPSource hostname specified is empty");
			Preconditions.checkNotNull(port, "HTTPSource requires a port number to be" + " specified");
			String handlerClassName = context.getString(HTTPSourceConfigurationConstants.CONFIG_HANDLER,
					HTTPSourceConfigurationConstants.DEFAULT_HANDLER).trim();
			if(sslEnable) {
				LOG.debug("SSL configuration enabled");
				keyStorePath = context.getString(HTTPSourceConfigurationConstants.SSL_KETSTORE);
                Preconditions.checkArgument(keyStorePath != null && !keyStorePath.isEmpty(),
                        "Keystore is required for SSL Conifguration");
                keyStorePasswd = context.getString(HTTPSourceConfigurationConstants.SSL_KETSTORE_PASSWD);
                Preconditions.checkArgument(keyStorePasswd != null,
                        "Keystore password is required for SSL Configuration");
                String excludeProtocolsStr = context.getString(HTTPSourceConfigurationConstants.EXCLUDE_PROTOCOLS);
                if (excludeProtocolsStr == null) {
                    excludedProtocols.add("SSLv3");
                } else {
                    excludedProtocols.addAll(Arrays.asList(excludeProtocolsStr.split(" ")));
                    if (!excludedProtocols.contains("SSLv3")) {
                        excludedProtocols.add("SSLv3");
                    }
                }
			}
		}catch(Exception ex) {
			
		}
	}

	@Override
	public synchronized void start() {
		super.start();
	}

	@Override
	public synchronized void stop() {
		super.stop();
	}

	public static class AsyncRemoteEvents implements AsyncRemoteInfo{
		private String remoteAddr;
		private String eventData;
		public AsyncRemoteEvents(String remoteAddr, String eventData) {
			this.remoteAddr = remoteAddr;
			this.eventData = eventData;
		}
		public String getRemoteAddr() {
			return remoteAddr;
		}
		public String getEventData() {
			return eventData;
		}
	}

	public Object processs(Object data) {
		AsyncRemoteEvents remoteRequest = (AsyncRemoteEvents) data;
		List<Event> events = null;
		return events;
	}

	public void onProcessFinished(Object data, Object result) {
		
	}

}
