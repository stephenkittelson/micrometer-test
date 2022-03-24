package com.kittelson;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

@Log4j2
public class Api {
    private PrometheusMeterRegistry prometheusMeterRegistry;

    @Inject
    public Api(PrometheusMeterRegistry prometheusMeterRegistry) {
        this.prometheusMeterRegistry = prometheusMeterRegistry;
    }

    public void startServer() throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8085), 0);
        httpServer.createContext("/prometheus", httpExchange -> {
            String responseBody = prometheusMeterRegistry.scrape();
            httpExchange.sendResponseHeaders(200, responseBody.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream outputStream = httpExchange.getResponseBody()) {
                outputStream.write(responseBody.getBytes(StandardCharsets.UTF_8));
            }
        });
        new Thread(httpServer::start).start();
    }
}
