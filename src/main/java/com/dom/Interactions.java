package com.dom;

import com.enums.ElementState;
import com.enums.LocatorPlatform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import java.util.HashMap;

public class Interactions extends WaitManager {
    private static final Logger log = LogManager.getLogger(Interactions.class.getName());

    public static void enterText(LocatorModel locatorModel, String text, boolean clearText) throws InvalidElementStateException {
        log.info("Entering {} text by clearing the {} locator field", text, locatorModel.getLocatorName());
        boolean fieldState = isElement(ElementState.Enabled, locatorModel);
        if (fieldState) {
            if (clearText)
                getElement(locatorModel, ElementState.Enabled).clear();
            getElement(locatorModel, ElementState.Enabled).sendKeys(text);
            log.info("{} text entered successfully!", text);
        } else
            throw new InvalidElementStateException("Text field is not intractable");
    }

    public static void click(LocatorModel locatorModel) throws ElementClickInterceptedException {
        log.info("Clicking on {} locator", locatorModel.getLocatorName());
        boolean eleState = isElement(ElementState.Clickable, locatorModel);
        if (eleState) {
            getElement(locatorModel, ElementState.Clickable).click();
            log.info("{} locator clicked successfully!", locatorModel.getLocatorName());
        } else
            throw new ElementClickInterceptedException("Element is not intractable");
    }

    public static class UserActions {
        private static Actions actions;

        private static Actions getActions(LocatorPlatform locatorPlatform) {
            if (actions == null) {
                actions = new Actions(DriverManager.getDriverInstance(locatorPlatform));
                log.info("{} actions initialized successfully!", locatorPlatform);
            }
            return actions;
        }

        private static HashMap<String, Integer> getCenterPoints(LocatorModel locatorModel) {
            Point location = getElement(locatorModel, ElementState.Present).getLocation();
            Dimension size = getElement(locatorModel, ElementState.Present).getSize();
            return new HashMap<>() {
                {
                    put("centerX", location.getX() + size.getWidth() / 2);
                    put("centerY", location.getY() + size.getHeight() / 2);
                }
            };
        }

        public static void enterText(LocatorModel locatorModel, String text) throws InvalidElementStateException {
            log.info("Entering {} text into {} locator field", text, locatorModel.getLocatorName());
            boolean fieldState = isElement(ElementState.Enabled, locatorModel);
            if (fieldState) {
                getActions(locatorModel.getLocatorPlatform())
                        .sendKeys(getElement(locatorModel, ElementState.Enabled), text)
                        .build().perform();
                log.info("{} text entered successfully!", text);
            } else
                throw new InvalidElementStateException("Text field is not intractable");
        }

        public static void click(LocatorModel locatorModel) throws ElementClickInterceptedException {
            log.info("Clicking on {} locator", locatorModel.getLocatorName());
            boolean eleState = isElement(ElementState.Clickable, locatorModel);
            if (eleState) {
                getActions(locatorModel.getLocatorPlatform())
                        .click(getElement(locatorModel, ElementState.Clickable))
                        .build().perform();
                log.info("{} locator clicked successfully!", locatorModel.getLocatorName());
            } else
                throw new ElementClickInterceptedException("Element is not intractable");
        }

        public static void doubleClick(LocatorModel locatorModel) throws ElementClickInterceptedException {
            log.info("Double clicking on {} locator", locatorModel.getLocatorName());
            boolean eleState = isElement(ElementState.Clickable, locatorModel);
            if (eleState) {
                getActions(locatorModel.getLocatorPlatform())
                        .doubleClick(getElement(locatorModel, ElementState.Clickable))
                        .build().perform();
                log.info("{} locator double clicked successfully!", locatorModel.getLocatorName());
            } else
                throw new ElementClickInterceptedException("Element is not intractable");
        }

        public static void rightClick(LocatorModel locatorModel) throws ElementClickInterceptedException {
            log.info("Right clicking on {} locator", locatorModel.getLocatorName());
            boolean eleState = isElement(ElementState.Clickable, locatorModel);
            if (eleState) {
                getActions(locatorModel.getLocatorPlatform())
                        .contextClick(getElement(locatorModel, ElementState.Clickable))
                        .build().perform();
                log.info("{} locator right clicked successfully!", locatorModel.getLocatorName());
            } else
                throw new ElementClickInterceptedException("Element is not intractable");
        }

