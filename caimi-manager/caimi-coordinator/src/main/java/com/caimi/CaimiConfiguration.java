package com.caimi;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.Primary;
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

import com.caimi.util.concurrent.DefaultThreadFactory;
import com.caimi.util.concurrent.SequentialThreadedProcessor;
import com.caimi.util.concurrent.SequentialThreadedProcessorImpl;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableScheduling
@EnableTransactionManagement
@EnableLoadTimeWeaving
@EnableJpaRepositories(basePackages = { "caimi.web.repository.entity" })
@EnableAsync
@EnableCaching
@ServletComponentScan(value = { "com.caimi.api.v1" })
@ComponentScan(value = { "com.caimi.api.v1", "caimi.web.service", "com.caimi.service.elasticsearch" })
// @EnableAspectJAutoProxy(proxyTargetClass = true)
public class CaimiConfiguration
        implements WebMvcConfigurer, SchedulingConfigurer, AsyncConfigurer, AsyncUncaughtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CaimiConfiguration.class);

    @Value("${server.host}")
    private String host;

    @Value("${server.port}")
    private int server_port;

    @Value("${database.user}")
    private String database_user;

    @Value("${database.password}")
    private String database_password;

    @Value("${database.pool.min}")
    private int database_pool_min;

    @Value("${database.pool.max}")
    private int database_pool_max;

    @Value("${database.pool.max-idle}")
    private int database_pool_max_idle;

    @Value("${redis.port}")
    private int redis_port;

    @Value("${redis.pool.max-active}")
    private int redis_pool_max_active;

    @Value("${redis.pool.max-wait}")
    private int redis_pool_max_wait;

    @Value("${redis.pool.max-idle}")
    private int redis_pool_max_idle;

    @Value("${redis.pool.min-idle}")
    private int redis_pool_min_idle;

    @Value("${redis.timeout}")
    private int redis_timeout;

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
        jettyContainer.setPort(server_port);
        jettyContainer.setSelectors(2);
        jettyContainer.setAcceptors(1);
        jettyContainer.setThreadPool(new ExecutorThreadPool(executorService()));
        return jettyContainer;
    }

    @Bean(name = "dataSource")
    public DataSource dataSource() throws Exception {
        ComboPooledDataSource ds = new ComboPooledDataSource();
        ds.setDriverClass("com.mysql.cj.jdbc.Driver");
        ds.setJdbcUrl(
                "jdbc:mysql://127.0.0.1:3306/caimi?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=GMT%2b08%3a00");
        ds.setUser(database_user);
        ds.setPassword(database_password);
        ds.setMinPoolSize(database_pool_min);
        ds.setMaxPoolSize(database_pool_max);
        ds.setMaxIdleTime(database_pool_max_idle);
        return ds;
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

    @Bean
    public java.util.concurrent.ScheduledThreadPoolExecutor scheduledExecutorService() {
        return taskScheduler;
    }

    @Primary
    @Bean
    public java.util.concurrent.ExecutorService executorService() {
        return asyncExecutor;
    }

    @Bean
    public SequentialThreadedProcessor threadedProcessor() {
        return threadedProcessor;
    }

    @Bean
    public JedisPool jedisPoolFactory() {
        logger.info("redisPool config: " + host + " " + redis_port);
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(redis_pool_max_idle);
        jedisPoolConfig.setMaxWaitMillis(redis_pool_max_wait);

        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, redis_port, redis_timeout);

        return jedisPool;
    }

    private void creatThreadPools() {
        taskScheduler = new ScheduledThreadPoolExecutor(5, new DefaultThreadFactory("scheduler"));
        asyncExecutor = new ThreadPoolExecutor(1000, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(), new DefaultThreadFactory("async"));
        asyncExecutor.allowCoreThreadTimeOut(true);
    }

    // --------------------------WebMvc Configuration----------------------------

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // http
        @SuppressWarnings("rawtypes")
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

    // -----------------------------SchedulingConfigurer---------------------------

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler);
    }

    // -------------------------------AsyncConfigurer-------------------------------

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        logger.error("Async invocation on " + method + " " + Arrays.asList(params) + "failed: " + ex.toString(), ex);
    }

    @Override
    public Executor getAsyncExecutor() {
        return asyncExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return this;
    }

    // ---------------------------Not Implement Method------------------------------

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
    }

    @Override
    public Validator getValidator() {
        return null;
    }

    @Override
    public MessageCodesResolver getMessageCodesResolver() {
        return null;
    }

}
