package com.utils;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.net.UrlChecker;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.io.File;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AppiumUtils {
    private static final Logger log = LogManager.getLogger(AppiumUtils.class);
    private static final int DEFAULT_APPIUM_PORT = 4723;
    private static final int MAX_STOP_ATTEMPTS = 5;
    private static AppiumDriverLocalService service;

    public static AppiumDriverLocalService startServer() {
        log.info("Starting Appium server");
        service = configureAppiumService();
        try {
            service.start();
            log.info("Appium server started at: {}", service.getUrl());
            waitForServerToStart(service);
        } catch (Exception e) {
            log.error("Failed to start Appium server: {}", e.getMessage());
            stopServer();
        }
        return service;
    }

    private static AppiumDriverLocalService configureAppiumService() {
        Map<String, String> env = new HashMap<>(System.getenv());
        env.put("PATH", "/usr/local/bin:" + env.get("PATH"));

        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability("noReset", "false");

        AppiumServiceBuilder builder = new AppiumServiceBuilder()
                .withIPAddress("127.0.0.1")
                .usingPort(DEFAULT_APPIUM_PORT)
                .withCapabilities(cap)
                .withEnvironment(env)
                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                .withArgument(GeneralServerFlag.LOCAL_TIMEZONE)
                .withArgument(GeneralServerFlag.LOG_LEVEL, "error")
                .withLogFile(new File("QualityInsights/AppiumServerLogs/Appium_Server_Log.txt"));

        return AppiumDriverLocalService.buildService(builder);
    }

    private static void waitForServerToStart(AppiumDriverLocalService service) throws MalformedURLException, UrlChecker.TimeoutException {
        log.info("Waiting for Appium server to start");
        URL status = new URL(service.getUrl() + "/sessions");
        new UrlChecker().waitUntilAvailable(5, TimeUnit.MINUTES, status);
        log.info("Appium server is up and running");
    }

    public static void stopServer() {
        if (service != null && service.isRunning()) {
            log.info("Stopping Appium server");
            service.stop();
            int attempts = 0;
            while (service.isRunning() && attempts < MAX_STOP_ATTEMPTS) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    attempts++;
                } catch (InterruptedException e) {
                    log.error("Thread interrupted while waiting for Appium server to stop: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
            if (service.isRunning()) {
                log.warn("Appium server could not be stopped after {} attempts", MAX_STOP_ATTEMPTS);
            } else {
                log.info("Appium server stopped");
            }
        }
    }

    public static boolean isServerRunning(int port) {
        log.info("Checking if Appium server is running");
        try (ServerSocket ignored = new ServerSocket(port)) {
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}