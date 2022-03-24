package com.kittelson;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                PrometheusMeterRegistry prometheusMeterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
                new JvmMemoryMetrics().bindTo(prometheusMeterRegistry);
//                new JvmGcMetrics().bindTo(prometheusMeterRegistry);
//                new ClassLoaderMetrics().bindTo(prometheusMeterRegistry);

                TimedAspect timedAspect = new TimedAspect(prometheusMeterRegistry);

                bind(MeterRegistry.class).toInstance(prometheusMeterRegistry);
                bind(PrometheusMeterRegistry.class).toInstance(prometheusMeterRegistry);
                bind(TimedAspect.class).toInstance(timedAspect);
            }
        });
        injector.getInstance(Api.class).startServer();

        Logic logic = injector.getInstance(Logic.class);
        while (true) {
            logic.randomRunner();
        }
    }
}
