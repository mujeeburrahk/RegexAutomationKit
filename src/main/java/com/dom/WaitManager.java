package com.dom;

import com.enums.ElementState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

public class WaitManager extends ElementHandler {
    private static final Logger log = LogManager.getLogger(WaitManager.class.getName());
    private static final int DEFAULT_WAIT_SECONDS = 5;
    private static WebDriverWait adaptiveWait;
    private static final ConcurrentHashMap<WebDriver, FluentWait<WebDriver>> fluentWaitMap = new ConcurrentHashMap<>();

    @SafeVarargs
    private static Wait<WebDriver> getFlexibleWait(LocatorModel locatorModel, int sec, Class<? extends Throwable>... exceptionClasses) {
        WebDriver driver = DriverManager.getDriverInstance(locatorModel.getLocatorPlatform());
        return fluentWaitMap.compute(driver, (key, wait) -> {
            if (wait == null || sec != DEFAULT_WAIT_SECONDS) {
                wait = new FluentWait<>(driver)
                        .withTimeout(Duration.ofSeconds(sec))
                        .pollingEvery(Duration.ofSeconds(1));
            } else {
                wait.withTimeout(Duration.ofSeconds(sec));
            }
            if (exceptionClasses != null) {
                for (Class<? extends Throwable> exceptionClass : exceptionClasses) {
                    wait = wait.ignoring(exceptionClass);
                }
            }
            return wait;
        });
    }

    private static WebDriverWait getAdaptiveWait(LocatorModel locatorModel) {
        if (adaptiveWait == null) {
            adaptiveWait = new WebDriverWait(DriverManager.getDriverInstance(locatorModel.getLocatorPlatform()), Duration.ofSeconds(DEFAULT_WAIT_SECONDS));
        }
        return adaptiveWait;
    }

    /**
     * Throws direct exception
     */
    private static ExpectedCondition<WebElement> elementPresent(LocatorModel locatorModel) {
        return ExpectedConditions.presenceOfElementLocated(getLocator(locatorModel.getLocatorType(), locatorModel.getLocatorValue()));
    }

    /**
     * Returns true if StaleElementReferenceException found
     */
    private static ExpectedCondition<Boolean> elementNotPresent(LocatorModel locatorModel) {
        return ExpectedConditions.stalenessOf(getElement(locatorModel, ElementState.NotPresent));
    }

    private static ExpectedCondition<WebElement> elementVisible(LocatorModel locatorModel) {
        return ExpectedConditions.visibilityOf(getElement(locatorModel, ElementState.Visible));
    }

    /**
     * Returns true if NoSuchElementException or StaleElementReferenceException found
     */
    private static ExpectedCondition<Boolean> elementInvisible(LocatorModel locatorModel) {
        return ExpectedConditions.invisibilityOf(getElement(locatorModel, ElementState.Invisible));
    }

    /**
     * Returns null if NoSuchElementException or StaleElementReferenceException found
     */
    private static ExpectedCondition<WebElement> elementClickable(LocatorModel locatorModel) {
        return ExpectedConditions.elementToBeClickable(getElement(locatorModel, ElementState.Clickable));
    }

    /**
     * To verify respective element state with web driver wait using static wait in seconds
     */
    public static boolean isElement(ElementState elementState, LocatorModel locatorModel) {
        log.info("Verifying '{}' locator to be: {}", locatorModel.getLocatorName() ,elementState);
        boolean state = false;
        try {
            switch (elementState) {
                case Present:
                    state = getAdaptiveWait(locatorModel).until(elementPresent(locatorModel)).isDisplayed();
                    break;
                case NotPresent:
                    state = getAdaptiveWait(locatorModel).until(elementNotPresent(locatorModel));
                    break;
                case Visible:
                    state = getAdaptiveWait(locatorModel).until(elementVisible(locatorModel)).isDisplayed();
                    break;
                case Invisible:
                    state = getAdaptiveWait(locatorModel).until(elementInvisible(locatorModel));
                    break;
                case Enabled:
                case Clickable:
                    state = getAdaptiveWait(locatorModel).until(elementClickable(locatorModel)).isEnabled();
                    break;
                case Disabled:
                    state = !getAdaptiveWait(locatorModel).until(elementClickable(locatorModel)).isEnabled();
                    break;
            }
        } catch (Exception e) {
            state = stepDown(e, locatorModel, elementState);
        }
        log.info("{} locator verification done!", locatorModel.getLocatorName());
        return state;
    }

    /**
     * To verify respective element state with fluent wait using dynamic wait in seconds
     */
    public static boolean isElement(ElementState elementState, LocatorModel locatorModel, int sec) {
        log.info("Verifying '{}' locator to be: {}", locatorModel.getLocatorName() ,elementState);
        boolean state = false;
        try {
            switch (elementState) {
                case Present:
                    state = getFlexibleWait(locatorModel, sec, NoSuchElementException.class)
                            .until(elementPresent(locatorModel)).isDisplayed();
                    break;
                case NotPresent:
                    state = getFlexibleWait(locatorModel, sec, (Class<? extends Throwable>) null)
                            .until(elementNotPresent(locatorModel));
                    break;
                case Visible:
                    state = getFlexibleWait(locatorModel, sec, StaleElementReferenceException.class)
                            .until(elementVisible(locatorModel)).isDisplayed();
                    break;
                case Invisible:
                    state = getFlexibleWait(locatorModel, sec, (Class<? extends Throwable>) null)
                            .until(elementInvisible(locatorModel));
                    break;
                case Enabled:
                case Clickable:
                    state = getFlexibleWait(locatorModel, sec, ElementNotInteractableException.class)
                            .until(elementClickable(locatorModel)).isEnabled();
                    break;
                case Disabled:
                    state = !getFlexibleWait(locatorModel, sec, ElementNotInteractableException.class)
                            .until(elementClickable(locatorModel)).isEnabled();
                    break;
            }
        } catch (Exception e) {
            state = stepDown(e, locatorModel, elementState);
        }
        log.info("{} locator verification done!", locatorModel.getLocatorName());
        return state;
    }
}