        public static void dragAndDrop(LocatorModel sourceLocatorModel, LocatorModel destLocatorModel) throws ElementClickInterceptedException {
            log.info("Dragging {} source locator to {} destination locator", sourceLocatorModel.getLocatorName(), destLocatorModel.getLocatorName());
            boolean sourceEleState = isElement(ElementState.Clickable, sourceLocatorModel);
            boolean destEleState = isElement(ElementState.Clickable, sourceLocatorModel);
            if (sourceEleState || destEleState) {
                getActions(sourceLocatorModel.getLocatorPlatform())
                        .dragAndDrop(getElement(sourceLocatorModel, ElementState.Clickable), getElement(destLocatorModel, ElementState.Clickable))
                        .build().perform();
                log.info("{} locator got dragged successfully!", sourceLocatorModel.getLocatorName());
            } else
                throw new ElementClickInterceptedException("Element is not intractable");
        }

        // Todo: Need to implement additional actions flows
    }

    public static class JSExecutor {
        private static JavascriptExecutor jsExecutor;

        private static JavascriptExecutor getJsExecutor() {
            if (jsExecutor == null) {
                DriverManager.setScriptTimeOut(5);
                jsExecutor = DriverManager.getDriverInstance(LocatorPlatform.WEB);
                log.info("Javascript executor initialized successfully for WEB");
            }
            return jsExecutor;
        }

        public static void enterText(LocatorModel locatorModel, String text) throws InvalidElementStateException {
            log.info("Sending {} text into {} locator field using JSExecutor", text, locatorModel.getLocatorName());
            boolean fieldState = isElement(ElementState.Enabled, locatorModel);
            if (fieldState) {
                getJsExecutor().executeScript("arguments[0].value= '" + text + "';", getElement(locatorModel, ElementState.Enabled));
                log.info("{} text sent successfully!", text);
            } else
                throw new InvalidElementStateException("Text field is not intractable");
        }

        public static void click(LocatorModel locatorModel) throws ElementClickInterceptedException {
            log.info("Clicking on {} locator using JSExecutor", locatorModel.getLocatorName());
            boolean eleState = isElement(ElementState.Clickable, locatorModel);
            if (eleState) {
                getJsExecutor().executeScript("arguments[0].click();", getElement(locatorModel, ElementState.Clickable));
                log.info("{} locator clicked successfully!", locatorModel.getLocatorName());
            } else
                throw new ElementClickInterceptedException("Element is not intractable");
        }

        public static String getText(LocatorModel locatorModel) throws InvalidElementStateException {
            log.info("Getting text of {} locator using JSExecutor", locatorModel.getLocatorName());
            boolean eleState = isElement(ElementState.Present, locatorModel);
            String text;
            if (eleState) {
                text = getJsExecutor().executeScript("return arguments[0].innerHTML;", getElement(locatorModel, ElementState.Present)).toString();
                log.info("{} locator text fetched successfully!", locatorModel.getLocatorName());
            } else
                throw new InvalidElementStateException("Argument field is not intractable");
            return text;
        }

        public static void touchAndHold(LocatorModel locatorModel, int duration){
            log.info("Touching and holding {} locator for {} seconds", locatorModel.getLocatorName(), duration);
            boolean eleState = isElement(ElementState.Present, locatorModel);
            if (eleState){
                DriverManager.getDriverInstance(locatorModel.getLocatorPlatform()).executeScript("mobile: touchAndHold",
                        new HashMap<String, Object>(){
                            {
                                put("element", getElement(locatorModel, ElementState.Clickable));
                                put("duration", duration);
                            }
                        });
                log.info("{} locator got touch and hold successfully!", locatorModel.getLocatorName());
            } else
                throw new InvalidElementStateException("Element is not intractable");
        }

        // Todo: Need to implement additional executor flows
    }
}