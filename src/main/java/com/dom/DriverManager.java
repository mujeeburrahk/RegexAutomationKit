package com.dom;

import com.enums.LocatorPlatform;
import com.utils.AppiumUtils;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.time.Duration;

public class DriverManager extends AppiumUtils {
    private static final Logger log = LogManager.getLogger(DriverManager.class.getName());
    private static WebDriver webDriver;
    private static IOSDriver iosDriver;
    private static AndroidDriver androidDriver;

    public static void setWebDriver() {
        if (webDriver == null) {
            webDriver = new ChromeDriver();
            log.info("WebDriver initialized successfully!");
        }
    }

    public static void setIosDriver(DesiredCapabilities capabilities) {
        if (iosDriver == null) {
            iosDriver = new IOSDriver(startServer().getUrl(), capabilities);
            log.info("IOSDriver initialized successfully!");
        }
    }

    public static void setAndroidDriver(DesiredCapabilities capabilities) {
        if (androidDriver == null) {
            androidDriver = new AndroidDriver(startServer(), capabilities);
            log.info("AndroidDriver initialized successfully!");
        }
    }

    public static RemoteWebDriver getDriverInstance(LocatorPlatform locatorPlatform) {
        switch (locatorPlatform) {
            case WEB:
                if (webDriver == null)
                    throw new WebDriverException("WebDriver not yet initialised!");
                return (RemoteWebDriver) webDriver;
            case IOS:
                if (iosDriver == null)
                    throw new WebDriverException("IosDriver not yet initialised!");
                return iosDriver;
            case ANDROID:
                if (androidDriver == null)
                    throw new WebDriverException("Android Driver not yet initialised!");
                return androidDriver;
            default:
                throw new IllegalArgumentException("Invalid platform: " + locatorPlatform);
        }
    }

    public static void quitDriverInstance(LocatorPlatform locatorPlatform) {
        log.debug("Quiting {} driver instance", locatorPlatform);
        switch (locatorPlatform) {
            case WEB:
                if (webDriver != null)
                    webDriver.quit();
                break;
            case IOS:
                if (iosDriver != null) {
                    iosDriver.quit();
                    stopServer();
                }
                break;
            case ANDROID:
                if (androidDriver != null)
                    androidDriver.quit();
                break;
        }
    }

    public static void launchURL(String URL, boolean maximiseWindow){
        if (maximiseWindow){
            getDriverInstance(LocatorPlatform.WEB).manage().window().maximize();
        }
        getDriverInstance(LocatorPlatform.WEB).get(URL);
    }

    public static void setPageTimeOut(LocatorPlatform locatorPlatform, int sec) {
        getDriverInstance(locatorPlatform).manage().timeouts().pageLoadTimeout(Duration.ofSeconds(sec));
        log.info("{} page timeout set to {} seconds", locatorPlatform, sec);
    }

    public static void setDefaultTimeOut(LocatorPlatform locatorPlatform, int sec) {
        getDriverInstance(locatorPlatform).manage().timeouts().implicitlyWait(Duration.ofSeconds(sec));
        log.info("{} driver implicit wait timeout set to {} seconds", locatorPlatform, sec);
    }

    public static void setScriptTimeOut(int sec) {
        getDriverInstance(LocatorPlatform.WEB).manage().timeouts().scriptTimeout(Duration.ofSeconds(sec));
        log.info("WEB driver script timeout set to {} seconds", sec);
    }
}