package com.quickstart.base.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StartupUrlLogger {

    private static final Logger log = LoggerFactory.getLogger(StartupUrlLogger.class);

    @EventListener(ApplicationReadyEvent.class)
    public void logUrls(ApplicationReadyEvent event) {
        if (!(event.getApplicationContext() instanceof WebServerApplicationContext webCtx)) {
            return;
        }
        Environment env = webCtx.getEnvironment();
        int port = webCtx.getWebServer().getPort();
        String basePath = normalizeContextPath(env.getProperty("server.servlet.context-path", "/"));
        String host = resolveHost(env);

        String backendUrl = "http://" + host + ":" + port + basePath;
        if (!backendUrl.endsWith("/")) {
            backendUrl += "/";
        }

        String docsUrl = "http://" + host + ":" + port + basePath + "/doc.html";

        log.info("后端服务: {}", backendUrl);
        log.info("接口文档(Knife4j): {}", docsUrl);
    }

    private static String normalizeContextPath(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String cp = raw.trim();
        if (!cp.startsWith("/")) {
            cp = "/" + cp;
        }
        if (cp.length() > 1 && cp.endsWith("/")) {
            cp = cp.substring(0, cp.length() - 1);
        }
        return "/".equals(cp) ? "" : cp;
    }

    private static String resolveHost(Environment env) {
        String addr = env.getProperty("server.address");
        if (addr == null || addr.isBlank() || "0.0.0.0".equals(addr) || "::".equals(addr)) {
            return "localhost";
        }
        return addr;
    }
}
