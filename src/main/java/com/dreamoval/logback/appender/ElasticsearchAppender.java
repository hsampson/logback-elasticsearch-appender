package com.dreamoval.logback.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import io.searchbox.core.Index;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Henry Sampson
 */
public class ElasticsearchAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private ElasticseacrchConnectionSource connectionSource = null;
    private ExecutorService threadPool = Executors.newSingleThreadExecutor();

    @Override
    protected void append(ILoggingEvent eventObject) {
        threadPool.submit(new AppenderTask(eventObject));
        
    }

    public void setConnectionSource(ElasticseacrchConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
    }
    
    /**
     * Simple Callable class that insert the document into ElasticSearch
     */
    class AppenderTask implements Callable<ILoggingEvent> {

        ILoggingEvent loggingEvent;

        AppenderTask(ILoggingEvent loggingEvent) {
            this.loggingEvent = loggingEvent;
        }

        protected void writeBasic(Map<String, Object> json, ILoggingEvent event) {
            json.put("hostName", connectionSource.getHostName());
            json.put("applicationName", connectionSource.getApplicationName());
            json.put("timestamp", event.getTimeStamp());
            json.put("logger", event.getLoggerName());
            json.put("level", event.getLevel().toString());
            json.put("message", event.getMessage());
        }

        protected void writeThrowable(Map<String, Object> json, ILoggingEvent event) {
            IThrowableProxy ti = event.getThrowableProxy();
            if (ti != null) {
                json.put("className", ti.getClassName());
                json.put("stackTrace", getStackTrace(((ThrowableProxy)ti).getThrowable()));
            }
        }

        protected String getStackTrace(Throwable aThrowable) {
            final Writer result = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);
            aThrowable.printStackTrace(printWriter);
            return result.toString();
        }

        /**
         * Method is called by ExecutorService and insert the document into
         * ElasticSearch
         *
         * @return
         * @throws Exception
         */
        @Override
        public ILoggingEvent call() throws Exception {
            try {
                if (connectionSource.getClient() != null) {
                    // Set up the es index response 
                    String uuid = UUID.randomUUID().toString();
                    Map<String, Object> data = new HashMap<>();

                    writeBasic(data, loggingEvent);
                    writeThrowable(data, loggingEvent);
                    // insert the document into elasticsearch
                    Index index = new Index.Builder(data).index(connectionSource.getElasticIndex()).type(connectionSource.getElasticType()).id(uuid).build();
                    connectionSource.getClient().execute(index);
                }
            } catch (Throwable ex) {
                System.out.println("Unable to write log: " + ex.getLocalizedMessage());
            }
            return loggingEvent;
        }
    }
}
