package caimi.flume.collector.http;

import java.util.List;

import org.apache.flume.Event;
import org.apache.flume.conf.Configurable;

public interface AsyncHTTPSourceHandler extends Configurable{
	
	/**
	 * �첽�����¼�
	 */
	public List<Event> processEvents(AsyncRemoteInfo remoteInfo, String eventData);

}
