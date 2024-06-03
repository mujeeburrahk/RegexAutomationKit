package com.dom;

import com.enums.ElementState;
import com.enums.LocatorType;
import io.appium.java_client.AppiumBy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;

public class ElementHandler extends CacheManager {
    private static final Logger log = LogManager.getLogger(ElementHandler.class.getName());

    public static By getLocator(LocatorType locatorType, String locatorValue) {
        log.debug("Getting {} locator for value: {}", locatorType, locatorValue);
        switch (locatorType) {
            case XPATH:
                return By.xpath(locatorValue);
            case CSS:
                return By.cssSelector(locatorValue);
            case NAME:
                return By.name(locatorValue);
            case TAG:
                return By.tagName(locatorValue);
            case TEXT:
                return By.linkText(locatorValue);
            case CHAIN:
                return AppiumBy.iOSClassChain(locatorValue);
            case PREDICATE:
                return AppiumBy.iOSNsPredicateString(locatorValue);
            case ACCESSIBILITY:
                return AppiumBy.accessibilityId(locatorValue);
            case ID:
                return By.id(locatorValue);
        }
        return null;
    }

    private static boolean edgeElementHandler(LocatorModel locatorModel) {
        log.error("Handling additional 5 seconds for element to be available in DOM");
        int sec = 5;
        WebDriver driver = DriverManager.getDriverInstance(locatorModel.getLocatorPlatform());
        By locator = getLocator(locatorModel.getLocatorType(), locatorModel.getLocatorValue());
        while (sec > 0) {
            try {
                Thread.sleep(1000);
                if (!driver.findElements(locator).isEmpty()) {
                    log.error("Successfully found element in DOM");
                    return true;
                }
                sec--;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static boolean handleException(Exception e, ElementState elementState) {
        log.error("Handling {} for ElementState: {}", e, elementState);
        if (e instanceof TimeoutException)
            return false;
        switch (elementState) {
            case Present:
            case Visible:
                return !(e instanceof NoSuchElementException);
            case NotPresent:
            case Invisible:
                return e instanceof NoSuchElementException || e instanceof StaleElementReferenceException;
            case Enabled:
                return !(e instanceof ElementNotInteractableException);
            case Disabled:
                return e instanceof InvalidElementStateException;
            case Clickable:
                return !(e instanceof ElementClickInterceptedException);
        }
        return true;
    }

    public static boolean stepDown(Exception e, LocatorModel locatorModel, ElementState elementState) {
        log.error("Stepping further down to handle locator: {}", locatorModel.getLocatorName());
        if (e instanceof StaleElementReferenceException)
            removeCacheLocator(locatorModel.getLocatorName());
        boolean state = handleException(e, elementState);
        if (!state) {
            return edgeElementHandler(locatorModel);
        }
        return false;
    }

    public static WebElement getElement(LocatorModel locatorModel, ElementState elementState) {
        String locatorName = locatorModel.getLocatorName();
        log.info("Getting {} locator element", locatorName);
        WebElement element = null;
        try {
            if (containsCacheLocator(locatorName)) {
                element = getCachedElement(locatorName);
                element.isDisplayed();
                return element;
            }
            element = DriverManager.getDriverInstance(locatorModel.getLocatorPlatform())
                    .findElement(getLocator(locatorModel.getLocatorType(), locatorModel.getLocatorValue()));
            cacheElement(locatorName, element);
            log.info("{} Locator element found successfully", locatorName);
        } catch (Exception e) {
            if (!stepDown(e, locatorModel, elementState))
                return getElement(locatorModel, elementState);
        }
        return element;
    }
}