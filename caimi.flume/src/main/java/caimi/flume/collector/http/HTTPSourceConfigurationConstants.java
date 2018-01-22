package caimi.flume.collector.http;
/**
 *≈‰÷√–≈œ¢
 */
public class HTTPSourceConfigurationConstants {
	
	public static final String CONFIG_PORT = "port";
	public static final String CONFIG_HANDLER = "handler";
	public static final String CONFIG_HANDLER_PREFIX = 
			CONFIG_HANDLER + ".";
	public static final String CONFIG_BIND = "bind";
	
	public static final String DEFAULT_BIND = "0.0.0.0";
	public static final String DEFAULT_HANDLER= "org.apache.flume.source.http.JSONHandler";
	
	public static final String SSL_KETSTORE = "keystore";
	public static final String SSL_KETSTORE_PASSWD = "keystorePasswd";
	public static final String SSL_ENABLED = "enableSSL";
	
	public static final String EXCLUDE_PROTOCOLS = "excludeProtocols";

}
