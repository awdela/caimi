package caimi.flume.collector.http;

import java.util.List;

import org.apache.flume.Event;
import org.apache.flume.conf.Configurable;

public interface AsyncHTTPSourceHandler extends Configurable{
	
	/**
	 * 异步处理事件
	 */
	public List<Event> processEvents(AsyncRemoteInfo remoteInfo, String eventData);

}
