# Adobe Target Java SDK

The Adobe Target Java SDK uses the [Target View Delivery API] to retrieve and deliver personalized experiences using
best practices. Furthermore, the Java SDK helps manage integrations with Experience Cloud solutions like Visitor API and Adobe 
Analytics.

- Checkout [Server-Side Optimization](https://medium.com/adobetech/server-side-optimization-with-the-new-target-java-sdk-421dc418a3f2) post for benefits and best practices of using Target Java SDK.

- Standalone Spring Boot based sample is available at [target-java-sdk-samples](https://github.com/adobe/target-java-sdk-samples) 

## Getting started

### Prerequisites

- Java 8+
- Maven or Gradle

### Installation  

To get started with Target Java SDK, just add it as a dependency in `gradle` as:
```groovy
compile 'com.adobe.target:target-java-sdk:1.0.0'
```
or `maven` as:
```xml
<dependency>
    <groupId>com.adobe.target</groupId>
    <artifactId>target-java-sdk</artifactId>
    <version>1.0.0 </version>
</dependency>
```

## Super Simple to Use

The Target Java SDK has been designed to facilitate interaction with Adobe [Target View Delivery API] 
in Java environments. 

### Creating Client

You communicate with Target using `TargetClient`, which can be instantiated as:
```java
ClientConfig config = ClientConfig.builder()
        .client("emeaprod4")
        .organizationId("0DD934B85278256B0A490D44@AdobeOrg")
        .build();
TargetClient targetClient = TargetClient.create(config);
```
`TargetClient` is thread safe and is supposed to be used at most one instance per `orgId` per application lifetime.

### Creating Request

Once we have `targetClient` instance, create requests as following:
```java
MboxRequest mbox = new MboxRequest().name("server-side-mbox").index(0);
TargetDeliveryRequest request = TargetDeliveryRequest.builder()
        .context(new Context().channel(ChannelType.WEB))
        .execute(new ExecuteRequest().mboxes(Arrays.asList(mbox)))
        .build();
```
It supports fluent API as well as Java bean conventions for setting request values.
### Executing Request
Making requests are as easy as:
```java
TargetDeliveryResponse response = targetClient.getOffers(request);
```
and async requests as:
```java
CompletableFuture<TargetDeliveryResponse> offers = targetClient.getOffersAsync(request);
```
 Offers retrieved can be used by any 
end client including browsers, mobiles, IOT devices or servers.
## Table of Contents

  * [Getting started](#getting-started)
    + [Prerequisites](#prerequisites)
    + [Installation](#installation)
  * [Super Simple to Use](#super-simple-to-use)
      + [Creating Client](#creating-client)
      + [Creating Request](#creating-request)
      + [Executing Request](#executing-request)
  * [Table of Contents](#table-of-contents)
  * [Target Only](#target-only)
  * [Maintaining Sessions](#maintaining-sessions)
  * [Asynchronous Requests](#asynchronous-requests)
  * [Proxy Configuration](#proxy-configuration)
  * [ECID Integration](#ecid-integration)
  * [ECID with Customer IDs Integration](#ecid-with-customer-ids-integration)
  * [ECID and Analytics Integration](#ecid-and-analytics-integration)
  * [at.js integration via serverState](#atjs-integration-via-serverstate)
  * [Custom rendering of Target offers](#custom-rendering-of-target-offers)
  * [JSON offers simplified](#json-offers-simplified)
  * [Enterprise Permissions and Property Support](#enterprise-permissions-and-property-support)
  * [On-device decisioning method](#on-device-decisioning-method)
  * [Troubleshooting](#troubleshooting)
  * [Target Traces](#target-traces)
  * [Target Java SDK API](#target-java-sdk-api)
      - [TargetClient.create](#targetclientcreate)
      - [TargetClient.getOffers](#targetclientgetoffers)
      - [TargetClient.getAttributes](#targetclientgetattributes)
      - [TargetClient.sendNotifications](#targetclientsendnotifications)
      - [Target SDK utility accessors](#targetsdk-utility-accessors)
  * [Multiple API requests](#multiple-api-requests)
  * [Development](#development)

---

## Target Only

The Target Java SDK can be used to retrieve personalized content from Target without forcing the use of ECID. Assuming
you have [created the client], you can make requests to target as follows:

```java
MboxRequest mbox = new MboxRequest().name("server-side-mbox").index(0);
TargetDeliveryRequest request = TargetDeliveryRequest.builder()
        .context(new Context().channel(ChannelType.WEB))
        .execute(new ExecuteRequest().mboxes(Arrays.asList(mbox)))
        .build();

TargetDeliveryResponse targetDeliveryResponse = targetClient.getOffers(request);
if (offers.getStatus() == 200) {
    DeliveryResponse offers = targetDeliveryResponse.getResponse();
} 
```
The original request URL should also be passed in the `address` field of the `Context`. 

Full Sample: Checkout `/mboxTargetOnly` endpoint in [TargetController](samples/src/main/java/com/adobe/target/sample/controller/TargetController.java)
## Maintaining Sessions
By default, the Target Java SDK generates a new session ID for every Target call, which might not always be the desired behavior. 
 
 To ensure that Target properly tracks the user session, you should save the Target cookies after Target content is retrieved. Target cookies are just key/value pairs that Target uses to maintain the session information. 
 It can be retrieved using:
 ```java
List<TargetCookie> cookies = targetDeliveryResponse.getCookies();
```
In case the end client supports persistence (eg. browser), it can be sent to the end client. These Target cookies need to be set in subsequent requests for the same user. The sdk will take care of expiring and refreshing the cookies, so they should be refreshed each time a response is received. You can retrieve names of all valid Target Cookies by 
 `CookieUtils.getTargetCookieNames()`

In a sample `Spring Boot` application, this could look like this:

```java
@RestController
public class TargetRestController {

    @Autowired
    private TargetClient targetJavaClient;

    @GetMapping("/mboxTargetOnly")
    public TargetDeliveryResponse mboxTargetOnly(
            @RequestParam(name = "mbox", defaultValue = "server-side-mbox") String mbox,
            HttpServletRequest request, HttpServletResponse response) {
        ExecuteRequest executeRequest = new ExecuteRequest()
                .mboxes(getMboxRequests(mbox));

        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(getContext(request))
                .execute(executeRequest)
                .cookies(getTargetCookies(request.getCookies()))
                .build();
        TargetDeliveryResponse targetResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        setCookies(targetResponse.getCookies(), response);
        return targetResponse;
    }

}
```
assuming you have [created the client] as a spring bean.
#### Utility Methods
All the helper methods used above are reusable across controllers and can be moved to a separate utility class.

```java
public class TargetRequestUtils {

    public static Context getContext(HttpServletRequest request) {
        Context context = new Context()
                .channel(ChannelType.WEB)
                .timeOffsetInMinutes(330.0)
                .address(getAddress(request));
        return context;
    }

    public static Address getAddress(HttpServletRequest request) {
        Address address = new Address()
                .referringUrl(request.getHeader("referer"))
                .url(request.getRequestURL().toString());
        return address;
    }

    public static List<TargetCookie> getTargetCookies(Cookie[] cookies) {
        if (cookies == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(cookies)
                .filter(Objects::nonNull)
                .filter(cookie -> CookieUtils.getTargetCookieNames().contains(cookie.getName()))
                .map(cookie -> new TargetCookie(cookie.getName(), cookie.getValue(), cookie.getMaxAge()))
                .collect(Collectors.toList());
    }

    public static HttpServletResponse setCookies(List<TargetCookie> targetCookies,
                                                  HttpServletResponse response) {
        targetCookies
                .stream()
                .map(targetCookie -> new Cookie(targetCookie.getName(), targetCookie.getValue()))
                .forEach(cookie -> {
                    cookie.setPath("/");
                    response.addCookie(cookie);
                });
        return response;
    }

    public static List<MboxRequest> getMboxRequests(String... name) {
        List<MboxRequest> mboxRequests = new ArrayList<>();
        for (int i = 0; i < name.length; i++) {
            mboxRequests.add(new MboxRequest().name(name[i]).index(i));
        }
        return mboxRequests;
    }

    public static PrefetchRequest getPrefetchRequest() {
        PrefetchRequest prefetchRequest = new PrefetchRequest();
        ViewRequest viewRequest = new ViewRequest();
        prefetchRequest.setViews(Arrays.asList(viewRequest));
        return prefetchRequest;
    }
}

```
Full Sample: Checkout `/mboxTargetOnly` endpoint in [TargetController](samples/src/main/java/com/adobe/target/sample/controller/TargetController.java)

## Asynchronous Requests

Aside from the fact that your end client will never flicker or reload, the main benefit of server-side integration is that you can leverage the huge band-with and computing resources available on the server-side by using parallelism. Target 
Java SDK supports asynchronous requests, using which you can bring down the effective target time to zero.

In a sample `Spring` application it could look like this:
```java
@RestController
public class TargetRestController {

    @Autowired
    private TargetClient targetJavaClient;
    
    @GetMapping("/mboxTargetOnlyAsync")
        public TargetDeliveryResponse mboxTargetOnlyAsync(
                @RequestParam(name = "mbox", defaultValue = "server-side-mbox") String mbox,
                HttpServletRequest request, HttpServletResponse response) {
            ExecuteRequest executeRequest = new ExecuteRequest()
                    .mboxes(getMboxRequests(mbox));
    
            TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                    .context(getContext(request))
                    .execute(executeRequest)
                    .cookies(getTargetCookies(request.getCookies()))
                    .build();
            CompletableFuture<TargetDeliveryResponse> targetResponseAsync =
                    targetJavaClient.getOffersAsync(targetDeliveryRequest);
            targetResponseAsync.thenAccept(tr -> setCookies(tr.getCookies(), response));
            simulateIO();
            TargetDeliveryResponse targetResponse = targetResponseAsync.join();
            return targetResponse;
        }

    /**
     * Function for simulating network calls like other microservices and database calls
     */
    private void simulateIO() {
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
```
assuming you have [created the client] as a spring bean and you have [utility methods] available.

Target request is fired before `simulateIO` and by the time it is executed target result should also be ready. 
Even if it's not, you'll have significant savings in most cases.

Full Sample: Full Sample: Checkout `/mboxTargetOnlyAsync` endpoint in [TargetRestController](samples/src/main/java/com/adobe/target/sample/controller/TargetRestController.java)

## Proxy Configuration

If the application running the SDK requires a proxy to access the internet, the TargetClient will need to be configured with a proxy configuration as follows.
```java
ClientConfig clientConfig = ClientConfig.builder()
	.client("emeaprod4")
	.organizationId("0DD934B85278256B0A490D44@AdobeOrg")
	.proxyConfig(new ClientProxyConfig(host,port))
	.build();
TargetClient targetClient = TargetClient.create(clientConfig);
```

If a proxy authentication is required, the credentials can be passed as parameters to the ClientProxyConfig constructor, as per the below example.
Please note that this only works for simple username/password proxy authentication. 
```java
ClientConfig clientConfig = ClientConfig.builder()
	.client("emeaprod4")
	.organizationId("0DD934B85278256B0A490D44@AdobeOrg")
	.proxyConfig(new ClientProxyConfig(host,port,username,password))
	.build();
TargetClient targetClient = TargetClient.create(clientConfig);
```

---

## ECID Integration

Although using the Target Java SDK for fetching content from Target can be powerful, the added value of using ECID
for user tracking outweighs using Target only. ECID allows leveraging all the cool features of the Adobe Experience Cloud,
such as audience sharing, analytics integration, etc.  
ECID has a client-side part `Visitor` (from visitor.js) which maintains it's own state. We'll need to keep this part
updated with the stuff happening on the server-side. `visitor.js` creates a cookie named `AMCV_{organizationId}` which is used by Target sdk for ECID integration. When Target response is returned you need to update `Visitor` instance on
client-side with `visitorState` returned by Target Java Sdk.

Here is the sample `client-side` code:
```html
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>ECID (Visitor API) Integration Sample</title>
  <script src="VisitorAPI.js"></script>
  <script>
    Visitor.getInstance("${organizationId}", {serverState: ${visitorState}});
  </script>
</head>
<body>
  <pre>Sample content</pre>
</body>
</html>
```

Here is the sample server-side `Spring` application handling ECID integration:

```java
@Controller
public class TargetControllerSample {

    @Autowired
    private TargetClient targetJavaClient;
    
    @GetMapping("/targetMcid")
    public String targetMcid(Model model, HttpServletRequest request, HttpServletResponse response) {
        Context context = getContext(request);
        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(context)
                .prefetch(getPrefetchRequest())
                .cookies(getTargetCookies(request.getCookies()))
                .build();
    
        TargetDeliveryResponse targetResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        model.addAttribute("visitorState", targetResponse.getVisitorState());
        model.addAttribute("organizationId", "0DD934B85278256B0A490D44@AdobeOrg");
        setCookies(targetResponse.getCookies(), response);
        return "targetMcid";
    }
    
}
```
assuming you have [created the client] as a spring bean and you have [utility methods] available.

The biggest benefit of using ECID integration is that it allows you to share Audience Manager segments with Target.
Note that as this is a server-side integration for first-time visitors, you might not have any Audience Manager 
related data. By default, the Target Java SDK creates a new Visitor instance on each getOffers call.

**Note:** Since `Visitor API` does not support non-browser clients natively, for now this integration will only work if 
end client is a browser. Please use native Target Mobile Sdk for mobile integrations of Visitor.

Full sample: Checkout `/targetMcid` endpoint in [TargetController](samples/src/main/java/com/adobe/target/sample/controller/TargetController.java)

---

## ECID with Customer IDs Integration

In order to track visitor user accounts and logon status details, `customerIds` may be passed to Target.   
The `customerIds` object is similar to the ECID functionality described here: https://marketing.adobe.com/resources/help/en_US/mcvid/mcvid-authenticated-state.htmlocs.adobe.com/content/help/en/id-service/using/reference/authenticated-state.html

Here is the sample `client-side` code:
```html
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>ECID (Visitor API) Integration Sample</title>
  <script src="VisitorAPI.js"></script>
  <script>
    Visitor.getInstance("${organizationId}", {serverState: ${visitorState}});
  </script>
</head>
<body>
  <pre>Sample content</pre>
</body>
</html>
```

Here is the `Spring` controller that showcases `customerIds` integration:

```java
@Controller
public class TargetControllerSample {

    @Autowired
    private TargetClient targetJavaClient;
    
    @GetMapping("/targetMcid")
    public String targetMcid(Model model, HttpServletRequest request, HttpServletResponse response) {
        Context context = getContext(request);
        Map<String, CustomerState> customerIds = new HashMap<>();
        customerIds.put("userid", CustomerState.authenticated("67312378756723456"));
        customerIds.put("puuid", CustomerState.unknown("550e8400-e29b-41d4-a716-446655440000"));
        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(context)
                .prefetch(getPrefetchRequest())
                .cookies(getTargetCookies(request.getCookies()))
                .customerIds(customerIds)
                .build();
    
        TargetDeliveryResponse targetResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        model.addAttribute("visitorState", targetResponse.getVisitorState());
        model.addAttribute("organizationId", "0DD934B85278256B0A490D44@AdobeOrg");
        setCookies(targetResponse.getCookies(), response);
        return "targetMcid";
    }
    
}
```

Full sample: Checkout `/targetMcid` endpoint in [TargetController](samples/src/main/java/com/adobe/target/sample/controller/TargetController.java)

---


## ECID and Analytics Integration

To get the most out of the Target Java SDK and to use the powerful analytics capabilities provided by Adobe Analytics,
 you can use the Target, ECID and Analytics combo. 

Using MCID, Analytics, and Target lets you:
- Use segments from Adobe Audience Manager
- Customize the user experience based on the content retrieved from Target
- Ensure that all events and success metrics are collected in Analytics
- Use Analytics' powerful queries and benefit from awesome report visualizations

You don't have to do any special handling for analytics on server-side. Analytics uses `Visitor` instance to synchronize
with target. Once you have ECID integrated, you just have to add `AppMeasurement.js`(Analytics library) on the client 
side.

Here is the sample `client-side` code for analytics integration:
```html
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>ECID and Analytics integration Sample</title>
  <script src="VisitorAPI.js"></script>
  <script>
    Visitor.getInstance("${organizationId}", {serverState: ${visitorState}});
  </script>
</head>
<body>
  <p>Sample content</p>
  <script src="AppMeasurement.js"></script>
  <script>var s_code=s.t();if(s_code)document.write(s_code);</script>
</body>
</html>
```

Here is a sample `Spring` controller that demonstrates how you can use all three solutions in a single application: 

```java
@Controller
public class TargetControllerSample {

    @Autowired
    private TargetClient targetJavaClient;
    
    @GetMapping("/targetAnalytics")
    public String targetMcid(Model model, HttpServletRequest request, HttpServletResponse response) {
        Context context = getContext(request);
        Map<String, CustomerState> customerIds = new HashMap<>();
        customerIds.put("userid", CustomerState.authenticated("67312378756723456"));
        customerIds.put("puuid", CustomerState.unknown("550e8400-e29b-41d4-a716-446655440000"));
        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(context)
                .prefetch(getPrefetchRequest())
                .cookies(getTargetCookies(request.getCookies()))
                .customerIds(customerIds)
                .trackingServer("imsbrims.sc.omtrds.net")
                .build();
    
        TargetDeliveryResponse targetResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        model.addAttribute("visitorState", targetResponse.getVisitorState());
        model.addAttribute("organizationId", "0DD934B85278256B0A490D44@AdobeOrg");
        setCookies(targetResponse.getCookies(), response);
        return "targetAnalytics";
    }
    
}
```
**Note:** Since `Analytics` require `Visitor` instance for integration, this integration will only work if end client 
is browser. Please use native Target Mobile Sdk for mobile integrations with Analytics.

Full sample: Full sample: Checkout `/targetAnalytics` endpoint in [TargetController](samples/src/main/java/com/adobe/target/sample/controller/TargetController.java)

---

## at.js integration via serverState

In the previous sections we saw that fetching offers from Target is pretty easy. Offers are easy to apply in case 
they are simple feature flags or have predefined json schemas. Sending conversion notifications for such content is also 
pretty easy as demonstrated [here](#custom-rendering-of-target-offers).

However, some offers can be tricky to render specially in case your end client is a browser. Offers created via VEC
contains DOM paths and complex HTMLs which requires manipulating DOM elements. Sending conversion notifications for such
content is also pretty tricky.

What if we could prefetch the Target content on the server-side, include it in the page returned to the client, and 
then just have `at.js`(Target's client side library) apply the Target offers immediately, without making another 
expensive network call? Your page will never pre-hide or flicker. `at.js` will also take care of sending conversion 
(displayed/clicked) notifications back to target.

Target `serverState` is a new feature available in at.js v2.2+, that allows at.js to apply Target offers directly from
content fetched on the server side.

In order to use this feature with Target Java SDK we just have to set `window.targetGlobalSettings.serverState` object
in the returned page, with response object returned after a successful `getOffers()` API call, as follows:

First you fetch the target offers as usual and then send the response to client in a variable. In a sample `Spring` 
application it could look something like this:
```java
@Controller
public class TargetController {

    @Autowired
    private TargetClient targetJavaClient;
    
    @GetMapping("/viewsTargetOnly")
    public String viewsTargetOnly(Model model, HttpServletRequest request, HttpServletResponse response) {
        Context context = getContext(request);
        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(context)
                .prefetch(getPrefetchRequest())
                .cookies(getTargetCookies(request.getCookies()))
                .build();
    
        TargetDeliveryResponse targetResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        model.addAttribute("serverState", targetResponse.getServerState());
        setCookies(targetResponse.getCookies(), response);
        return "viewsTargetOnly";
    }
    
}
```
Once you render your page, you can set this response in `window.targetGlobalSettings.serverState` variable. In a sample
`thymeleaf` application it could look something like this:

```html
<!doctype html>
<html>
<head>
  ...
<script th:inline="javascript">
    window.targetGlobalSettings = {
        overrideMboxEdgeServer: true,
        serverState: /*[[${serverState}]]*/ {}
    }
</script>
<script src="at.js"></script>
</head>
...
</html>
```

A sample `serverState` object JSON for view prefetch looks as follows:
```json
{
 "request": {
  "requestId": "076ace1cd3624048bae1ced1f9e0c536",
  "id": {
   "tntId": "08210e2d751a44779b8313e2d2692b96.21_27"
  },
  "context": {
   "channel": "web",
   "timeOffsetInMinutes": 0
  },
  "experienceCloud": {
   "analytics": {
    "logging": "server_side",
    "supplementalDataId": "7D3AA246CC99FD7F-1B3DD2E75595498E"
   }
  },
  "prefetch": {
   "views": [
    {
     "address": {
      "url": "my.testsite.com/"
     }
    }
   ]
  }
 },
 "response": {
  "status": 200,
  "requestId": "076ace1cd3624048bae1ced1f9e0c536",
  "id": {
   "tntId": "08210e2d751a44779b8313e2d2692b96.21_27"
  },
  "client": "testclient",
  "edgeHost": "mboxedge21.tt.omtrdc.net",
  "prefetch": {
   "views": [
    {
     "name": "home",
     "key": "home",
     "options": [
      {
       "type": "actions",
       "content": [
        {
         "type": "setHtml",
         "selector": "#app > DIV.app-container:eq(0) > DIV.page-container:eq(0) > DIV:nth-of-type(2) > SECTION.section:eq(0) > DIV.container:eq(1) > DIV.heading:eq(0) > H1.title:eq(0)",
         "cssSelector": "#app > DIV:nth-of-type(1) > DIV:nth-of-type(1) > DIV:nth-of-type(2) > SECTION:nth-of-type(1) > DIV:nth-of-type(2) > DIV:nth-of-type(1) > H1:nth-of-type(1)",
         "content": "<span style=\"color:#FF0000;\">Latest</span> Products for 2020"
        }
       ],
       "eventToken": "t0FRvoWosOqHmYL5G18QCZNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==",
       "responseTokens": {
        "profile.memberlevel": "0",
        "geo.city": "dublin",
        "activity.id": "302740",
        "experience.name": "Experience B",
        "geo.country": "ireland"
       }
      }
     ],
     "state": "J+W1Fq18hxliDDJonTPfV0S+mzxapAO3d14M43EsM9f12A6QaqL+E3XKkRFlmq9U"
    }
   ]
  }
 }
}
```

Once the page is loaded in the browser, at.js will apply all the Target offers from `serverState` immediately,
without firing any network calls against the Target edge. Additionally, at.js will only prehide the DOM elements
for which Target offers are available in the content fetched server-side, thus positively impacting page load performance
and end-user experience.

### Important notes 
- At the moment, at.js v2.2 supports only Page Load and View Prefetch for `serverState` scenarios. Support for mboxes may 
be provided in a future at.js release
- In case of SPAs using [Target Views and triggerView() at.js API](https://docs.adobe.com/content/help/en/target/using/implement-target/client-side/functions-overview/adobe-target-triggerview-atjs-2.html)
, at.js will cache the content for all Views prefetched on the server-side and will apply these as soon as each View is 
triggered via `triggerView()`, again without firing any additional content-fetching calls to Target.
- When applying `serverState` offers, at.js takes into consideration `pageLoadEnabled` and `viewsEnabled` settings, e.g.
Page Load offers will not be applied if `pageLoadEnabled` setting is `false`

Full sample: Checkout `/targetMcid` endpoint in [TargetController](samples/src/main/java/com/adobe/target/sample/controller/TargetController.java)

---

## Custom rendering of Target offers

Customers may opt to process Target offers fetched via `getOffers()` API in a custom way, without using the rendering
capabilities of at.js for example, or when Target content is to be displayed on a runtime environment or device, for 
which no Target rendering libraries are available.  
Target delivers offers in 2 modes:
1. **Execute**: Get the offer and mark the offer as converted. For eg. displayed or clicked. 
2. **Prefetch**: Get the offer but don't mark it as converted. In this case target responds with a notification event 
token which can later be used to mark offer as converted.

In cases when mbox or view content is prefetched, customers should make sure to call `sendNotifications()` API for 
proper reporting once any prefetched content has been displayed (or clicked, in case of click metrics).  
Note that in this case, it is the responsibility of the customer to provide the appropriate notification event tokens, 
as well as view and mbox `states` in the `sendNotifications` request.  

Consider the following example:

```java
// First, let's build the Target Delivery API request for pre-fetching content for an mbox
PrefetchRequest executeRequest = new PrefetchRequest()
        .mboxes(getMboxRequests("server-side-mbox"));

TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
        .context(getContext(request))
        .prefetch(executeRequest)
        .cookies(getTargetCookies(request.getCookies()))
        .build();
        
// Next, we fetch the offers via Target Java SDK getOffers() API
TargetDeliveryResponse targetResponse = targetJavaClient.getOffers(targetDeliveryRequest);
```

A successful response will contain a Target Delivery API response object, which contains prefetched content for the 
requested mboxes.   
A sample `targetResponse.response` object may look as follows:
```json
{
   "status":200,
   "requestId":"ea6dfe94-f4aa-46a8-b13e-a1e131a1dc34",
   "id":{
      "tntId":"20250794242226839061607285880759069379.31_5",
      "marketingCloudVisitorId":"20250794242226839061607285880759069379",
      "customerIds":[

      ]
   },
   "client":"emeaprod4",
   "edgeHost":"mboxedge31.tt.omtrdc.net",
   "prefetch":{
      "views":[

      ],
      "mboxes":[
         {
            "index":1,
            "name":"server-side-mbox-prefetch",
            "options":[
               {
                  "type":"html",
                  "content":" Server side prefetch HTML offer",
                  "eventToken":"DQ5I8XE7vs6wVIBc5m8jgmqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==",
                  "responseTokens":{
                     "offer.name":"/anshul_server_sideprefetch/experiences/0/pages/0/zones/0/1559900851641",
                     "experience.id":"0",
                     "activity.name":"Anshul_server_side_prefetch",
                     "profile.activeActivities":"210454,210453",
                     "profile.daysSinceLastVisit":"1.0",
                     "activity.id":"210454",
                     "option.name":"Offer2",
                     "experience.name":"Experience A",
                     "option.id":"2",
                     "profile.isFirstSession":"false",
                     "offer.id":"531934"
                  }
               }
            ],
            "metrics":[

            ],
            "trace":{

            },
            "state":"R3GqCTBoZfbf6JuhJihve0/cEw7CopUPID0/aevSePqR+8iGdmzv/1+oPhGbQUgYtjiF0fhGOW6oFeBK6hWP6jaAmqyD543TKXaB5ffKjtgzsDjYqO0jwtew3ecS9qyyIWx+LEfG6IOdekO/jEIPG19DnqxGHiXzI/EvMWiV5Sf7n89R1qzn7k3IjsI2xruVqP+9uSqmO8K+T89f8OEc4fOb5wcob4Gz/92IPiDe+z8IF3423bQxcUYtamFgo1v7urtZvh8+NepSfLR9EAhDZg=="
         }
      ],
      "metrics":[

      ]
   }
}
```

Note the mbox `name`, `state` and `eventToken` field in each of the Target content options. These
should be provided in the `sendNotifications()` request, as soon as each content option is displayed.  
Let's consider that the mbox at index `0` has been displayed on a non-browser device. The notifications 
request will look like this:
```java
ResponseStatus sendNotifications(TargetDeliveryResponse targetDeliveryResponse,
                                                    HttpServletRequest request) {
    PrefetchResponse prefetch = targetDeliveryResponse.getResponse().getPrefetch();
    PrefetchMboxResponse mbox = prefetch.getMboxes().get(0);
    List<String> tokens = mbox.getOptions()
            .stream()
            .map(Option::getEventToken)
            .collect(Collectors.toList());
    Notification notification = new Notification()
            .id(UUID.randomUUID().toString())
            .impressionId(UUID.randomUUID().toString())
            .mbox(new NotificationMbox().name(mbox.getName()).state(mbox.getState()))
            .type(MetricType.DISPLAY)
            .timestamp(System.currentTimeMillis())
            .tokens(tokens);

    TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
            .context(getContext(request))
            .cookies(getTargetCookies(request.getCookies()))
            .notifications(Arrays.asList(notification))
            .build();

    ResponseStatus status = targetJavaClient.sendNotifications(targetDeliveryRequest);
    return status;
}
```

Notice that we've included both the mbox state and the event token corresponding to the Target offer delivered in the
prefetch response.
Having built the notifications request, we can send it to Target via `sendNotifications()` method. It also has an async
counterpart `sendNotificationsAsync`. 

---

## JSON offers simplified

The Target Java SDK provides a simplified way to to retrieve JSON offers from target and access the attributes of the offer. This is done using the `getAttributes` method.

In the code sample below, take a look at the `getAttributes` call.  A `TargetDeliveryRequest` and a var-args array of mbox names is passed in. The result is an `Attributes` object with a few methods that can be used to get offer details. 

Let's say that the JSON content for an offer for mbox `demo-engineering-flags` is the following:

```json
{
    "cdnHostname": "cdn.cloud.corp.net",
    "searchProviderId": 143,
    "hasLegacyAccess": false
}
```

There are various methods that can be used to get the values from the `demo-engineering-flags` mbox offer, depending on their type such as `getString(mbox)` and `getBoolean(mbox)`.

And the `toMboxMap(mbox)` method can be used to get a Map representation of the mbox offer.

```java

Attributes attrs = targetJavaClient.getAttributes(targetRequest, "demo-engineering-flags");

String searchProviderId = attrs.getString("demo-engineering-flags", "searchProviderId");
boolean hasLegacyAccess = attrs.getBoolean("demo-engineering-flags", hasLegacyAccess");

Map<String, Object> mboxMap = attrs.toMboxMap("demo-engineering-flags");
String cdnHostname = (String) mboxMap.get("cdnHostname");

```

Note that you don't have to add the attribute mboxes into the `TargetDeliveryRequest` that is passed into `getAttributes`. If you don't add them yourself, they will be added for you. However, if you would like to add custom parameters or customize the mbox request in any way, you can do that in the `TargetDeliveryRequest` parameter. 

#### Example

See the `localGetAttributes` method in `TargetController` in the [Adobe Target Java SDK Samples](https://github.com/adobe/target-java-sdk-samples) for a working example.

---

## Enterprise Permissions and Property Support

The Target Java SDK includes support for Target Properties.  If you are unfamiliar with how Adobe Target handles enterprise permissions via workspaces and properties, you can [read more about it here](https://docs.adobe.com/content/help/en/target/using/administer/manage-users/enterprise/properties-overview.html).

The client can make use of a property token in one of two ways.

### Global Property Token

If you want all getOffers calls to use the same propertyToken, you can specify a `defaultPropertyToken` in the ClientConfig object passed in during initialization.  When configured in this way all getOffers calls will automatically include the property token.

```java
ClientConfig clientConfig = ClientConfig.builder()
	.client("emeaprod4")
	.organizationId("0DD934B85278256B0A490D44@AdobeOrg")
	.defaultPropertyToken("8c4630b1-16db-e2fc-3391-8b3d81436cfb")
	.build();

TargetClient targetClient = TargetClient.create(clientConfig);
```

### Incidental Property Token in getOffers call

A property token can also be specified in an individual getOffers call.  This is done by [adding a property object with token to the request](https://developers.adobetarget.com/api/delivery-api/#section/User-Permissions-(Premium)).  A property token specified in this way takes precedent over one set in the config.  

```java
ExecuteRequest executeRequest = new ExecuteRequest()
    .mboxes(getMboxRequests(mbox));

TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
    .context(getContext(request))
    .execute(executeRequest)
    .cookies(getTargetCookies(request.getCookies()))
    .property(new Property().token("8c4630b1-16db-e2fc-3391-8b3d81436cfb"))
    .build();

TargetDeliveryResponse targetResponse = targetClient.getOffers(targetDeliveryRequest);
```

---

## On-device decisioning mode

The Target Java SDK can be configured to run in on-device decisioning mode. In this mode, the SDK loads a rules definition file on startup and uses it to determine the outcomes for subsequent `getOffers` calls instead of making repeat requests to the delivery API each time. This can greatly improve performance if you are concerned about network latency and would like to limit the number of requests made to target edge servers.

By default, the SDK is configured to always make a request to the target delivery API for each `getOffers` call. But you can configure the SDK to use on-device decisioning method instead.
 
First, you need to configure the SDK properly in order to enable on-device decisioning. This is done by setting the defaultDecisioningMethod to either `DecisioningMethod.ON_DEVICE` or `DecisioningMethod.HYBRID` in the `ClientConfig` object that is used to initialize the targetClient.

```java
ClientConfig clientConfig = ClientConfig.builder()
    .client("emeaprod4")
    .organizationId("0DD934B85278256B0A490D44@AdobeOrg")
    .defaultDecisioningMethod(DecisioningMethod.HYBRID)
    .build();

TargetClient targetClient = TargetClient.create(clientConfig);
```

This will set the `decisioningMethod` in each getOffers request to whatever you specify as the `defaultDecisioningMethod` by default. 

However, you can override the decisioning method in any `getOffers` call by explicitly setting `decisioningMethod` in the `TargetDeliveryRequest`.

```java
ExecuteRequest executeRequest = new ExecuteRequest()
    .mboxes(getMboxRequests("local-mbox"));

TargetDeliveryRequest targetRequest = TargetDeliveryRequest.builder()
    .context(getContext(request))    
    .cookies(getTargetCookies(cookies))
    .execute(executeRequest)
    .decisioningMethod(DecisioningMethod.ON_DEVICE)
    .build();

TargetDeliveryResponse response = targetClient.getOffers(targetRequest);
```

With decisioningMethod set to DecisioningMethod.ON_DEVICE, your app will determine what offers the user qualifies for on-device decisioning without hitting the target edge servers.

Note that it will take some time (usually less than 1 second) between when the targetClient is initialized until it will be able to execute requests on device. You can set a `onDeviceDecisioningHandler` object in the `ClientConfig` and its `onDeviceDecisioningReady` method will be called when the client is ready to handle on-device decisioning. Please see the documentation for the `OnDeviceDecisioningHandler` below for more details.

### Limitations

Not all target activities can be decided on-device at this time.  

#### Audience Rules

Some activities are not supported due to audience rules. Below is a list of audience rules with an indication for they are supported by on-device decisioning or will require a request to target edge servers to fulfill:

| Audience Rule    | On-device decisioning | Server side decisioning |
|------------------|-----------------------|------------------------|
| Geo              | Supported             | Supported              |
| Network          | Not Supported         | Supported              |
| Mobile           | Not Supported         | Supported              |
| Custom           | Supported             | Supported              |
| Operating System | Supported             | Supported              |
| Site Pages       | Supported             | Supported              |
| Browser          | Supported             | Supported              |
| Visitor Profile  | Not Supported         | Supported              |
| Traffic Sources  | Not Supported         | Supported              |
| Time Frame       | Supported             | Supported              |

#### Response Types

Most, but not all response types are supported by on-device decisioning:

| Response Type    | On-device decisioning | Server side decisioning |
|------------------|-----------------------|------------------------|
| Default Content  | Supported             | Supported              |
| HTML             | Supported             | Supported              |
| JSON             | Supported             | Supported              |
| Recommendation   | Not Supported         | Supported              |
| Redirect         | Supported             | Supported              |
| Remote           | Not Supported         | Supported              |
| Visual/VEC       | Supported             | Supported              |

#### Other Limitations

Only A/B activities with Manual Traffic Allocation are supported in on-device decisioning. Auto-allocate and Auto-target are not supported currently.

XT activities are supported in on-device decisioning.

Automated Personalization, Multi-variate and Recommendations activities are not currently supported in on-device decisioning.

### Hybrid Mode

Although all activities are not yet supported by on-device decisioning method, there is a way to get the best of both worlds. If you set `decisioningMethod` to `DecisioningMethod.HYBRID`, then the SDK will determine on its own whether to make decisions on-device or on server side. This way, if a `getOffers` request can be completed on device, the SDK will do so. But if the request includes activities that are not supported, a request to the target delivery API will be made instead. This may be useful as you begin to adopt on-device decisioning.

### Geo support in on-device decisioning

In order to maintain zero latency in on-device decisioning requests with geo-based audiences, we recommend that you provide the geo values yourself in each `TargetDeliveryRequest`. You can do this by setting the `Geo` object in the `Context` of the request.

This means your server will need a way to determine the location of each end user, for instance by doing an IP-to-Geo lookup using a service you will need to set up yourself. Also, some hosting providers such as Google Cloud provide this functionality via custom headers in each `HttpServletRequest`.

```java
public class TargetRequestUtils {

    public static Context getContext(HttpServletRequest request) {
        Context context = new Context()
            .geo(ipToGeoLookup(request.getRemoteAddr()))
            .channel(ChannelType.WEB)
            .timeOffsetInMinutes(330.0)
            .address(getAddress(request));
        return context;
    }
    
    public static Geo ipToGeoLookup(String ip) {
        GeoResult geoResult = geoLookupService.lookup(ip);
        return new Geo()
            .city(geoResult.getCity())
            .stateCode(geoResult.getStateCode())
            .countryCode(geoResult.getCountryCode());
    }

}
```

However, if you don't have the ability to perform IP-to-Geo lookups in your server and you still want to be able to perform on-device decisioning of `getOffers` requests that contain geo-based audiences, this is also supported.

The downside of this approach is that it will use a remote IP-to-Geo lookup that will add latency to each `getOffers` call. This latency should be lower than a remote `getOffers` call since it hits a CDN that should be closer to the end user than the target server.

You can signal to the targetClient that you want to perform an IP-to-Geo lookup in your on-device decisioning request by setting *only* the `ipAddress` object in the `Geo` object in the `Context` of your request. 

```java
public class TargetRequestUtils {

    public static Context getContext(HttpServletRequest request) {
        Context context = new Context()
            .geo(new Geo().ipAddress(request.getRemoteAddr()))
            .channel(ChannelType.WEB)
            .timeOffsetInMinutes(330.0)
            .address(getAddress(request));
        return context;
    }

}
```

Note that if you specify the IP address in the `Geo` object in the `Context` and you don't currently have any on-device decisioning activities that use geo-based audiences then the IP-to-Geo lookup will be skipped as a latency optimization.

---

### Example

See the `onDeviceDecisioning` method in `TargetController` in the [Adobe Target Java SDK Samples](https://github.com/adobe/target-java-sdk-samples) for a working example.

---

## Troubleshooting

In order to understand what is happening on the wire, you have 3 options when instantiating the Java SDK.  
1. `logRequests`: Logs whole request body as well as response body. 
2. `logRequestStatus`: Logs request's url, status along with response time.
3. `requestInterceptor`: You can provide your own request interceptor to debug an outgoing request.

Target Java Sdk uses `slf4j` logging. You need to provide your implementation of logger such as java.util.logging, 
logback and log4j. Refer http://www.slf4j.org/manual.html for more information. All logs will be printed in `debug`.

Here is an example that shows the sample logger being used:

Add dependency like:
```groovy
compile 'org.slf4j:slf4j-simple:2.0.0-alpha0'
```

Once you've configured your logger implementation. Enable the `DEBUG` logs based on your implementation and mark the 
request logging flags.
```java
System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
ClientConfig config = ClientConfig.builder().client("emeaprod4")
        .organizationId("0DD934B85278256B0A490D44@AdobeOrg")
        .logRequests(true)
        .logRequestStatus(true)
        .build();
TargetClient targetClient = TargetClient.create(config);
```
You should see requests, responses and response times being printed in console.

---

## Target Traces

For enhanced debugging via Target Trace functionality, the `authorizationToken` should be passed in the `trace` section
of the Delivery API request. The auth token can be provided from the client-side as a query param:

```java
ExecuteRequest executeRequest = new ExecuteRequest()
        .mboxes(getMboxRequests(mbox));

TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
        .context(getContext(request))
        .execute(executeRequest)
        .cookies(getTargetCookies(request.getCookies()))
        .trace(new Trace().authorizationToken("dummyAuthToken"))
        .build();

TargetDeliveryResponse targetResponse = targetJavaClient.getOffers(targetDeliveryRequest);
```

---

## Target Java SDK API

#### TargetClient.create

`TargetClient TargetClient.create(ClientConfig clientConfig)` creates an instance of the Target Java client. 
`ClientConfig` is created using `ClientConfigBuilder ClientConfig.builder()`.
 
The `ClientConfigBuilder` object has the following structure:

| Name                 | Type     |Required | Default                | Description                                         |
|----------------------|----------|---------|------------------------|-----------------------------------------------------|
| client               |  String  | Yes     | None                   | Target Client Id                                    |
| organizationId       |  String  | Yes     | None                   | Experience Cloud Organization ID                    |
| connectTimeout       |  Number  | No      | 10000                  | Connection timeout for all requests in milliseconds |
| socketTimeout        |  Number  | No      | 10000                  | Socket timeout for all requests in milliseconds     |
| maxConnectionsPerHost|  Number  | No      | 100                    | Max Connections per Target host                     |
| maxConnectionsTotal  |  Number  | No      | 200                    | Max Connections including all Target hosts          |
| enableRetries        |  Boolean | No      | True                   | Automatic retries for socket timeouts (max 4)       |
| logRequests          |  Boolean | No      | False                  | Log Target requests and responses in debug          |
| logRequestStatus     |  Boolean | No      | False                  | Log Target response time, status and url            |
| serverDomain         |  String  | No      | `client`.tt.omtrdc.net | Overrides default hostname                          |
| secure               |  Boolean | No      | true                   | Unset to enforce HTTP scheme                        |
| requestInterceptor   |  HttpRequestInterceptor  | No      | Null   | Add custom request Interceptor                      |
| defaultPropertyToken | String   | No      | None                   | Sets the default property token for every getOffers call |
| defaultDecisioningMethod | DecisioningMethod enum | No | REMOTE            | Must be set to ON_DEVICE or HYBRID to enable on-device decisioning |
| onDeviceEnvironment     | String   | No      | production             | Can be used to specify a different on-device environment such as staging |
| onDeviceConfigHostname  | String   | No      | assets.adobetarget.com | Can be used to specify a different host to use to download the on-device decisioning artifact file |
| onDeviceDecisioningPollingIntSecs | int | No | 300 (5 min)            | Number of seconds between fetches of the on-device decisioning artifact file |
| onDeviceArtifactPayload | byte[]   | No      | None                   | Provides on-device decisioning with previous artifact payload to allow immediate execution |
| onDeviceDecisioningHandler | OnDeviceDecisioningHandler | No | None          | Registers callbacks for on-device decisioning events      |

The `OnDeviceDecisioningHandler` object contains the following callbacks which are called for certain events:

| Name                 | Arguments | Description                                         |
|----------------------|-----------|-----------------------------------------------------|
| onDeviceDecisioningReady  |  None     | Called only once the first time the client is ready for on-device decisioning |
| artifactDownloadSucceeded | byte[] contents of artifact file | Called every time a on-device decisioning artifact is downloaded |
| artifactDownloadFailed | Exception | Called every time there is a failure to download a on-device decisioning artifact |

#### TargetClient.getOffers

`TargetDeliveryResponse TargetClient.getOffers(TargetDeliveryRequest request)` is used to fetch offers from Target. It 
also has an async counterpart `getOffersAsync`.
`TargetDeliveryRequest` is created using `TargetDeliveryRequestBuilder TargetDeliveryRequest.builder()`.

The `TargetDeliveryRequestBuilder` object has the following structure:

| Name                     | Type     | Required  | Description                                      |
|--------------------------|----------|-----------|--------------------------------------------------|
| context                  | Context  |  Yes      | Specifies the context for the request            |
| sessionId                | String   |  No       | Used for linking multiple Target requests        |
| thirdPartyId             | String   |  No       | Your company’s identifier for the user that you can send with every call|
| cookies                  | List<TargetCookie> | No | List of cookies returned in previous Target request of same user.|
| customerIds              | Map<String, CustomerState>    |  No       | Customer Ids in VisitorId-compatible format   |
| execute                  | ExecuteRequest | No | PageLoad or mboxes request to execute. Will be evaluated on server side immediately |
| prefetch                 | PrefetchRequest | No | Views, PageLoad or mboxes request to prefetch. Returns with notification token to be returned on conversion. |
| notifications            | List<Notification> | No | Used to sent notifications regarding what prefetched content was displayed|
| requestId                | String | No | The request ID that will be returned in the response. Generated automatically if not present. |
| impressionId             | String | No | If present,  second and subsequent requests with the same id will not increment impressions to activities/metrics. Generated automatically if not present.|
| environmentId            | Long | No | Valid client environment id. If not specified host will be determined base on the provided host.|
| property                 | Property | No | Specifies the at_property via the token field. It can be used to control the scope for the delivery. |
| trace                    | Trace | No | Enables trace for delivery API.|
| qaMode                   | QAMode | No | Use this object to enable the QA mode in the request.|
| locationHint             | String | No | Target edge cluster location hint. Used to target given edge cluster for this request. |
| visitor                  | Visitor | No | Used to provide custom Visitor API object.|
| id                       | VisitorId | No | Object that contains the identifiers for the visitor. Eg. tntId, thirdParyId, mcId, customerIds. |
| experienceCloud          | ExperienceCloud | No | Specifies integrations with Audience Manager and Analytics. Automatically populated using cookies, if not provided.|
| tntId                    | String | No | Primary identifier in Target for a user. Fetched from targetCookies. Auto-generated if not provided. |
| mcId                     | String | No | Used to merge and share data between different Adobe solutions(ECID). Fetched from targetCookies. Auto-generated if not provided. |
| trackingServer           | String | No | The Adobe Analaytics Server in order for Adobe Target and Adobe Analytics to correctly stitch the data together.
| trackingServerSecure     | String | No | The Adobe Analaytics Secure Server in order for Adobe Target and Adobe Analytics to correctly stitch the data together.
| decisioningMethod        | DecisioningMethod | No | Can be used to explicitly set ON_DEVICE or HYBRID decisioning method for on-device decisioning |

The values of each field should conform to [Target View Delivery API] request specification. 
To learn more about the [Target View Delivery API], see http://developers.adobetarget.com/api/#view-delivery-overview

The `TargetDeliveryResponse` returned by `TargetClient.getOffers()` has the following structure:

| Name                     | Type              | Description                                                 |
|--------------------------|-------------------|-------------------------------------------------------------|
| request                  | TargetDeliveryRequest       | [Target View Delivery API] request                          |
| response                 | DeliveryResponse            | [Target View Delivery API] response                         |
| cookies                  | List<TargetCookies>         | List of session metadata for this user. Need to be passed in next target request for this user.  |
| visitorState             | Map<String, VisitorState>   | Visitor state to be set on client side to be used by Visitor API|
| responseStatus           | ResponseStatus              | An object representing the status of the resposne |

The `ResponseStatus` in the response contains the following fields:

| Name                     | Type              | Description                                                 |
|--------------------------|-------------------|-------------------------------------------------------------|
| status                   | int                         | HTTP status returned from Target          |
| message                  | String                      | Status message in case HTTP status is not 200    |
| remoteMboxes             | List of Strings             | Used for on-device decisioning. Contains a list of mboxes that have remote activities that cannot be decided entirely on-device. |
| remoteViews              | List of Strings             | Used for on-device decisioning. Contains a list of views that have remote activities that cannot be decided entirely on-device. |

The `TargetCookie` object used for saving data for user session has the following structure:

| Name   | Type   | Description                                                                                               |
|--------|--------|-----------------------------------------------------------------------------------------------------------|
| name   | String | Cookie name                                                                                               |
| value  | String | Cookie value, the value will be converted to string                                                       |
| maxAge | Number | The `maxAge` option is a convenience for setting `expires` relative to the current time in seconds        |

You don't have to worry about expiring the cookies. Target handles maxAge inside the Sdk.

#### TargetClient.getAttributes

`Attributes TargetClient.getAttributes(TargetDeliveryRequest request, String ...mboxes)` is used to fetch JSON offers for the given mboes from Target. It 
also has an async counterpart `getAttributesAsync`.
`TargetDeliveryRequest` is created using `TargetDeliveryRequestBuilder TargetDeliveryRequest.builder()`. See `getOffers` documentation above for details.

The `Attributes` object returned by `TargetClient.getAttributes()` has the following methods:

| Name                     | Type                   | Description                                                 |
|--------------------------|------------------------|-------------------------------------------------------------|
| toMboxMap(mbox)          | Map<String, Object>    | Map representation of JSON offer for given mbox. |
| toMap()                  | Map<String, Map<String, Object>>   | Map representation of all JSON offers for all mboxes, keyed by mbox. |
| getString(mbox, key)     | String                 | String representation of value for given key in JSON offer. |
| getBoolean(mbox, key)    | boolean                | Boolean representation of value for given key in JSON offer. Returns false if not present. |
| getInteger(mbox, key)    | int                    | Integer representation of value for given key in JSON offer. Returns 0 if not present. |
| getDouble(mbox, key)     | double                 | Double representation of value for given key in JSON offer. Returns 0 if not present. |
| getResponse()            | TargetDeliveryResponse | See `TargetDeliveryResponse` above in `getOffers()` documentation. |

#### TargetClient.sendNotifications

`TargetClient.sendNotifications(TargetDeliveryRequest): ResponseStatus` is used to send display/click notifications to Target,
for previously prefetched mboxes/views.  
**Note:** this API should only be used when the prefetched Target content is displayed in non-browser environments/devices, where at.js cannot be deployed. 
For content displayed in supported browsers, at.js will handle sending of notifications for content prefetched on the server-side and delivered via serverState.  
The arguments and return value are the same as for [TargetClient.getOffers](#targetclientgetoffers). 
Note that `notifications` array must be present in the provided [Target View Delivery API] request (`request` option).  

#### Target SDK utility accessors

`Set<String> CookieUtils.getTargetCookieNames()` is used to retrieve possible names of target cookies.

---

## Multiple API requests

When using the Target Java SDK to service an incoming client request, a single `getOffers()` call with multiple 
mboxes/views are always preferred over multiple `getOffers()` calls.
However, in cases when there's a need to call Target Java SDK API methods more than once when servicing a client request,
the following considerations should be taken into account:

* **Session Id** - `sessionId` option is not required for a single Target Java SDK API call. However, when several API
calls are made, the same `sessionId` value should be supplied (provided there's no Target cookie present in the client-side 
request). The supplied `sessionId` should be a randomly generated UUID string.

* **Visitor** - By default, the Target Java SDK instantiates a new ECID Visitor instance internally on each API call.
When multiple API calls should share the same Visitor instance, it should be instantiated externally and provided in the
`visitor` option in each of the Target Java SDK API calls.

* **consumerId** - For proper visitor stitching in A4T reports, distinct `consumerId` values should be provided when
making multiple Target Java SDK API calls. `ConsumerIds` are random strings, the only requirement is for these to have
different values when call stitching should take place, and the same value otherwise.  

---

## Development

Check out our [Contribution guidelines](.github/CONTRIBUTING.md) as well as [Code of Conduct](CODE_OF_CONDUCT.md) prior
to contributing to Target Java SDK development.  
1. To build the project: `./gradlew build`  
2. To install `java-sdk` locally: `./gradle install`  
3. To run samples: Run `ClientSampleApplication` and it'll start the application on port 8080.

---

[back to top](#table-of-contents)

[Target View Delivery API]: https://developers.adobetarget.com/api/delivery-api/
[created the client]: #creating-client
[utility methods]: #utility-methods
