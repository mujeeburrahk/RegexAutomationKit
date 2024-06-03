# Project Overview:

This project provides a structured approach to managing API interactions, database operations, DOM manipulations, and
Appium server management through various packages.

## com.api

### Purpose:

Manages API interactions including sending requests, parsing responses, and handling request configurations.

#### Key Classes and Usage

```
APIClientManager:
    * Set the base URI: APIClientManager.setBaseURI("http://example.com")
    * Send a request: APIClientManager.sendRequest(requestModel, ResponseClass.class)
APIRequestModel:
    * Create a request model: new APIRequestModel(RequestType.POST, "/endpoint", requestBody, headers)
APIResponseModel:
    * Handle response data: responseModel.getStatusCode(), responseModel.getResponseBody()
```

## com.database

### Purpose:

Handles database interactions, including establishing connections, executing queries, and processing results.

#### Key Classes and Usage

```
DBClientManager:
    * Set up a connection: DBClientManager.setConnection(dbUrl, user, password)
    * Retrieve a connection: DBClientManager.getConnection()
    * Close connection pool: DBClientManager.closeConnectionPool()
DBHandler:
    * Execute a query: DBHandler.executeQuery(queryModel)
    * Process results: DBHandler.handleSelectQueryResult(resultSet)
QueryModel:
    * Create a query model: new QueryModel(QueryType.SELECT, "tableName")
    * Set query parameters: queryModel.setWhereCondition("id=1")
```

## com.dom

### Purpose:

Manages WebDriver instances, handles web elements, and performs various interactions in automated testing.

#### Key Classes and Usage

```
CacheManager:
    * Cache an element: cacheManager.cacheElement("locatorName", webElement)
    * Retrieve a cached element: cacheManager.getCachedElement("locatorName")
DriverManager:
    * Initialize WebDriver: DriverManager.setWebDriver()
    * Launch a URL: DriverManager.launchURL("http://example.com", true)
ElementHandler:
    * Locate an element: elementHandler.getElement(locatorModel, ElementState.Visible)
Interactions:
    * Interact with elements: interactions.click(locatorModel)
LocatorModel:
    * Create a locator model: new LocatorModel(LocatorPlatform.WEB, "locatorName", "locatorType", "locatorValue")  
WaitManager:
    * Verify element state: waitManager.isElement(ElementState.Clickable, locatorModel)    
```

## com.enums

### Purpose:

Defines various enums used for states, platforms, locator types, query types, and request types.

#### Key Enums

```
ElementState:
    Present, NotPresent, Visible, Invisible, Enabled, Disabled, Clickable
LocatorPlatform:
    WEB, IOS, ANDROID
LocatorType:
    XPATH, CSS, NAME, TAG, TEXT, CHAIN, PREDICATE, ACCESSIBILITY, ID
QueryType:
    SELECT, INSERT, UPDATE, DELETE
RequestType:
    GET, POST, PUT, DELETE
```

## com.utils

### Purpose:

Provides utility methods for managing the lifecycle of an Appium server.

#### Key Class and Usage

```
* AppiumUtils:
    * Start the server: AppiumUtils.startServer()
    * Stop the server: AppiumUtils.stopServer()
    * Check server status: AppiumUtils.isServerRunning(port)
```

## Integration Guide

This guide explains how to link child projects with this 'RegexAutomationKit' project using Maven. Follow these steps to
configure your pom.xml files and import the necessary packages.

### Step 1: Add Child Projects to this parent project pom.xml

```
<groupId>org.regexautokit</groupId>
<artifactId>RegexAutomateKit</artifactId>
<version>1.0.0</version>
<modules>
        <module>child-project-1</module>
        <module>child-project-2</module>
        <!-- Add more child projects as needed -->
</modules>
```

### Step 2: Configure Child Projects to use the parent project

```
<parent>
        <groupId>org.regexautokit</groupId>
        <artifactId>RegexAutomateKit</artifactId>
        <version>1.0.0</version>
</parent>
<groupId>com.yourcompany.child</groupId>
<artifactId>child-project-1</artifactId>
<version>1.0.0</version>
```

### Step 3: Import Packages in your Child Project

```
DriverManager.setWebDriver();
DriverManager.setPageTimeOut(LocatorPlatform.WEB, 60);
DriverManager.launchURL("https://google.com", true);
Interactions.isElement(ElementState.Present, parseLocatorObject(LocatorPlatform.WEB, "LandingScreen"));
Interactions.isElement(ElementState.Present, parseLocatorObject(LocatorPlatform.WEB, "LoginTitle"), 10);
Interactions.enterText(parseLocatorObject(LocatorPlatform.WEB, "LoginField"), "user@gamil.com", false);
Interactions.enterText(parseLocatorObject(LocatorPlatform.WEB, "PasswordField"), "123", false);
Interactions.click(parseLocatorObject(LocatorPlatform.WEB, "SubmitButton"));
DriverManager.quitDriverInstance(LocatorPlatform.WEB);
```