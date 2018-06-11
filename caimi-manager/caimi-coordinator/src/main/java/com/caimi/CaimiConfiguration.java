package com.caimi;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import caimi.common.util.concurrent.DefaultThreadFactory;
import caimi.common.util.concurrent.SequentialThreadedProcessor;
import caimi.common.util.concurrent.SequentialThreadedProcessorImpl;

@Configuration
@EnableScheduling
@EnableTransactionManagement
@EnableLoadTimeWeaving
@EnableJpaRepositories(basePackages= {"caimi.web.repository.entity"})
@EnableAsync
@ServletComponentScan
@ComponentScan(value = { "caimi.web.service" })
public class CaimiConfiguration implements WebMvcConfigurer, SchedulingConfigurer, AsyncConfigurer, AsyncUncaughtExceptionHandler{
	
	private static final Logger logger = LoggerFactory.getLogger(CaimiConfiguration.class);

	private ScheduledThreadPoolExecutor taskScheduler;
	private ThreadPoolExecutor asyncExecutor;
	private SequentialThreadedProcessorImpl threadedProcessor;

	public CaimiConfiguration() {
		creatThreadPools();
	}

	// ---------------------------Beans----------------------------------

	@Bean
	public JettyEmbeddedServletContainerFactory jettyEmbeddedServletContainerFactory() {
		JettyEmbeddedServletContainerFactory jettyContainer = new JettyEmbeddedServletContainerFactory();
		jettyContainer.setPort(10081);
		jettyContainer.setSelectors(2);
		jettyContainer.setAcceptors(1);
		jettyContainer.setThreadPool(new ExecutorThreadPool(executorService()));
		return jettyContainer;
	}

	@Bean(name = "dataSource")
	@ConfigurationProperties("caimi.datasource")
	public DataSource dataSource() throws Exception {
		return DataSourceBuilder.create().type(com.mchange.v2.c3p0.ComboPooledDataSource.class).build();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws Exception {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan(new String[] { "caimi.web.repository.entity" });

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);

		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
		properties.setProperty("hibernate.default_schema", "caimi");
		properties.setProperty("hibernate.connection.autoReconnect", "true");

		String value = System.getProperty("hibernate.show_sql", "false");
		properties.setProperty("hibernate.show_sql", value);
		em.setJpaProperties(properties);

		return em;
	}
	@Bean(name = "transactionManager")
	public PlatformTransactionManager getTransactionManager(EntityManagerFactory emf) throws Exception {
		JpaTransactionManager transactionManager = new JpaTransactionManager(emf);
		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	@Primary
	@Bean
	public ScheduledThreadPoolExecutor scheduledExecutorService() {
		return taskScheduler;
	}

	@Primary
	@Bean
	public ExecutorService executorService() {
		return asyncExecutor;
	}

	@Primary
	@Bean
	public SequentialThreadedProcessor threadedProcessor() {
		return threadedProcessor;
	}

	private void creatThreadPools() {
		taskScheduler = new ScheduledThreadPoolExecutor(5, new DefaultThreadFactory("scheduler"));
		asyncExecutor = new ThreadPoolExecutor(1000, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(), new DefaultThreadFactory("async"));
		asyncExecutor.allowCoreThreadTimeOut(true);
	}

	// --------------------------WebMvc Configuration----------------------------
	
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		// ��Ϣת����
		// http
		HttpMessageConverter converter = new StringHttpMessageConverter();
		converters.add(converter);
		logger.info("HttpMessageCoverter added");
		// string
		converter = new FormHttpMessageConverter();
		converters.add(converter);
		logger.info("FormHttpMessageConverter added");
		// json
		converter = new GsonHttpMessageConverter();
		converters.add(converter);
		logger.info("GsonHttpMessageConverter added");
	}

	//-----------------------------SchedulingConfigurer---------------------------
	
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskScheduler);
	}
	
	//-------------------------------AsyncConfigurer-------------------------------

	public void handleUncaughtException(Throwable ex, Method method, Object... params) {
		logger.error("Async invocation on "+method+" "+Arrays.asList(params)+"failed: "+ex.toString(), ex);
	}

	public Executor getAsyncExecutor() {
		return asyncExecutor;
	}

	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return this;
	}
	
	// ---------------------------Not Implement Method------------------------------
	
	public void configurePathMatch(PathMatchConfigurer configurer) {
	}

	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
	}

	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
	}

	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
	}

	public void addFormatters(FormatterRegistry registry) {
	}

	public void addInterceptors(InterceptorRegistry registry) {
	}

	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	}

	public void addCorsMappings(CorsRegistry registry) {
	}

	public void addViewControllers(ViewControllerRegistry registry) {
	}

	public void configureViewResolvers(ViewResolverRegistry registry) {
	}

	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
	}

	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
	}

	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
	}

	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
	}

	public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
	}

	public Validator getValidator() {
		return null;
	}

	public MessageCodesResolver getMessageCodesResolver() {
		return null;
	}

}
