/** @license ============== DO NOT ALTER ANYTHING BELOW THIS LINE ! ============

 Adobe Visitor API for JavaScript version: 1.10.0
 Copyright 1996-2015 Adobe, Inc. All Rights Reserved
 More info available at http://www.omniture.com
 */

/*********************************************************************
 * Class Visitor(marketingCloudOrgID,initConfig): Shared functionality across products
 *     marketingCloudOrgID = Marketing Cloud Organization ID to use
 *     initConfig          = Optional initial config object allowing the constructor to fire
 *                           off requests immediately instead of lazily
 *
 * @constructor
 * @noalias
 *********************************************************************/
function Visitor(marketingCloudOrgID,initConfig) {
  if (!marketingCloudOrgID) {
    throw "Visitor requires Adobe Marketing Cloud Org ID";
  }

  /**
   * @type {Visitor}
   * @noalias
   */
  var visitor = this;

  visitor.version = "1.10.0";

  /**
   * @type {!Window}
   */
  var w = window;
  /**
   * @type {Visitor}
   * @noalias
   */
  var thisClass = w['Visitor'];

  thisClass.version = visitor.version;

  if (!w.s_c_in) {
    w.s_c_il = [];
    w.s_c_in = 0;
  }
  visitor._c  = "Visitor";
  visitor._il = w.s_c_il;
  visitor._in = w.s_c_in;
  visitor._il[visitor._in] = visitor;
  w.s_c_in++;

  visitor._log = {
    requests: []
  };

  /**
   * @type {!Document}
   */
  var d = w.document;

  // This and the use of Null below is a hack to keep Google Closure Compiler from creating a global variable for null
  var Null = thisClass.Null;
  if (!Null) {
    Null = null;
  }
  // Same thing for undefined...
  var Undefined = thisClass.Undefined;
  if (!Undefined) {
    Undefined = undefined;
  }
  // ...and for true...
  var True = thisClass.True;
  if (!True) {
    True = true;
  }
  // ...and for false
  var False = thisClass.False;
  if (!False) {
    False = false;
  }

  /*********************************************************************
   * Function _isNOP(m): Test to see if a member is NOT part of the Object.prototype
   *     m = Member
   * Returns:
   *     True if m is NOT part of Object.prototype otherwise False
   *********************************************************************/
  var _isNOP = function(m) {
    return (!Object.prototype[m]);
  };

  /*********************************************************************
   * Function _hash(str): Generate hash of string
   *     str = String to generate hash from
   * Returns:
   *     Hash
   *********************************************************************/
  visitor._hash = function(str) {
    var
      hash = 0,
      pos,
      ch;
    if (str) {
      for (pos = 0;pos < str.length;pos++) {
        ch = str.charCodeAt(pos);
        hash = (((hash << 5) - hash) + ch);
        hash = (hash & hash); // Convert to 32bit integer
      }
    }
    return hash;
  };

  /*********************************************************************
   * Function _generateID(method): Generate a random 128bit ID
   *     method = Optional ID generation method
   *              0 = Decimal 2 63bit numbers
   *              1 = Hex 2 63bit numbers
   * Returns:
   *     Random 128bit ID as a string
   *********************************************************************/
  visitor._generateID = function(method, field) {
    var
      digits = "0123456789",
      high = "",low = "",
      digitNum,digitValue,digitValueMax = 8,highDigitValueMax = 10,lowDigitValueMax = 10; /* The first nibble can't have the left-most bit set because we are deailing with signed 64bit numbers. */

    if (field === fieldMarketingCloudVisitorID) {
      _callStateTracker.isClientSideMarketingCloudVisitorID = True;
    }

    if (method == 1) {
      digits += "ABCDEF";
      for (digitNum = 0;digitNum < 16;digitNum++) {
        digitValue = Math.floor(Math.random() * digitValueMax);
        high += digits.substring(digitValue,(digitValue + 1));
        digitValue = Math.floor(Math.random() * digitValueMax);
        low += digits.substring(digitValue,(digitValue + 1));
        digitValueMax = 16;
      }
      return high + "-" + low;
    }
    /*
     * We're dealing with 2 signed, but positive, 64bit numbers so the max for high and low is:
     * 9222372036854775807
     *    ^---------------- The 4th digit could actually be a 3 if we wanted to add more max checks
     *                      but we set the max to 2 to avoid them
     */
    for (digitNum = 0;digitNum < 19;digitNum++) {
      digitValue = Math.floor(Math.random() * highDigitValueMax);
      high += digits.substring(digitValue,(digitValue + 1));
      if ((digitNum == 0) && (digitValue == 9)) {
        highDigitValueMax = 3;
      } else if (((digitNum == 1) || (digitNum == 2)) && (highDigitValueMax != 10) && (digitValue < 2)) {
        highDigitValueMax = 10;
      } else if (digitNum > 2) {
        highDigitValueMax = 10;
      }
      digitValue = Math.floor(Math.random() * lowDigitValueMax);
      low += digits.substring(digitValue,(digitValue + 1));
      if ((digitNum == 0) && (digitValue == 9)) {
        lowDigitValueMax = 3;
      } else if (((digitNum == 1) || (digitNum == 2)) && (lowDigitValueMax != 10) && (digitValue < 2)) {
        lowDigitValueMax = 10;
      } else if (digitNum > 2) {
        lowDigitValueMax = 10;
      }
    }
    return high + low;
  };

  /*********************************************************************
   * Function _getDomain(hostname): Get domain from hostname
   *     hostname = Optional hostname to build the domain from.
   *                Defaults to window.location.hostname
   * Returns:
   *     Domain
   *********************************************************************/
  visitor._getDomain = function(hostname) {
    var
      domain;

    if ((!hostname) && (w.location)) {
      hostname = w.location.hostname;
    }
    domain = hostname;
    if (domain) {
      if (!(/^[0-9.]+$/).test(domain)) {
        var
          domain2CharExceptions = ",DOMAIN_2_CHAR_EXCEPTIONS,",
          domainPartList = domain.split("."),
          domainPartNum = (domainPartList.length - 1),
          domainTargetPartNum = (domainPartNum - 1);
        // Take care of two part top level domains like .co.uk
        // The rule is:
        // 1. Right most part is 2 characters or less
        // 2. The next part to the left is not 2 characters
        // 3. The right most part is not in the 2 character exception list
        // that doesn't require a 2 part TLD
        if ((domainPartNum > 1) && (domainPartList[domainPartNum].length <= 2) &&
          ((domainPartList[(domainPartNum - 1)].length == 2) || (domain2CharExceptions.indexOf("," + domainPartList[domainPartNum] + ",") < 0))) {
          domainTargetPartNum--;
        }
        if (domainTargetPartNum > 0) {
          domain = "";
          while (domainPartNum >= domainTargetPartNum) {
            domain = domainPartList[domainPartNum] + (domain ? "." : "") + domain;
            domainPartNum--;
          }
        }
      } else {
        domain = "";
      }
    }

    return domain;
  };

  /*********************************************************************
   * Function cookieRead(k): Read, URL-decode, and return value of k in cookies
   *     k = key to read value for out of cookies
   * Returns:
   *     Value of k in cookies if found, blank if not
   *********************************************************************/
  visitor.cookieRead = function(k) {
    k = encodeURIComponent(k);
    var
      c = (";" + d.cookie).split(" ").join(";"),
      i = c.indexOf(";" + k + "="),
      e = (i < 0 ? i : c.indexOf(";",(i + 1))),
      v = (i < 0 ? "" : decodeURIComponent(c.substring((i + 2 + k.length),(e < 0 ? c.length : e))));
    return v;
  };

  /*********************************************************************
   * Function cookieWrite(k,v,e): Write value v as key k in cookies with
   *                              optional expiration e and domain automaticly
   *                              generated by getCookieDomain()
   *     k = key to write value for in cookies
   *     v = value to write to cookies
   *     e = optional expiration Date object or 1 to use default expiration
   * Returns:
   *     True if value was successfuly written and false if it was not
   *********************************************************************/
  visitor.cookieWrite = function(k,v,e) {
    var
      l = visitor.cookieLifetime,
      t;
    v = "" + v;
    l = (l ? ("" + l).toUpperCase() : "");
    if ((e) && (l != "SESSION") && (l != "NONE")) {
      t = (v != "" ? parseInt((l ? l : 0),10) : -60);
      if (t) {
        e = new Date;
        e.setTime(e.getTime() + (t * 1000));
      } else if (e == 1) {
        e = new Date;
        var y = e.getYear();
        e.setYear( y + 2 + (y < 1900 ? 1900 : 0));
      }
    } else {
      e = 0;
    }
    if ((k) && (l != "NONE")) {
      d.cookie = encodeURIComponent(k) + "=" + encodeURIComponent(v) + "; path=/;"
        + (e ? " expires=" + e.toGMTString() + ";" : "")
        + (visitor.cookieDomain ? " domain=" + visitor.cookieDomain + ";" : "");
      return (visitor.cookieRead(k) == v);
    }
    return 0;
  };

  /*********************************************************************
   * Function _callCallback(callback,args): Call a callback
   *     callback = If this is a function it will just be called.
   *                Otherwise it needs to be an array with two elements
   *                containing the object at index 0 and a function to
   *                call on the object at index 1.
   *     args     = Array of arguments
   * Returns:
   *     Nothing
   *********************************************************************/
  visitor._callbackList = Null;
  visitor._callCallback = function(callback,args) {
    var
      e;
    try {
      if (typeof(callback) == "function") {
        callback.apply(w,args);
      } else {
        callback[1].apply(callback[0],args);
      }
    } catch (e) {}
  };

  /*********************************************************************
   * Function _registerCallback(field,callback): Register callback for field
   *     field    = Field to link callback to
   *     callback = (see _callCallback)
   * Returns:
   *     Nothing
   *********************************************************************/
  visitor._registerCallback = function(field,callback) {
    if (callback) {
      if (visitor._callbackList == Null) {
        visitor._callbackList = {};
      }
      if (visitor._callbackList[field] == Undefined) {
        visitor._callbackList[field] = [];
      }
      visitor._callbackList[field].push(callback);
    }
  };

  /*********************************************************************
   * Function _callAllCallbacks(field,args): CAll all callbacks registered to field
   *     field = Field callbacks are linked to
   *     args  = Array of arguments
   * Returns:
   *     Nothing
   *********************************************************************/
  visitor._callAllCallbacks = function(field,args) {
    if (visitor._callbackList != Null) {
      // Call all of the callbacks
      var
        callbackList = visitor._callbackList[field];
      if (callbackList) {
        while (callbackList.length > 0) {
          visitor._callCallback(callbackList.shift(),args);
        }
      }
    }
  };

  /*********************************************************************
   * Function _addQuerystringParam(url, key, value, location): Adds a param to the querystring
   *                            optionally at a specific location
   *     url      = URL to add the param to
   *     key      = querystring parameter key to add to url
   *     value    = querystring parameter value to add to url
   *     location = optional location index, defaults to end of array
   * Returns:
   *     url with param added to querystring
   *********************************************************************/
  visitor._addQuerystringParam = function(url, key, value, location) {
    var param = encodeURIComponent(key) + "=" + encodeURIComponent(value);

    // Preserve any existing hashes.
    var hash = helpers.parseHash(url);
    var urlWithoutHash = helpers.hashlessUrl(url);

    var hasNoQuerystring = urlWithoutHash.indexOf("?") === -1;
    if (hasNoQuerystring) {
      return urlWithoutHash + "?" + param + hash;
    }

    var urlParts = urlWithoutHash.split("?");
    var host = urlParts[0] + "?";
    var querystring = urlParts[1];

    var params = helpers.addQueryParamAtLocation(querystring, param, location);

    return host + params + hash;
  };

  /*********************************************************************
   * Function _extractParamFromUri(url, paramName): Extracts a query parameter value from a URL.
   *
   *     url        = The URI from which to extract the parameter.
   *     paramName  = The name of the query paramater to extract.
   * Returns:
   *     URL Decoded value of the query paramater. undefined if not found.
   *********************************************************************/
  visitor._extractParamFromUri = function (url, paramName) {
    var re = new RegExp('[\\?&#]' + paramName + '=([^&#]*)');
    var results = re.exec(url);

    if (results && results.length) {
      return decodeURIComponent(results[1]);
    }
  };

  /*********************************************************************
   * Function _parseAdobeMcFromUrl(url): Parse the adobe mc param from the URL into an object set on visitor.
   *
   *     url        = The URI from which to extract the parameter. Default to window.location.search.
   * Returns:
   *     visitor_adobePayload: simple object literal containing the k/v pairs.
   *         example: { MCMID: "12345", MCAID: "5678" }
   *********************************************************************/
  visitor._parseAdobeMcFromUrl = function (url) {
    var adobeMcIds = Null;
    var querystring = url || w.location.href;

    try {
      var adobeMcFromUrl = visitor._extractParamFromUri(querystring, constants.ADOBE_MC);

      if (adobeMcFromUrl) {
        adobeMcIds = {};
        // Parse the '|' delimited k=v pairs into an object.
        var keyValuePairs = adobeMcFromUrl.split("|");
        for (var i = 0, l = keyValuePairs.length; i < l; i++) {
          var tokens = keyValuePairs[i].split("=");
          adobeMcIds[tokens[0]] = decodeURIComponent(tokens[1]);
        }
      }

      return adobeMcIds;
    } catch (ex) {
      adobeMcIds = Null;
    }
  };

  /*
   * '_attemptToPopulateIdsFromUrl' will get called before the initConfig instantiation,
   * basically before any calls have been made.
   *
   * Look for an 'adobe_mc' param in the URL. If found, parse it and look for MCMID or MCAID, and try to set those
   * IDs manually.
   */

  function populateIdsFrom(adobeMcParam) {
    function setIdIfValid(id, setter) {
      if (id && id.match(constants.VALID_VISITOR_ID_REGEX)) {
        setter(id);
      }
    }

    // Those expressions need to stay in this order!
    setIdIfValid(adobeMcParam[fieldMarketingCloudVisitorID], visitor.setMarketingCloudVisitorID);
    visitor._setFieldExpire(fieldAudienceManagerBlob, -1);
    setIdIfValid(adobeMcParam[fieldAnalyticsVisitorID], visitor.setAnalyticsVisitorID);
  }

  visitor._attemptToPopulateIdsFromUrl = function () {
    var adobeMcParam = visitor._parseAdobeMcFromUrl();

    if (adobeMcParam && adobeMcParam['TS']) {
      var now = new Date().getTime();
      var diff = now - adobeMcParam['TS'];
      var adobeMcParamAgeInMin = diff / (60 * 1000);

      if (adobeMcParamAgeInMin > constants.ADOBE_MC_TTL || adobeMcParam[fieldMarketingCloudOrgID] !== marketingCloudOrgID) {
        return;
      }

      populateIdsFrom(adobeMcParam);
    }
  };

  visitor._mergeServerState = function (serverState) {
    function mergeCustomerIDs(ids) {
      if (helpers.isObject(ids)) {
        visitor.setCustomerIDs(ids);
      }
    }

    function mergeSupplementalDataID(sdidState) {
      sdidState = sdidState || {};
      visitor._supplementalDataIDCurrent = sdidState["supplementalDataIDCurrent"] || "";
      visitor._supplementalDataIDCurrentConsumed = sdidState["supplementalDataIDCurrentConsumed"] || {};
      visitor._supplementalDataIDLast = sdidState["supplementalDataIDLast"] || "";
      visitor._supplementalDataIDLastConsumed = sdidState["supplementalDataIDLastConsumed"] || {};
    }

    if (serverState && serverState[visitor.marketingCloudOrgID]) {
      var stateByOrgID = serverState[visitor.marketingCloudOrgID];
      mergeCustomerIDs(stateByOrgID["customerIDs"]);
      mergeSupplementalDataID(stateByOrgID["sdid"]);
    }
  };

  /*********************************************************************
   * Function _loadData(fieldGroup,url,loadErrorHandler,corsData): Load a set of data
   *                            via CORS or JSONP
   *     fieldGroup  = Field group to link the loading/timeout to
   *     url         = URL to make the JSONP call to
   *     loadErrorHandler = Timeout function to call
   *     corsData    = CORS-specific data
   * Returns:
   *     Nothing
   *********************************************************************/
  visitor._timeout = Null;
  visitor._loadData = function(fieldGroup,url,loadErrorHandler,corsData) {
    var fieldGroupParamKey = "d_fieldgroup";

    url = visitor._addQuerystringParam(url, fieldGroupParamKey, fieldGroup, 1);
    corsData.url = visitor._addQuerystringParam(corsData.url, fieldGroupParamKey, fieldGroup, 1);
    corsData.corsUrl = visitor._addQuerystringParam(corsData.corsUrl, fieldGroupParamKey, fieldGroup, 1);

    _callStateTracker.fieldGroupObj[fieldGroup] = True;

    if (corsData === Object(corsData) && corsData.corsUrl && visitor._requestProcs.corsMetadata.corsType === 'XMLHttpRequest') {
      visitor._requestProcs.fireCORS(corsData, loadErrorHandler, fieldGroup);
    } else if (!visitor.useCORSOnly) {
      visitor._loadJSONP(fieldGroup,url,loadErrorHandler);
    }
  };

  /*********************************************************************
   * Function _loadJSONP(fieldGroup,url,loadErrorHandler): Load a set of data
   *                            via JSONP with a timeout
   *     fieldGroup  = Field group to link the loading/timeout to
   *     url         = URL to make the JSONP call to
   *     loadErrorHandler = Timeout function to call
   * Returns:
   *     Nothing
   *********************************************************************/
  visitor._loadJSONP = function(fieldGroup,url,loadErrorHandler) {
    // Create a <script> tag to request the data
    // IMPORTANT: The below code is super paranoid to deal with different DOM frameworks and case sensitivity
    var
      p = 0,
      s = 0,
      e,
      c;

    // Make sure we have a URL before creating the script tag
    if ((url) && (d)) {
      // Find target parent/container tag
      c = 0;
      while ((!p) && (c < 2)) {
        try {
          p = d.getElementsByTagName((c > 0 ? "HEAD" : "head"));
          if ((p) && (p.length > 0)) {
            p = p[0];
          } else {
            p = 0;
          }
        } catch (e) {
          p = 0;
        }
        c++;
      }
      // If the <head> tag is not found try to use the <body> tag as the parent
      // NOTE: We would end up here in a case sensitive DOM with a document that used mixed case for the <head> tag like <Head>
      if (!p) {
        try {
          if (d.body) {
            p = d.body;
          }
        } catch (e) {
          p = 0;
        }
      }

      // Create the script tag if we were able to find a parent
      if (p) {
        c = 0;
        while ((!s) && (c < 2)) {
          try {
            s = d.createElement((c > 0 ? "SCRIPT" : "script"));
          } catch (e) {
            s = 0;
          }
          c++;
        }
      }
    }

    // ERROR: If for some reason we don't have a URL or were unable to find a parent or create the script tag bail out
    if ((!url) || (!p) || (!s)) {
      if (loadErrorHandler) {
        loadErrorHandler();
      }
      return;
    }

    // Fill out the script tag for the JSONP request
    s.type = "text/javascript";
    s.src = url;
    if (p.firstChild) {
      p.insertBefore(s,p.firstChild);
    } else {
      p.appendChild(s);
    }

    var loadTimeout = visitor.loadTimeout;

    _timeoutMetrics.fieldGroupObj[fieldGroup] = {
      requestStart: _timeoutMetrics.millis(),
      url: url,
      d_visid_stg_timeout_captured: loadTimeout,
      d_settimeout_overriden: _timeoutMetrics.getSetTimeoutOverriden(),
      d_visid_cors: 0
    };

    // Set delay for load error handler
    if (loadErrorHandler) {
      if (visitor._timeout == Null) {
        visitor._timeout = new Object();
      }
      visitor._timeout[fieldGroup] = setTimeout(function() {
        loadErrorHandler(True);
      }, loadTimeout);
    }

    // Log the request
    visitor._log.requests.push(url);
  };

  /*********************************************************************
   * Function _clearTimeout(fieldGroup): Clear a timeout tied to a field group
   *     fieldGroup = Field group timeout is linked to
   * Returns:
   *     Nothing
   *********************************************************************/
  visitor._clearTimeout = function(fieldGroup) {
    // Clear timeout
    if ((visitor._timeout != Null) &&
      (visitor._timeout[fieldGroup])) {
      clearTimeout(visitor._timeout[fieldGroup]);
      visitor._timeout[fieldGroup] = 0;
    }
  };

  /*********************************************************************
   * Function isAllowed(): Test to see if the visitor class functionality
   *                       is allowed which currently means the ability
   *                       to set a cookie
   * Returns:
   *     true if allowed or false if not
   *********************************************************************/
  visitor._isAllowedDone = False;
  visitor._isAllowedFlag = False;
  visitor.isAllowed = function() {
    if (!visitor._isAllowedDone) {
      visitor._isAllowedDone = True;
      if ((visitor.cookieRead(visitor.cookieName)) ||
        (visitor.cookieWrite(visitor.cookieName,"T",1))) {
        visitor._isAllowedFlag = True;
      }
    }
    return visitor._isAllowedFlag;
  };

  /*********************************************************************
   * Fields
   *********************************************************************/
  visitor._fields = Null;
  visitor._fieldsExpired = Null;
  // NOTE: The messy construction of these field* variables is to keep the Google closure compiler from
  // just using the string literal everywhere making the end result bigger or creating global variables
  // Marketing Cloud
  var fieldGroupMarketingCloud = thisClass.fieldGroupMarketingCloud;
  if (!fieldGroupMarketingCloud) {
    fieldGroupMarketingCloud = "MC";
  }
  var fieldMarketingCloudVisitorID = thisClass.fieldMarketingCloudVisitorID;
  if (!fieldMarketingCloudVisitorID) {
    fieldMarketingCloudVisitorID = "MCMID";
  }
  var fieldMarketingCloudOrgID = thisClass.fieldMarketingCloudOrgID;
  if (!fieldMarketingCloudOrgID) {
    fieldMarketingCloudOrgID = "MCORGID";
  }
  var fieldMarketingCloudCustomerIDHash = thisClass.fieldMarketingCloudCustomerIDHash;
  if (!fieldMarketingCloudCustomerIDHash) {
    fieldMarketingCloudCustomerIDHash = "MCCIDH";
  }
  var fieldMarketingCloudSyncs = thisClass.fieldMarketingCloudSyncs;
  if (!fieldMarketingCloudSyncs) {
    fieldMarketingCloudSyncs = "MCSYNCS";
  }
  var fieldMarketingCloudSyncsOnPage = thisClass.fieldMarketingCloudSyncsOnPage;
  if (!fieldMarketingCloudSyncsOnPage) {
    fieldMarketingCloudSyncsOnPage = "MCSYNCSOP";
  }
  var fieldMarketingCloudIDCallTimeStamp = thisClass.fieldMarketingCloudIDCallTimeStamp;
  if (!fieldMarketingCloudIDCallTimeStamp) {
    fieldMarketingCloudIDCallTimeStamp = "MCIDTS";
  }
  var fieldMarketingCloudOptOut = thisClass.fieldMarketingCloudOptOut;
  if (!fieldMarketingCloudOptOut) {
    fieldMarketingCloudOptOut = "MCOPTOUT";
  }

  // Analytics
  var fieldGroupAnalytics = thisClass.fieldGroupAnalytics;
  if (!fieldGroupAnalytics) {
    fieldGroupAnalytics = "A";
  }
  var fieldAnalyticsVisitorID = thisClass.fieldAnalyticsVisitorID;
  if (!fieldAnalyticsVisitorID) {
    fieldAnalyticsVisitorID = "MCAID";
  }

  // Audience Manager
  var fieldGroupAudienceManager = thisClass.fieldGroupAudienceManager;
  if (!fieldGroupAudienceManager) {
    fieldGroupAudienceManager = "AAM";
  }
  var fieldAudienceManagerLocationHint = thisClass.fieldAudienceManagerLocationHint;
  if (!fieldAudienceManagerLocationHint) {
    fieldAudienceManagerLocationHint = "MCAAMLH";
  }
  var fieldAudienceManagerBlob = thisClass.fieldAudienceManagerBlob;
  if (!fieldAudienceManagerBlob) {
    fieldAudienceManagerBlob = "MCAAMB";
  }

  var fieldValueNONE = thisClass.fieldValueNONE;
  if (!fieldValueNONE) {
    fieldValueNONE = "NONE";
  }

  /*********************************************************************
   * Function _getSettingsDigest(): Generate the settings digest for this instace
   * Returns:
   *     Nothing
   *********************************************************************/
  visitor._settingsDigest = 0;
  visitor._getSettingsDigest = function() {
    if (!visitor._settingsDigest) {
      var
        settings = visitor.version;
      if (visitor.audienceManagerServer) {
        settings += '|' + visitor.audienceManagerServer;
      }
      if (visitor.audienceManagerServerSecure) {
        settings += '|' + visitor.audienceManagerServerSecure;
      }
      visitor._settingsDigest = visitor._hash(settings);
    }
    return visitor._settingsDigest;
  };

  /*********************************************************************
   * Function _readVisitor(): Read the visitor cookie into instance
   * Returns:
   *     Nothing
   *********************************************************************/
  visitor._readVisitorDone = False;
  visitor._readVisitor = function() {
    if (!visitor._readVisitorDone) {
      visitor._readVisitorDone = True;
      var
        settingsDigest = visitor._getSettingsDigest(),
        settingsDigestChanged = False,
        data = visitor.cookieRead(visitor.cookieName),
        pos,
        parts,
        field,
        value,
        expire,
        expireOnSession,
        now = new Date;
      if (visitor._fields == Null) {
        visitor._fields = {};
      }
      // If this is a valid cookie value parse and go through each key|value pair
      if ((data) && (data != "T")) {
        data = data.split("|");
        // If the cookie starts out with a settings digest
        if (data[0].match(/^[\-0-9]+$/)) {
          if (parseInt(data[0],10) != settingsDigest) {
            settingsDigestChanged = True;
          }
          data.shift();
        }

        if (data.length % 2 == 1) {
          data.pop();
        }
        for (pos = 0;pos < data.length;pos += 2) {
          parts = data[pos].split("-");
          field  = parts[0];
          value  = data[pos + 1];
          if (parts.length > 1) {
            expire = parseInt(parts[1],10);
            expireOnSession = (parts[1].indexOf("s") > 0);
          } else {
            expire = 0;
            expireOnSession = False;
          }
          if (settingsDigestChanged) {
            // If the settings digest has changed clear out the Customer ID hash forcing resyncs
            if (field == fieldMarketingCloudCustomerIDHash) {
              value = "";
            }
            // If the settings digest has changed expire all expiring fields now
            if (expire > 0) {
              expire = ((now.getTime() / 1000) - 60);
            }
          }
          if ((field) && (value)) {
            visitor._setField(field,value,1);
            if (expire > 0) {
              visitor._fields["expire" + field] = expire + (expireOnSession ? "s" : "");
              if ((now.getTime() >= (expire * 1000)) ||
                ((expireOnSession) && (!visitor.cookieRead(visitor.sessionCookieName)))) {
                if (!visitor._fieldsExpired) {
                  visitor._fieldsExpired = {};
                }
                visitor._fieldsExpired[field] = True;
              }
            }
          }
        }
      }

      var trackingServerIsPopulated = visitor.loadSSL ? !!visitor.trackingServerSecure : !!visitor.trackingServer;

      // If we still don't have the analytics visitor ID look for the Mod-Stats created s_vi because we may be on first party data collection where the s_vi cookie is availible
      if (!visitor._getField(fieldAnalyticsVisitorID) && trackingServerIsPopulated) {
        /* s_vi=[CS]v1|28B7854A85160711-40000182A01D8F44[CE]; */
        data = visitor.cookieRead("s_vi");
        if (data) {
          data = data.split("|");
          if ((data.length > 1) &&
            (data[0].indexOf("v1") >= 0)) {
            value = data[1];
            pos = value.indexOf("[");
            if (pos >= 0) {
              value = value.substring(0,pos);
            }
            if ((value) &&
              (value.match(constants.VALID_VISITOR_ID_REGEX))) {
              visitor._setField(fieldAnalyticsVisitorID,value);
            }
          }
        }
      }
    }
  };

  /*********************************************************************
   * Function _writeVisitor(): Write visitor fields out to cookie
   * Returns:
   *     Nothing
   *********************************************************************/
  visitor._writeVisitor = function() {
    var
      data = visitor._getSettingsDigest(), // The first thing in the cookie is the settings digest
      field,
      value;
    for (field in visitor._fields) {
      if ((_isNOP(field)) &&
        (visitor._fields[field]) &&
        (field.substring(0,6) != "expire")) {
        value = visitor._fields[field];
        data += (data ? "|" : "") + field + (visitor._fields["expire" + field] ? "-" + visitor._fields["expire" + field] : "") + "|" + value;
      }
    }
    visitor.cookieWrite(visitor.cookieName,data,1);
  };

  /*********************************************************************
   * Function _getField(field,getExpired): Get value for field
   *     field      = Field to get value for
   *     getExpired = Optional flag to get expired field values
   * Returns:
   *     Field value
   *********************************************************************/
  visitor._getField = function(field,getExpired) {
    if ((visitor._fields != Null) && ((getExpired) || (!visitor._fieldsExpired) || (!visitor._fieldsExpired[field]))) {
      return visitor._fields[field];
    }
    return Null;
  };

  /*********************************************************************
   * Function _setField(field,value,noSave): Set value for field
   *     field  = Field to set value for
   *     value  = Value to set field to
   *     noSave = (option) Don't save the visitor
   * Returns:
   *     Nothing
   *********************************************************************/
  visitor._setField = function(field,value,noSave) {
    if (visitor._fields == Null) {
      visitor._fields = {};
    }
    visitor._fields[field] = value;
    if (!noSave) {
      visitor._writeVisitor();
    }
  };

  /*********************************************************************
   * Function _getFieldList(field,getExpired): Get list value for field
   *     field      = Field to get list value for
   *     getExpired = Optional flag to get expired field values
   * Returns:
   *     Field list value
   *********************************************************************/
  visitor._getFieldList = function(field,getExpired) {
    var value = visitor._getField(field,getExpired);
    if (value) {
      return value.split("*");
    }
    return Null;
  };

  /*********************************************************************
   * Function _setFieldList(field,listValue,noSave): Set list value for field
   *     field     = Field to set list value for
   *     listValue = List value to set field to
   *     noSave    = (option) Don't save the visitor
   * Returns:
   *     Nothing
   *********************************************************************/
  visitor._setFieldList = function(field,listValue,noSave) {
    visitor._setField(field,(listValue ? listValue.join("*") : ""),noSave);
  };

  /*********************************************************************
   * Function _getFieldMap(field,getExpired): Get map value for field
   *     field      = Field to get map value for
   *     getExpired = Optional flag to get expired field values
   * Returns:
   *     Field list value
   *********************************************************************/
  visitor._getFieldMap = function(field,getExpired) {
    var listValue = visitor._getFieldList(field,getExpired);
    if (listValue) {
      var
        mapValue = {},
        i;
      for (i = 0;i < listValue.length;i += 2) {
        mapValue[listValue[i]] = listValue[(i + 1)];
      }
      return mapValue;
    }
    return Null;
  };

  /*********************************************************************
   * Function _setFieldMap(field,mapValue,noSave): Set map value for field
   *     field    = Field to set map value for
   *     mapValue = Map value to set field to
   *     noSave   = (option) Don't save the visitor
   * Returns:
   *     Nothing
   *********************************************************************/
  visitor._setFieldMap = function(field,mapValue,noSave) {
    var
      listValue = Null,
      m;
    if (mapValue) {
      listValue = [];
      for (m in mapValue) {
        if (_isNOP(m)) {
          listValue.push(m);
          listValue.push(mapValue[m]);
        }
      }
    }
    visitor._setFieldList(field,listValue,noSave);
  };

  /*********************************************************************
   * Function _setFieldExpire(field,ttl): Set a field to expire
   *     field = Field to set value for
   *     ttl   = Field TTL in seconds
   `  *     expireOnSession = (optional)
   * Returns:
   *     Nothing
   *********************************************************************/
  visitor._setFieldExpire = function(field,ttl,expireOnSession) {
    var
      now = new Date;
    now.setTime(now.getTime() + (ttl * 1000));
    if (visitor._fields == Null) {
      visitor._fields = {};
    }
    visitor._fields["expire" + field] = Math.floor(now.getTime() / 1000) + (expireOnSession ? "s" : "");
    if (ttl < 0) {
      if (!visitor._fieldsExpired) {
        visitor._fieldsExpired = {};
      }
      visitor._fieldsExpired[field] = True;
    } else if (visitor._fieldsExpired) {
      visitor._fieldsExpired[field] = False;
    }
    if (expireOnSession) {
      if (!visitor.cookieRead(visitor.sessionCookieName)) {
        visitor.cookieWrite(visitor.sessionCookieName,"1");
      }
    }
  };

  /*********************************************************************
   * Function _findVisitorID(visitorID): Find a visitor ID in an object
   *     visitorID = Visitor ID or object containing visitorID
   * Returns:
   *     Visitor ID
   *********************************************************************/
  visitor._findVisitorID = function(visitorID) {
    if (visitorID) {
      // Get the visitor ID
      if (typeof(visitorID) == "object") {
        if (visitorID["d_mid"]) {
          visitorID = visitorID["d_mid"];
        } else if (visitorID["visitorID"]) {
          visitorID = visitorID["visitorID"];
        } else if (visitorID["id"]) {
          visitorID = visitorID["id"];
        } else if (visitorID["uuid"]) {
          visitorID = visitorID["uuid"];
        } else {
          visitorID = "" + visitorID; /* Call toString() method of object */
        }
      }
      // Handle special visitorID values
      if (visitorID) {
        visitorID = visitorID.toUpperCase();
        if (visitorID == "NOTARGET") {
          visitorID = fieldValueNONE;
        }
      }
      // If the visitorID is not valid clear it out
      if ((!visitorID) || ((visitorID != fieldValueNONE) && (!visitorID.match(constants.VALID_VISITOR_ID_REGEX)))) {
        visitorID = "";
      }
    }
    return visitorID;
  };

  /*********************************************************************
   * Function _setFields(field,data): Set fields for fieldGroup
   *     fieldGroup = Field group
   *     data       = Data for fields
   * Returns:
   *     Nothing
   *********************************************************************/
  visitor._setFields = function(fieldGroup,data) {
    // Clear the timeout and loading flag
    visitor._clearTimeout(fieldGroup);
    if (visitor._loading != Null) {
      visitor._loading[fieldGroup] = False;
    }

    if (_timeoutMetrics.fieldGroupObj[fieldGroup]) {
      _timeoutMetrics.fieldGroupObj[fieldGroup].requestEnd = _timeoutMetrics.millis();
      _timeoutMetrics.process(fieldGroup);
    }

    if (_callStateTracker.fieldGroupObj[fieldGroup]) {
      _callStateTracker.setState(fieldGroup, False);
    }

    // Marketing Cloud
    if (fieldGroup == fieldGroupMarketingCloud) {
      if (_callStateTracker.isClientSideMarketingCloudVisitorID !== True) {
        _callStateTracker.isClientSideMarketingCloudVisitorID = False;
      }

      // Marketing Cloud Visitor ID
      var
        marketingCloudVisitorID = visitor._getField(fieldMarketingCloudVisitorID);
      if (!marketingCloudVisitorID || visitor.overwriteCrossDomainMCIDAndAID) {
        if ((typeof(data) == "object") && (data["mid"])) {
          marketingCloudVisitorID = data["mid"];
        } else {
          marketingCloudVisitorID = visitor._findVisitorID(data);
        }
        /***********************************************************************
         * If we don't have a Marketing Cloud ID at this point
         * 1. If there is a 1st party s.marketingCloudServer fire off an Analytics server call to generate a Marketing Cloud Visitor ID
         * 2. If there is not a 1st party s.marketingCloudServer just generate a Marketing Cloud Visitor ID
         ***********************************************************************/
        // We always have to have a Marketing Cloud Visitor ID so if one was not passed in generate one
        if (!marketingCloudVisitorID) {
          if (visitor._use1stPartyMarketingCloudServer) {
            visitor.getAnalyticsVisitorID(Null,False,True);
            return;
          }
          marketingCloudVisitorID = visitor._generateID(0, fieldMarketingCloudVisitorID);
        }
        visitor._setField(fieldMarketingCloudVisitorID,marketingCloudVisitorID);
      }
      if ((!marketingCloudVisitorID) || (marketingCloudVisitorID == fieldValueNONE)) {
        marketingCloudVisitorID = "";
      }

      // Look for other Audience Manager or Analytics data that may have come back from the call to get the Marketing Cloud data
      if (typeof(data) == "object") {
        if ((data["d_region"]) || (data["dcs_region"]) ||
          (data["d_blob"]) || (data["blob"])) {
          visitor._setFields(fieldGroupAudienceManager,data);
        }
        if ((visitor._use1stPartyMarketingCloudServer) && (data["mid"])) {
          visitor._setFields(fieldGroupAnalytics,{"id":data["id"]});
        }
      }

      // Call any Marketing Cloud Visitor ID Callbacks
      visitor._callAllCallbacks(fieldMarketingCloudVisitorID,[marketingCloudVisitorID]);
    }

    // Audience Manager
    if ((fieldGroup == fieldGroupAudienceManager) && (typeof(data) == "object")) {
      // Get TTL for AAM fields
      var
        ttl = 604800; // One week
      if ((data["id_sync_ttl"] != Undefined) && (data["id_sync_ttl"])) {
        ttl = parseInt(data["id_sync_ttl"],10);
      }

      // AAM Location Hint
      var
        aamLH = visitor._getField(fieldAudienceManagerLocationHint);
      if (!aamLH) {
        aamLH = data["d_region"];
        if (!aamLH) {
          aamLH = data["dcs_region"];
        }
        if (aamLH) {
          visitor._setFieldExpire(fieldAudienceManagerLocationHint,ttl);
          visitor._setField(fieldAudienceManagerLocationHint,aamLH);
        }
      }
      if (!aamLH) {
        aamLH = "";
      }
      // Call any Audience Manager Location Hint callbacks
      visitor._callAllCallbacks(fieldAudienceManagerLocationHint,[aamLH]);

      // AAM Blob
      var
        aamBlob = visitor._getField(fieldAudienceManagerBlob);
      if ((data["d_blob"]) || (data["blob"])) {
        aamBlob = data["d_blob"];
        if (!aamBlob) {
          aamBlob = data["blob"];
        }
        visitor._setFieldExpire(fieldAudienceManagerBlob,ttl);
        visitor._setField(fieldAudienceManagerBlob,aamBlob);
      }
      if (!aamBlob) {
        aamBlob = "";
      }
      // Call any Audience Manager Blob callbacks
      visitor._callAllCallbacks(fieldAudienceManagerBlob,[aamBlob]);

      /*
       * We are using the Audience Manager /id service as the Customer ID mapping service so if we recieve a response from
       * Audience Manager without an error we should apply the _newCustomerIDsHash marking the mapping as successful
       */
      if ((!data["error_msg"]) && (visitor._newCustomerIDsHash)) {
        visitor._setField(fieldMarketingCloudCustomerIDHash,visitor._newCustomerIDsHash);
      }
    }

    // Analytics
    if (fieldGroup == fieldGroupAnalytics) {
      // Analytics Visitor ID
      var
        analyticsVisitorID = visitor._getField(fieldAnalyticsVisitorID);
      if (!analyticsVisitorID || visitor.overwriteCrossDomainMCIDAndAID) {
        analyticsVisitorID = visitor._findVisitorID(data);
        // If we don't have an Analytics visitor ID store the special NONE value so we don't keep trying to request it
        if (!analyticsVisitorID) {
          analyticsVisitorID = fieldValueNONE;
        } else if (analyticsVisitorID !== fieldValueNONE) {
          visitor._setFieldExpire(fieldAudienceManagerBlob,-1);
        }
        visitor._setField(fieldAnalyticsVisitorID,analyticsVisitorID);
      }
      if ((!analyticsVisitorID) || (analyticsVisitorID == fieldValueNONE)) {
        analyticsVisitorID = "";
      }
      // Call any Analytics Visitor ID callbacks
      visitor._callAllCallbacks(fieldAnalyticsVisitorID,[analyticsVisitorID]);
    }

    // Handle ID Syncs
    if (!visitor.idSyncDisableSyncs) {
      destinationPublishing.idCallNotProcesssed = False;

      var json = {};

      json['ibs'] = data['ibs'];
      json['subdomain'] = data['subdomain'];

      destinationPublishing.processIDCallData(json);
    } else {
      destinationPublishing.idCallNotProcesssed = True;
    }

    // Handle opt out
    if (data === Object(data)) {
      var optOut,
        d_ottl;

      if (visitor.isAllowed()) {
        // Check if the cookie has been set already
        optOut = visitor._getField(fieldMarketingCloudOptOut);
      }

      if (!optOut) {
        optOut = fieldValueNONE; // We always need a value even if it's none

        if ((data["d_optout"]) && (data["d_optout"] instanceof Array)) {
          optOut = data["d_optout"].join(",");
        }

        d_ottl = parseInt(data['d_ottl'], 10);

        if (isNaN(d_ottl)) {
          d_ottl = 7200; // 2 hours
        }

        visitor._setFieldExpire(fieldMarketingCloudOptOut,d_ottl,True /* expireOnSession */);
        visitor._setField(fieldMarketingCloudOptOut,optOut);
      }

      visitor._callAllCallbacks(fieldMarketingCloudOptOut,[optOut]);
    }
  };

  /*********************************************************************
   * Function _getRemoteField(field,url,callback): Get a remote field
   *     field             = Field
   *     url               = URL to load field from
   *     callback          = Optional callback to call if field isn't ready yet
   *                         available yet.  (See _callCallback)
   *     forceCallCallback = Optional flag to call the callback
   * Returns:
   *     Blank field value if not allowed or not ready
   *     Field value if ready
   *********************************************************************/
  visitor._loading = Null;
  visitor._getRemoteField = function(field,url,callback,forceCallCallback,corsData) {
    var
      fieldValue = "",
      fieldGroup,
      isFirstPartyAnalyticsVisitorIDCall = helpers.isFirstPartyAnalyticsVisitorIDCall(field);

    // Make sure we can actually support this
    if (visitor.isAllowed()) {
      // Get the current value and see if we already have what we need
      visitor._readVisitor();

      // Get even if expired if it's a non-blocking expiration field
      fieldValue = visitor._getField(field, fieldsNonBlockingExpiration[field] === True);

      if (visitor.disableThirdPartyCalls && !fieldValue) {
        if (field === fieldMarketingCloudVisitorID) {
          fieldValue = visitor._generateID(0, fieldMarketingCloudVisitorID);
          visitor.setMarketingCloudVisitorID(fieldValue);
        } else if (field === fieldAnalyticsVisitorID) {
          if (!isFirstPartyAnalyticsVisitorIDCall) {
            fieldValue = "";
            visitor.setAnalyticsVisitorID(fieldValue);
          }
        }
      }

      var shouldCallServer = function() {
        return (!fieldValue || (visitor._fieldsExpired && visitor._fieldsExpired[field])) &&
          (!visitor.disableThirdPartyCalls || isFirstPartyAnalyticsVisitorIDCall);
      };

      if (shouldCallServer()) {
        // Generate field group
        if ((field == fieldMarketingCloudVisitorID) || (field == fieldMarketingCloudOptOut)) {
          fieldGroup = fieldGroupMarketingCloud;
        } else if ((field == fieldAudienceManagerLocationHint) || (field == fieldAudienceManagerBlob)) {
          fieldGroup = fieldGroupAudienceManager;
        } else if (field == fieldAnalyticsVisitorID) {
          fieldGroup = fieldGroupAnalytics;
        }
        // Make sure we have a known field group
        if (fieldGroup) {
          // Make sure we have a url only do this once
          if ((url) && ((visitor._loading == Null) || (!visitor._loading[fieldGroup]))) {
            if (visitor._loading == Null) {
              visitor._loading = {};
            }
            visitor._loading[fieldGroup] = True;
            visitor._loadData(fieldGroup,url,function(isActualTimeout, corsDataFromError) {
              if (!visitor._getField(field)) {
                if (_timeoutMetrics.fieldGroupObj[fieldGroup]) {
                  _timeoutMetrics.fieldGroupObj[fieldGroup].timeout = _timeoutMetrics.millis();
                  _timeoutMetrics.fieldGroupObj[fieldGroup].isActualTimeout = !!isActualTimeout;
                  _timeoutMetrics.process(fieldGroup);
                }

                // If it's a CORS error (not a CORS timeout) and useCORSOnly is falsey, fall back to JSONP
                if (corsDataFromError === Object(corsDataFromError) && !visitor.useCORSOnly) {
                  visitor._loadJSONP(fieldGroup,corsDataFromError.url,corsDataFromError.loadErrorHandler);

                  return;
                }

                if (isActualTimeout) {
                  _callStateTracker.setState(fieldGroup, True);
                }

                var
                  fallbackValue = "";
                if (field == fieldMarketingCloudVisitorID) {
                  fallbackValue = visitor._generateID(0, fieldMarketingCloudVisitorID);
                } else if (fieldGroup == fieldGroupAudienceManager) {
                  // IMPORTANT: For the AAM group the value must always be an object and we include a timeout error so we will try again on the next page
                  fallbackValue = {"error_msg" : "timeout"};
                }
                visitor._setFields(fieldGroup,fallbackValue);
              }
            }, corsData);
          }

          if (fieldValue) {
            return fieldValue;
          }

          visitor._registerCallback(field,callback);
          // If we don't have a url set the fields to a default so all callbacks will be wrapped up
          if (!url) {
            visitor._setFields(fieldGroup,{"id":fieldValueNONE});
          }
          return "";
        }
      }
    }

    // If the field value is a visitor ID and it's the special NONE value clear out the return and force the callback to be called
    if (((field == fieldMarketingCloudVisitorID) || (field == fieldAnalyticsVisitorID)) &&
      (fieldValue == fieldValueNONE)) {
      fieldValue = "";
      forceCallCallback = True;
    }

    // If we have a callback and forceCallCallback is set or disableThridPartyCalls is turned on, call the callback.
    // When disableThridPartyCalls is turned, no server calls are made, which means no callbacks will be called. We want
    // to make sure callbacks are always called. (MCID-223)
    if ((callback) && (forceCallCallback || visitor.disableThirdPartyCalls)) {
      visitor._callCallback(callback,[fieldValue]);
    }

    return fieldValue;
  };

  /*********************************************************************
   * Function _setMarketingCloudFields(marketingCloudData): Set Marketing Cloud fields
   *      marketingCloudData = Marketing Cloud Data
   * Returns:
   *     Nothing
   * Notes:
   *     See _setFields
   *********************************************************************/
  visitor._setMarketingCloudFields = function(marketingCloudData) {
    visitor._readVisitor();
    visitor._setFields(fieldGroupMarketingCloud,marketingCloudData);
  };

  /*********************************************************************
   * Function setMarketingCloudVisitorID(marketingCloudVisitorID): Set the Marketing Cloud Visitor ID
   *     marketingCloudVisitorID = Marketing Cloud Visitor ID
   * Returns:
   *     Nothing
   * Notes:
   *     See _setMarketingCloudFields
   *********************************************************************/
  visitor.setMarketingCloudVisitorID = function(marketingCloudVisitorID) {
    visitor._setMarketingCloudFields(marketingCloudVisitorID);
  };

  /*********************************************************************
   * Function getMarketingCloudVisitorID(callback,forceCallCallback): Get the Marketing Cloud Visitor ID
   *     callback          = Optional callback to register if visitor ID isn't
   *                         ready yet
   *     forceCallCallback = Option flag to force calling callback because
   *                         the return will not be checked
   * Returns:
   *     Blank visitor ID if not allowed or not ready
   *     Visitor ID if ready
   * Notes:
   *     See _getRemoteField
   *********************************************************************/
  visitor._use1stPartyMarketingCloudServer = False;
  visitor.getMarketingCloudVisitorID = function(callback,forceCallCallback) {
    // Make sure we can actually support this
    if (visitor.isAllowed()) {
      if ((visitor.marketingCloudServer) && (visitor.marketingCloudServer.indexOf(".demdex.net") < 0)) {
        visitor._use1stPartyMarketingCloudServer = True;
      }
      var
        corsData = visitor._getAudienceManagerURLData("_setMarketingCloudFields"),
        url = corsData.url;

      return visitor._getRemoteField(fieldMarketingCloudVisitorID,url,callback,forceCallCallback, corsData);
    }
    return "";
  };

  /*********************************************************************
   * Function _mapCustomerIDs(): Fire off mapping call for Customer IDs
   * Returns:
   *     Nothing
   *********************************************************************/
  visitor._mapCustomerIDs = function() {
    /*
     * We using the Audience Manager /id service for the Customer ID mapping and the AAM blob isd
     * already tied to the Customer ID hash changing so mapping is triggered by simply asking for
     * the AAM blob again.  We're not using a callback here because we are depending on _setFields
     * to apply the _newCustomerIDsHash if there wasn't an error
     */
    visitor.getAudienceManagerBlob();
  };

  /*********************************************************************
   * Function setCustomerIDs(customerIDs): Set the map of Customer IDs
   *     customerIDs = A map of customerIDType = customerID pairs
   * Returns:
   *     Nothing
   *********************************************************************/
  thisClass.AuthState = {
    "UNKNOWN":0,
    "AUTHENTICATED":1,
    "LOGGED_OUT":2
  };
  visitor._currentCustomerIDs = {};
  visitor._customerIDsHashChanged = False;
  visitor._newCustomerIDsHash = "";
  visitor.setCustomerIDs = function(customerIDs) {
    // Make sure we can actually support this
    if ((visitor.isAllowed()) && (customerIDs)) {
      // Get the current value and see if we already have what we need
      visitor._readVisitor();

      // Update the current customer IDs and authState enum
      var
        cidt,
        cid;
      for (cidt in customerIDs) {
        if (_isNOP(cidt)) {
          cid = customerIDs[cidt];
          if (cid) {
            if (typeof(cid) == "object") {
              var ccid = {};
              if (cid["id"]) {
                ccid["id"] = cid["id"];
              }
              if (cid["authState"] != Undefined) {
                ccid["authState"] = cid["authState"];
              }
              visitor._currentCustomerIDs[cidt] = ccid;
            } else {
              visitor._currentCustomerIDs[cidt] = {"id":cid};
            }
          }
        }
      }

      var
        customerIDsWithAuthState = visitor.getCustomerIDs(),
        customerIDsHash = visitor._getField(fieldMarketingCloudCustomerIDHash),
        customerIDsSerialized = "";
      if (!customerIDsHash) {
        customerIDsHash = 0;
      }
      for (cidt in customerIDsWithAuthState) {
        if (_isNOP(cidt)) {
          cid = customerIDsWithAuthState[cidt];
          customerIDsSerialized += (customerIDsSerialized ? "|" : "") + cidt + "|" + (cid["id"] ? cid["id"] : "") + (cid["authState"] ? cid["authState"] : "");
        }
      }
      visitor._newCustomerIDsHash = visitor._hash(customerIDsSerialized);
      if (visitor._newCustomerIDsHash != customerIDsHash) {
        visitor._customerIDsHashChanged = True;

        // Sync with mapping services
        visitor._mapCustomerIDs();
      }
    }
  };

  /*********************************************************************
   * Function getCustomerIDs(): Get Customer IDs set by setCustomerIDs and
   *                            the auth-state for each customer ID type
   * Returns:
   *     Customer IDs and auth-states
   *     {
    *          [customerIDType1]:{
    *               "id":[customerID1],
    *               "authState":[authState1]
    *          },
    *          [customerIDType2]:{
    *               "id":[customerID2],
    *               "authState":[authState2]
    *          }
    *          ...
    *     }
   *********************************************************************/
  visitor.getCustomerIDs = function() {
    visitor._readVisitor();
    var
      customerIDs = {},
      cidt,
      cid;
    // Pull in the currently provided customer IDs and authenticated states
    for (cidt in visitor._currentCustomerIDs) {
      if (_isNOP(cidt)) {
        cid = visitor._currentCustomerIDs[cidt];
        if (!customerIDs[cidt]) {
          customerIDs[cidt] = {};
        }
        if (cid["id"]) {
          customerIDs[cidt]["id"] = cid["id"];
        }
        if (cid["authState"] != Undefined) {
          customerIDs[cidt]["authState"] = cid["authState"];
        } else {
          customerIDs[cidt]["authState"] = thisClass.AuthState["UNKNOWN"];
        }
      }
    }
    return customerIDs;
  };

  /*********************************************************************
   * Function _setAnalyticsFields(analyticsData): Set the Analytics fields
   *     analyticsData = Analytics data
   * Returns:
   *     Nothing
   * Notes:
   *     See _setFields
   *********************************************************************/
  visitor._setAnalyticsFields = function(analyticsData) {
    visitor._readVisitor();
    visitor._setFields(fieldGroupAnalytics,analyticsData);
  };

  /*********************************************************************
   * Function setAnalyticsVisitorID(analyticsVisitorID): Set the analytics visitor ID
   *     analyticsVisitorID = Analytics visitor ID
   * Returns:
   *     Nothing
   * Notes:
   *     See _setAnalyticsFields
   *********************************************************************/
  visitor.setAnalyticsVisitorID = function(analyticsVisitorID) {
    visitor._setAnalyticsFields(analyticsVisitorID);
  };

  /*********************************************************************
   * Function getAnalyticsVisitorID(callback,forceCallCallback,gettingMarketingCloudVisitorID): Get the analytics visitor ID
   *     callback                       = Optional callback to register if visitor ID isn't ready yet
   *     forceCallCallback              = Option flag to force calling callback because the return will not be checked
   *     gettingMarketingCloudVisitorID = Option flag to also get the Marketing Cloud Visitor ID from the Analytics /id service
   * Returns:
   *     Blank visitor ID if not allowed or not ready
   *     Visitor ID if ready
   * Notes:
   *     See _getRemoteField
   *********************************************************************/
  visitor.getAnalyticsVisitorID = function(callback,forceCallCallback,gettingMarketingCloudVisitorID) {
    // Make sure we can actually support this
    if (visitor.isAllowed()) {
      var marketingCloudVisitorID = "";
      if (!gettingMarketingCloudVisitorID) {
        marketingCloudVisitorID = visitor.getMarketingCloudVisitorID(function(newMarketingCloudVisitorID){
          visitor.getAnalyticsVisitorID(callback,True);
        });
      }
      if ((marketingCloudVisitorID) || (gettingMarketingCloudVisitorID)) {
        var
          server = (gettingMarketingCloudVisitorID ? visitor.marketingCloudServer : visitor.trackingServer),
          url = "";
        if (visitor.loadSSL) {
          if (gettingMarketingCloudVisitorID) {
            if (visitor.marketingCloudServerSecure) {
              server = visitor.marketingCloudServerSecure;
            }
          } else if (visitor.trackingServerSecure) {
            server = visitor.trackingServerSecure;
          }
        }

        var corsData = {};

        if (server) {
          var baseUrl = "http" + (visitor.loadSSL ? "s" : "") + "://" + server
            + "/id";

          var queryData = "d_visid_ver=" + visitor.version
              + "&mcorgid=" + encodeURIComponent(visitor.marketingCloudOrgID)
              + (marketingCloudVisitorID ? "&mid=" + encodeURIComponent(marketingCloudVisitorID) : "")
              + (visitor.idSyncDisable3rdPartySyncing ? "&d_coppa=true" : "")
            ;

          var callbackInfo = ["s_c_il", visitor._in, "_set" + (gettingMarketingCloudVisitorID ? "MarketingCloud" : "Analytics") + "Fields"];

          url = baseUrl
            + "?"
            + queryData
            + "&callback=s_c_il%5B" + visitor._in + "%5D._set" + (gettingMarketingCloudVisitorID ? "MarketingCloud" : "Analytics") + "Fields"
          ;

          corsData.corsUrl = baseUrl + "?" + queryData;
          corsData.callback = callbackInfo;
        }

        corsData.url = url;

        return visitor._getRemoteField((gettingMarketingCloudVisitorID ? fieldMarketingCloudVisitorID : fieldAnalyticsVisitorID),url,callback,forceCallCallback,corsData);
      }
    }
    return "";
  };

  /*********************************************************************
   * Function _setAudienceManagerFields(audienceManagerData): Set the AudienceManager fields
   *     audienceManagerData = AudienceManager data
   * Returns:
   *     Nothing
   * Notes:
   *     See _setFields
   *********************************************************************/
  visitor._setAudienceManagerFields = function(audienceManagerData) {
    visitor._readVisitor();
    visitor._setFields(fieldGroupAudienceManager,audienceManagerData);
  };

  /*********************************************************************
   * Function _getAudienceManagerURLData(jsonpCallback): Generate AAM request URL data
   *     jsonpCallback = Optional JSONP callback function name
   * Returns:
   *     AAM Request URL Data
   *********************************************************************/
  visitor._getAudienceManagerURLData = function(jsonpCallback) {
    var
      server = visitor.audienceManagerServer,
      url = "",
      marketingCloudVisitorID = visitor._getField(fieldMarketingCloudVisitorID),
      blob = visitor._getField(fieldAudienceManagerBlob,True),
      analyticsVisitorID = visitor._getField(fieldAnalyticsVisitorID),
      customerIDs = ((analyticsVisitorID) && (analyticsVisitorID != fieldValueNONE) ? "&d_cid_ic=AVID%01" + encodeURIComponent(analyticsVisitorID) : "");
    if ((visitor.loadSSL) && (visitor.audienceManagerServerSecure)) {
      server = visitor.audienceManagerServerSecure;
    }
    if (server) {
      var
        customerIDsWithAuthState = visitor.getCustomerIDs(),
        cidt,
        cid;
      if (customerIDsWithAuthState) {
        for (cidt in customerIDsWithAuthState) {
          if (_isNOP(cidt)) {
            cid = customerIDsWithAuthState[cidt];
            customerIDs += "&d_cid_ic=" + encodeURIComponent(cidt) + "%01" + encodeURIComponent((cid["id"] ? cid["id"] : "")) + (cid["authState"] ? "%01" + cid["authState"] : "");
          }
        }
      }
      if (!jsonpCallback) {
        jsonpCallback = "_setAudienceManagerFields";
      }

      var  baseUrl = "http" + (visitor.loadSSL ? "s" : "") + "://" + server
        + "/id";

      var queryData = "d_visid_ver=" + visitor.version
        + "&d_rtbd=json"
        + "&d_ver=2"
        + ((!marketingCloudVisitorID) && (visitor._use1stPartyMarketingCloudServer) ? "&d_verify=1" : "")
        + "&d_orgid=" + encodeURIComponent(visitor.marketingCloudOrgID)
        + "&d_nsid=" + (visitor.idSyncContainerID || 0)
        + (marketingCloudVisitorID ? "&d_mid=" + encodeURIComponent(marketingCloudVisitorID) : "")
        + (visitor.idSyncDisable3rdPartySyncing ? "&d_coppa=true" : "")
        + (blob ? "&d_blob=" + encodeURIComponent(blob) : "")
        + customerIDs;

      var callbackInfo = ["s_c_il", visitor._in, jsonpCallback];

      url = baseUrl
        + "?"
        + queryData
        + "&d_cb=s_c_il%5B" + visitor._in + "%5D." + jsonpCallback;

      return {
        url: url,
        corsUrl: baseUrl + "?" + queryData,
        callback: callbackInfo
      };
    }

    return {
      url: url
    }
  };

  /*********************************************************************
   * Function getAudienceManagerLocationHint(callback,forceCallCallback): Get the AudienceManager Location Hint
   *     callback          = Optional callback to register if location hint isn't ready
   *     forceCallCallback = Option flag to force calling callback because
   *                         the return will not be checked
   * Returns:
   *     Blank location hint if not allowed or not ready
   *     Location hint if ready
   * Notes:
   *     See _getRemoteField
   *********************************************************************/
  visitor.getAudienceManagerLocationHint = function(callback,forceCallCallback) {
    // Make sure we can actually support this
    if (visitor.isAllowed()) {
      var marketingCloudVisitorID = visitor.getMarketingCloudVisitorID(function(newMarketingCloudVisitorID){
        visitor.getAudienceManagerLocationHint(callback,True);
      });
      if (marketingCloudVisitorID) {
        var analyticsVisitorID = visitor._getField(fieldAnalyticsVisitorID);
        if (!analyticsVisitorID) {
          analyticsVisitorID = visitor.getAnalyticsVisitorID(function(newAnalyticsVisitorID){
            visitor.getAudienceManagerLocationHint(callback,True);
          });
        }
        if (analyticsVisitorID) {
          var
            corsData = visitor._getAudienceManagerURLData(),
            url = corsData.url;

          return visitor._getRemoteField(fieldAudienceManagerLocationHint,url,callback,forceCallCallback, corsData);
        }
      }
    }
    return "";
  };

  /*********************************************************************
   * Function getLocationHint(callback,forceCallCallback): Proxy of getAudienceManagerLocationHint
   *********************************************************************/
  visitor.getLocationHint = visitor.getAudienceManagerLocationHint;

  /*********************************************************************
   * Function getAudienceManagerBlob(callback,forceCallCallback): Get the AudienceManager blob
   *     callback          = Optional callback to register if blob isn't ready
   *     forceCallCallback = Option flag to force calling callback because
   *                         the return will not be checked
   * Returns:
   *     Blank blob if not allowed or not ready
   *     Blob if ready
   * Notes:
   *     See _getRemoteField
   *********************************************************************/
  visitor.getAudienceManagerBlob = function(callback,forceCallCallback) {
    // Make sure we can actually support this
    if (visitor.isAllowed()) {
      var marketingCloudVisitorID = visitor.getMarketingCloudVisitorID(function(newMarketingCloudVisitorID){
        visitor.getAudienceManagerBlob(callback,True);
      });
      if (marketingCloudVisitorID) {
        var analyticsVisitorID = visitor._getField(fieldAnalyticsVisitorID);
        if (!analyticsVisitorID) {
          analyticsVisitorID = visitor.getAnalyticsVisitorID(function(newAnalyticsVisitorID){
            visitor.getAudienceManagerBlob(callback,True);
          });
        }
        if (analyticsVisitorID) {
          var
            corsData = visitor._getAudienceManagerURLData(),
            url = corsData.url;

          if (visitor._customerIDsHashChanged) {
            visitor._setFieldExpire(fieldAudienceManagerBlob,-1);
          }
          return visitor._getRemoteField(fieldAudienceManagerBlob,url,callback,forceCallCallback, corsData);
        }
      }
    }
    return "";
  };

  /*********************************************************************
   * Function getSupplementalDataID(consumerID,noGenerate): Get a supplemental-data ID for the consumer
   *     consumerID = Consumer ID requesting supplemental-data ID (AppMeasurement instance number, client-code+mbox ID, etc...)
   *     noGenerate = Optional flag to not generate a new supplemental-data ID if there isn't a current one
   * Returns:
   *     Hit-stitching ID to use for a single event
   *********************************************************************/
  visitor._supplementalDataIDCurrent = "";
  visitor._supplementalDataIDCurrentConsumed = {};
  visitor._supplementalDataIDLast = "";
  visitor._supplementalDataIDLastConsumed = {};
  visitor.getSupplementalDataID = function(consumerID,noGenerate) {
    // If we don't have a current supplemental-data ID generate one if needed
    if ((!visitor._supplementalDataIDCurrent) && (!noGenerate)) {
      visitor._supplementalDataIDCurrent = visitor._generateID(1);
    }
    // Default to using the current supplemental-data ID
    var
      supplementalDataID = visitor._supplementalDataIDCurrent;
    // If we have the last supplemental-data ID that has not been consumed by this consumer...
    if ((visitor._supplementalDataIDLast) && (!visitor._supplementalDataIDLastConsumed[consumerID])) {
      // Use the last supplemental-data ID
      supplementalDataID = visitor._supplementalDataIDLast;
      // Mark the last supplemental-data ID as consumed for this consumer
      visitor._supplementalDataIDLastConsumed[consumerID] = True;
      // If we are using te current supplemental-data ID at this point and we have a supplemental-data ID...
    } else if (supplementalDataID) {
      // If the current supplemental-data ID has already been consumed by this consumer..
      if (visitor._supplementalDataIDCurrentConsumed[consumerID]) {
        // Move the current supplemental-data ID to the last including the current consumed list
        visitor._supplementalDataIDLast            = visitor._supplementalDataIDCurrent;
        visitor._supplementalDataIDLastConsumed    = visitor._supplementalDataIDCurrentConsumed;
        // Generate a new current supplemental-data ID if needed, use it, and clear the current consumed list
        visitor._supplementalDataIDCurrent         = supplementalDataID = (!noGenerate ? visitor._generateID(1) : '');
        visitor._supplementalDataIDCurrentConsumed = {};
      }
      // If we still have a supplemental-data ID mark the current supplemental-data ID as consumed by this consumer
      if (supplementalDataID) {
        visitor._supplementalDataIDCurrentConsumed[consumerID] = True;
      }
    }
    // Return the supplemental-data ID to use
    return supplementalDataID;
  };

  thisClass.OptOut = {
    GLOBAL:'global'
  };

  /*
   Returns:
   'global' - global opt-out
   'NONE' - no opt-out
   '' - unknown and you need to wait for the callback to be called
   */
  visitor.getOptOut = function(callback,forceCallCallback) {
    if (visitor.isAllowed()) {
      var
        corsData = visitor._getAudienceManagerURLData("_setMarketingCloudFields"),
        url = corsData.url;

      return visitor._getRemoteField(fieldMarketingCloudOptOut,url,callback,forceCallCallback, corsData);
    }
    return "";
  };

  /*
   Returns:
   true - opted-out
   false - not opted-out
   null - unknown and you need to wait for the callback to be called
   */
  visitor.isOptedOut = function(callback,optOutType,forceCallCallback) {
    if (visitor.isAllowed()) {
      // Default to optOutType global
      if (!optOutType) {
        optOutType = thisClass["OptOut"]["GLOBAL"];
      }

      var optOut = visitor.getOptOut(function(optOut) {
        var isOptedOut = (optOut == thisClass["OptOut"]["GLOBAL"]) || (optOut.indexOf(optOutType) >= 0);

        visitor._callCallback(callback, [isOptedOut]);
      }, forceCallCallback);

      if (optOut) {
        return ((optOut == thisClass["OptOut"]["GLOBAL"]) || (optOut.indexOf(optOutType) >= 0));
      }
      return Null;
    }
    return False;
  };

  function generateAdobeMcParam(fields) { // fields: array of tuples.
    var mcParam = "";

    function appendToMcParam(key, value, mcParam) {
      mcParam = mcParam ? (mcParam += "|") : mcParam;
      mcParam += key + "=" + encodeURIComponent(value);
      return mcParam;
    }

    function appendCreationTimestamp(mcParam) {
      var ts = new Date().getTime();
      mcParam = mcParam ? (mcParam += "|") : mcParam;
      mcParam += "TS=" + ts;
      return mcParam;
    }

    for (var i = 0, l = fields.length; i < l; i++) {
      var fieldTokens = fields[i];
      var key = fieldTokens[0];
      var value = fieldTokens[1];

      if (value != Null && value !== fieldValueNONE) {
        mcParam = appendToMcParam(key, value, mcParam);
      }
    }

    return appendCreationTimestamp(mcParam);
  }

  /*********************************************************************
   * Function appendVisitorIDsTo(url): Adds an 'adobe_mc' query param to a url for cross domain support.
   *     url                   = URL to add the query param to.
   *     adobe_mc format  = encodeURIComponent(MCMID=encodeURIComponent(value1)|MCAID=...)
   *     adobe_mc content = MCMID, MCAID, MCORGID
   *
   * Returns:
   *     URL with `adobe_mc` param added to it if the IDs were found, otherwise url as is.
   *********************************************************************/
  visitor.appendVisitorIDsTo = function (url) {
    var adobeMcKey = constants.ADOBE_MC;
    var fields = [[fieldMarketingCloudVisitorID, visitor._getField(fieldMarketingCloudVisitorID)],
      [fieldAnalyticsVisitorID, visitor._getField(fieldAnalyticsVisitorID)],
      [fieldMarketingCloudOrgID, visitor.marketingCloudOrgID]];
    var adobeMcValue = generateAdobeMcParam(fields);

    try {
      return visitor._addQuerystringParam(url, adobeMcKey, adobeMcValue);
    } catch (ex) {
      return url;
    }
  };

  /* Constants */
  var constants = {
    POST_MESSAGE_ENABLED: !!w.postMessage,
    DAYS_BETWEEN_SYNC_ID_CALLS: 1,
    MILLIS_PER_DAY: 24 * 60 * 60 * 1000,
    ADOBE_MC: "adobe_mc",
    VALID_VISITOR_ID_REGEX: /^[0-9a-fA-F\-]+$/,
    ADOBE_MC_TTL: 5
  };

  visitor._constants = constants;

  /*
   * a backwards compatible implementation of postMessage
   * by Josh Fraser (joshfraser.com)
   * released under the Apache 2.0 license.
   *
   * this code was adapted from Ben Alman's jQuery postMessage code found at:
   * http://benalman.com/projects/jquery-postmessage-plugin/
   *
   * other inspiration was taken from Luke Shepard's code for Facebook Connect:
   * http://github.com/facebook/connect-js/blob/master/src/core/xd.js
   *
   * the goal of this project was to make a backwards compatable version of postMessage
   * without having any dependency on jQuery or the FB Connect libraries
   *
   * my goal was to keep this as terse as possible since my own purpose was to use this
   * as part of a distributed widget where filesize could be sensative.
   *
   */
  visitor._xd = {
    /*********************************************************************
     * Function postMessage(message, target_url, target): Post message to iframe
     *     message = specially formatted data
     *     target_url = iframe src
     *     target = iframe.contentWindow
     * Returns:
     *     Nothing
     *********************************************************************/
    postMessage: function (message, target_url, target) {
      var cache_bust = 1;

      if (!target_url) {
        return;
      }

      if (constants.POST_MESSAGE_ENABLED) {
        // the browser supports window.postMessage, so call it with a targetOrigin
        // set appropriately, based on the target_url parameter.
        target.postMessage(message, target_url.replace(/([^:]+:\/\/[^\/]+).*/, '$1'));

      } else if (target_url) {
        // the browser does not support window.postMessage, so set the location
        // of the target to target_url#message. A bit ugly, but it works! A cache
        // bust parameter is added to ensure that repeat messages trigger the callback.
        target.location = target_url.replace(/#.*$/, '') + '#' + (+new Date()) + (cache_bust++) + '&' + message;
      }
    },
    /*********************************************************************
     * Function receiveMessage(callback, source_origin): receive message from iframe
     *     callback = function
     *     source_origin = iframe hostname
     * Returns:
     *     Nothing
     *********************************************************************/
    receiveMessage: function(callback, source_origin) {
      var attached_callback;

      try {
        // browser supports window.postMessage
        if (constants.POST_MESSAGE_ENABLED) {
          // bind the callback to the actual event associated with window.postMessage
          if (callback) {
            attached_callback = function (e) {
              if ((typeof source_origin === 'string' && e.origin !== source_origin) || (Object.prototype.toString.call(source_origin) === '[object Function]' && source_origin(e.origin) === !1)) {
                return !1;
              }
              callback(e);
            };
          }
          if (window.addEventListener) {
            window[callback ? 'addEventListener' : 'removeEventListener']('message', attached_callback, !1);
          } else {
            window[callback ? 'attachEvent' : 'detachEvent']('onmessage', attached_callback);
          }
        }
      } catch (__Error__) {}
    }
  };

  /* Helpers */
  var helpers = {
    /*********************************************************************
     * Function addListener(element, eventType, callback): Add cross-browser event listener
     *     element = DOM element
     *     eventType = e.g., 'load'
     *     callback = function
     * Returns:
     *     Nothing
     *********************************************************************/
    addListener: (function() {
      if (d.addEventListener) {
        return function (element, eventType, callback) {
          element.addEventListener(eventType, function (event) {
            if (typeof callback === 'function') {
              callback(event);
            }
          }, False);
        };
      } else if (d.attachEvent) {
        return function (element, eventType, callback) {
          element.attachEvent('on' + eventType, function (event) {
            if (typeof callback === 'function') {
              callback(event);
            }
          });
        };
      }
    }()),
    /*********************************************************************
     * Function map(arr, fun): cross-browser Array map function
     *     arr = array
     *     fun = function called at each iteration
     * Returns:
     *     mapped array
     *********************************************************************/
    map: function (arr, fun) {
      // Adapted from MDC
      if (!Array.prototype.map) {
        if (arr === void 0 || arr === Null) {
          throw new TypeError();
        }

        var t = Object(arr);
        var len = t.length >>> 0;

        if (typeof fun !== 'function') {
          throw new TypeError();
        }

        var res = new Array(len);
        var thisp = arguments[1];
        for (var i = 0; i < len; i++) {
          if (i in t) {
            res[i] = fun.call(thisp, t[i], i, t);
          }
        }

        return res;
      } else {
        return arr.map(fun);
      }
    },
    /*********************************************************************
     * Function encodeAndBuildRequest(arr, character): Take an array, encode it and join it on a character
     *     array = array to encode and join
     *     character = character join with
     * Returns:
     *     string representing the sanitized values
     * Example:
     *     encodeAndBuildRequest(['a=2', 'b=2'], '&'); // 'a%3D2&b%3D2'
     *********************************************************************/
    encodeAndBuildRequest: function (arr, character) {
      return this.map(arr, function (c) {
        return encodeURIComponent(c);
      }).join(character);
    },
    parseHash: function (url) {
      var hashIndex = url.indexOf("#");
      return hashIndex > 0 ? url.substr(hashIndex) : "";
    },
    hashlessUrl: function (url) {
      var hashIndex = url.indexOf("#");
      return hashIndex > 0 ? url.substr(0, hashIndex) : url;
    },
    addQueryParamAtLocation: function (querystring, param, location) {
      var params = querystring.split("&");
      location = location != Null ? location : params.length;
      params.splice(location, 0, param);

      return params.join("&");
    },
    /*********************************************************************
     * Function isFirstPartyAnalyticsVisitorIDCall(field, trackingServer, trackingServerSecure)
     *     all params are optional except field
     * Returns:
     *     boolean - is Analytics Visitor ID field and server is considered first party
     *********************************************************************/
    isFirstPartyAnalyticsVisitorIDCall: function(field, trackingServer, trackingServerSecure) {
      if (field !== fieldAnalyticsVisitorID) {
        return False;
      }

      var server;

      if (!trackingServer) {
        trackingServer = visitor.trackingServer;
      }

      if (!trackingServerSecure) {
        trackingServerSecure = visitor.trackingServerSecure;
      }

      if (visitor.loadSSL) {
        server = trackingServerSecure;
      } else {
        server = trackingServer;
      }

      if (typeof server === 'string' && server.length) {
        return server.indexOf('2o7.net') < 0 && server.indexOf('omtrdc.net') < 0;
      }

      return False;
    },
    isObject: function (val) {
      return Boolean(val && val === Object(val));
    }
  };

  visitor._helpers = helpers;

  /* Private request processors */
  var requestProcs = {
    // CORS is for cross-domain AJAX in browsers that support it
    // 'withCredentials' needs to be a supported property for cookies to be enabled
    corsMetadata: (function () {
      var corsType = 'none',
        corsCookiesEnabled = True;

      if (typeof XMLHttpRequest !== 'undefined' && XMLHttpRequest === Object(XMLHttpRequest)) {
        if ('withCredentials' in new XMLHttpRequest()) {
          // Standard feature detection
          corsType = 'XMLHttpRequest';
        } else if (new Function('/*@cc_on return /^10/.test(@_jscript_version) @*/')()) {
          // IE10
          corsType = 'XMLHttpRequest';
        } else if (typeof XDomainRequest !== 'undefined' && XDomainRequest === Object(XDomainRequest)) {
          // IE8/9 - XDomainRequest will not be used
          corsCookiesEnabled = False;
        }

        // Safari
        if (Object.prototype.toString.call(window.HTMLElement).indexOf('Constructor') > 0) {
          corsCookiesEnabled = False;
        }
      }

      return {
        corsType: corsType,
        corsCookiesEnabled: corsCookiesEnabled
      };
    }()),
    /*********************************************************************
     * Function getCORSInstance()
     * Returns:
     *     A valid CORS instance or null
     *********************************************************************/
    getCORSInstance: function () {
      return this.corsMetadata.corsType === 'none' ? Null : new window[this.corsMetadata.corsType]();
    },
    /*********************************************************************
     * Function fireCors(corsData): send CORS POST and pass response to callback
     * Returns:
     *     Nothing
     *********************************************************************/
    fireCORS: function (corsData, loadErrorHandler, fieldGroup) {
      var self = this;

      if (loadErrorHandler) {
        corsData.loadErrorHandler = loadErrorHandler;
      }

      function handleCORSResponse(responseText) {
        var json;

        // JSON.parse is supported by all CORS-enabled browsers except iOS Safari 3.2, which has 0% market share (see http://caniuse.com/#search=json.parse and http://caniuse.com/#search=cors)
        try {
          json = JSON.parse(responseText);

          if (json !== Object(json)) {
            self.handleCORSError(corsData, Null, 'Response is not JSON');

            return;
          }

        } catch (error) {
          // Possible source of error: a pixel is returned from a 302 redirect
          self.handleCORSError(corsData, error, 'Error parsing response as JSON');

          return;
        }

        try {
          var callback = corsData.callback,
            callbackFn = window;

          for (var i = 0; i < callback.length; i++) {
            callbackFn = callbackFn[callback[i]];
          }

          callbackFn(json);
        } catch (error) {
          self.handleCORSError(corsData, error, 'Error forming callback function');
        }
      }

      try {
        var corsInstance = this.getCORSInstance();

        corsInstance.open('get', corsData.corsUrl + '&ts=' + new Date().getTime(), True);

        if (this.corsMetadata.corsType === 'XMLHttpRequest') {
          // This line has to come after corsInstance.open() to work in IE10/11 and Safari 5 (see http://stackoverflow.com/questions/19666809/cors-withcredentials-support-limited)
          corsInstance.withCredentials = True;

          // This line has to come after corsInstance.open() to work in IE10/11
          corsInstance.timeout = visitor.loadTimeout;

          // Set content type
          corsInstance.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

          corsInstance.onreadystatechange = function() {
            if (this.readyState === 4) {
              if (this.status === 200) {
                handleCORSResponse(this.responseText);
              }
            }
          };
        }

        corsInstance.onerror = function(error) {
          // One possible error is if the proper CORS response headers are not returned
          self.handleCORSError(corsData, error, 'onerror');
        };

        corsInstance.ontimeout = function(error) {
          self.handleCORSError(corsData, error, 'ontimeout');
        };

        corsInstance.send();

        _timeoutMetrics.fieldGroupObj[fieldGroup] = {
          requestStart: _timeoutMetrics.millis(),
          url: corsData.corsUrl,
          d_visid_stg_timeout_captured: corsInstance.timeout,
          d_settimeout_overriden: _timeoutMetrics.getSetTimeoutOverriden(),
          d_visid_cors: 1
        };

        // Log the request
        visitor._log.requests.push(corsData.corsUrl);
      } catch(error) {
        // One possible error is 'Access Denied' in IE10/11 due to http://devmohd.blogspot.com/2013/09/cross-domain.html
        this.handleCORSError(corsData, error, 'try-catch');
      }
    },
    /*********************************************************************
     * Function handleCORSError(corsData, error, description): put CORS error info into visitor.CORSErrors array
     * Returns:
     *     Nothing
     *********************************************************************/
    handleCORSError: function (corsData, error, description) {
      visitor.CORSErrors.push({
        corsData: corsData,
        error: error,
        description: description
      });

      if (corsData.loadErrorHandler) {
        if (description === 'ontimeout') {
          corsData.loadErrorHandler(True);
        } else {
          // Pass corsData to signify that it's a CORS error (not a CORS timeout)
          corsData.loadErrorHandler(False, corsData);
        }
      }
    }
  };

  visitor._requestProcs = requestProcs;

  /* Destination publishing for id syncs */
  var destinationPublishing = {
    THROTTLE_START: 30000,
    MAX_SYNCS_LENGTH: 649,
    throttleTimerSet: False,
    id: Null,
    onPagePixels: [],
    iframeHost: Null,
    /*********************************************************************
     * Function getIframeHost(url): get hostname of iframe src
     *     url = iframe src
     * Returns:
     *     hostname
     * Example:
     *     getIframeHost('http://fast.demofirst.demdex.net/dest5.html') // http://fast.demofirst.demdex.net
     *********************************************************************/
    getIframeHost: function(url) {
      if (typeof url === 'string') {
        var split = url.split('/');

        return split[0] + '//' + split[2];
      }
    },
    subdomain: Null,
    url: Null,
    /*********************************************************************
     * Function getUrl(): get iframe src
     * Returns:
     *     iframe src
     *********************************************************************/
    getUrl: function() {
      var prefix = 'http://fast.',
        suffix = '?d_nsid=' + visitor.idSyncContainerID + '#' + encodeURIComponent(d.location.href),
        url;

      if (!this.subdomain) {
        this.subdomain = 'nosubdomainreturned';
      }

      if (visitor.loadSSL) {
        if (visitor.idSyncSSLUseAkamai) {
          prefix = 'https://fast.';
        } else {
          prefix = 'https://';
        }
      }

      url = prefix + this.subdomain + '.demdex.net/dest5.html' + suffix;
      this.iframeHost = this.getIframeHost(url);
      this.id = 'destination_publishing_iframe_' + this.subdomain + '_' + visitor.idSyncContainerID;

      return url;
    },
    /*********************************************************************
     * Function checkDPIframeSrc(): get iframe src from visitor._dpIframeSrc if it exists
     * Returns:
     *     iframe src
     *********************************************************************/
    checkDPIframeSrc: function() {
      var suffix = '?d_nsid=' + visitor.idSyncContainerID + '#' + encodeURIComponent(d.location.href);

      if (typeof visitor._dpIframeSrc === 'string' && visitor._dpIframeSrc.length) {
        this.id = 'destination_publishing_iframe_' + new Date().getTime() + '_' + visitor.idSyncContainerID;
        this.iframeHost = this.getIframeHost(visitor._dpIframeSrc);
        this.url = visitor._dpIframeSrc + suffix;
      }
    },
    idCallNotProcesssed: Null,
    doAttachIframe: False,
    startedAttachingIframe: False,
    iframeHasLoaded: Null,
    iframeIdChanged: Null,
    newIframeCreated: Null,
    originalIframeHasLoadedAlready: Null,
    sendingMessages: False,
    messages: [],
    messagesPosted: [],
    messagesReceived: [],
    messageSendingInterval: constants.POST_MESSAGE_ENABLED ? 15 : 100, // 100 ms for IE6/7, 15 ms for all other major modern browsers
    jsonWaiting: [],
    jsonProcessed: [],
    canSetThirdPartyCookies: True,
    receivedThirdPartyCookiesNotification: False,
    /*********************************************************************
     * Function readyToAttachIframe(): check if iframe is ready to be attached
     * Returns:
     *     Nothing
     *********************************************************************/
    readyToAttachIframe: function() {
      return !visitor.idSyncDisable3rdPartySyncing && (this.doAttachIframe || visitor._doAttachIframe) && this.subdomain && this.subdomain !== 'nosubdomainreturned' && this.url && !this.startedAttachingIframe;
    },
    /*********************************************************************
     * Function attachIframe(): attach iframe
     * Returns:
     *     Nothing
     *********************************************************************/
    attachIframe: function () {
      this.startedAttachingIframe = True;

      var self = this,
        iframe = document.getElementById(this.id);

      if (!iframe) {
        createNewIframe();
      } else if (iframe.nodeName !== 'IFRAME') {
        this.id += '_2';
        this.iframeIdChanged = True;
        createNewIframe();
      } else {
        this.newIframeCreated = False;

        // This class name is set by Visitor API
        if (iframe.className !== 'aamIframeLoaded') {
          this.originalIframeHasLoadedAlready = False;
          addLoadListener();
        } else {
          this.originalIframeHasLoadedAlready = True;
          this.iframeHasLoaded = True;
          this.iframe = iframe;
          this.requestToProcess();
        }
      }

      function createNewIframe() {
        iframe = document.createElement('iframe');
        iframe['sandbox'] = 'allow-scripts allow-same-origin';
        iframe.title = 'Adobe ID Syncing iFrame';
        iframe.id = self.id;
        iframe.style.cssText = 'display: none; width: 0; height: 0;';
        iframe.src = self.url;
        self.newIframeCreated = True;
        addLoadListener();
        document.body.appendChild(iframe);
      }

      function addLoadListener() {
        helpers.addListener(iframe, 'load', function () {
          iframe.className = 'aamIframeLoaded';
          self.iframeHasLoaded = True;
          self.requestToProcess();
        });
      }

      this.iframe = iframe;
    },
    /*********************************************************************
     * Function requestToProcess(json): queues json, then processes the queue when conditions are met
     *     json = id sync json
     * Returns:
     *     Nothing
     *********************************************************************/
    requestToProcess: function(json) {
      var self = this;

      if (json === Object(json)) {
        this.jsonWaiting.push(json);
        this.processSyncOnPage(json);
      }

      // IE6/7 will not receive ThirdPartyCookiesNotification
      if ((this.receivedThirdPartyCookiesNotification || !constants.POST_MESSAGE_ENABLED || this.iframeHasLoaded) && this.jsonWaiting.length) {
        this.process(this.jsonWaiting.shift());
        this.requestToProcess();
      }

      if (!visitor.idSyncDisableSyncs && this.iframeHasLoaded && this.messages.length && !this.sendingMessages) {
        if (!this.throttleTimerSet) {
          this.throttleTimerSet = True;

          setTimeout(function() {
            self.messageSendingInterval = constants.POST_MESSAGE_ENABLED ? 15 : 150; // 150 ms for IE6/7, 15 ms for all other major modern browsers
          }, this.THROTTLE_START);
        }

        this.sendingMessages = True;
        this.sendMessages();
      }
    },
    /*********************************************************************
     * Function processSyncOnPage(json): processes json for sending id syncs on page
     *     json = id sync json
     * Returns:
     *     Nothing
     *********************************************************************/
    processSyncOnPage: function (json) {
      var key, l, i, k;

      if ((key = json['ibs']) && key instanceof Array && (l = key.length)) {
        for (i = 0; i < l; i++) {
          k = key[i];

          if (k['syncOnPage']) {
            this.checkFirstPartyCookie(k, '', 'syncOnPage');
          }
        }
      }
    },
    /*********************************************************************
     * Function process(json): processes json for sending to iframe
     *     json = id sync json
     * Returns:
     *     Nothing
     *********************************************************************/
    process: function (json) {
      var f = encodeURIComponent,
        declaredIdString = '',
        key, l, i, k, a, cb, callback;

      if ((key = json['ibs']) && key instanceof Array && (l = key.length)) {
        for (i = 0; i < l; i++) {
          k = key[i];

          a = [f('ibs'), f(k['id'] || ''), f(k['tag'] || ''), helpers.encodeAndBuildRequest(k['url'] || [], ','), f(k['ttl'] || ''), '', declaredIdString, k['fireURLSync'] ? 'true' : 'false'];

          if (k['syncOnPage']) {
            // This is handled in processSyncOnPage()
            continue;
          } else if (this.canSetThirdPartyCookies) {
            this.addMessage(a.join('|'));
          } else if (k['fireURLSync']) {
            this.checkFirstPartyCookie(k, a.join('|'));
          }
        }
      }

      this.jsonProcessed.push(json);
    },
    /*********************************************************************
     * Function checkFirstPartyCookie(config, message): checks if id sync should be fired, and sets control cookie
     *     config = id sync config
     *     message = id sync message
     * Returns:
     *     Nothing
     *********************************************************************/
    checkFirstPartyCookie: function(config, message, idSyncType) {
      var onPage = idSyncType === 'syncOnPage' ? True : False,
        field = onPage ? fieldMarketingCloudSyncsOnPage : fieldMarketingCloudSyncs;

      visitor._readVisitor();

      var syncs = visitor._getField(field),
        dataPresent = False,
        dataValid = False,
        now = Math.ceil(new Date().getTime() / constants.MILLIS_PER_DAY),
        data, pruneResult;

      if (syncs) {
        data = syncs.split('*');
        pruneResult = this.pruneSyncData(data, config['id'], now);
        dataPresent = pruneResult.dataPresent;
        dataValid = pruneResult.dataValid;

        if (!dataPresent || !dataValid) {
          this.fireSync(onPage, config, message, data, field, now);
        }
      } else {
        data = [];
        this.fireSync(onPage, config, message, data, field, now);
      }
    },
    /*********************************************************************
     * Function pruneSyncData(data, id, now): removes expired id syncs and returns status of current id sync tracker
     *     data = array of id sync trackers
     *     id = data provider id
     *     now = current date in days since epoch
     * Returns:
     *     {
         *         dataPresent: <boolean>,
         *         dataValid: <boolean>
         *     }
     *********************************************************************/
    pruneSyncData: function(data, id, now) {
      var dataPresent = False,
        dataValid = False,
        tinfo, i, tstamp;

      for (i = 0; i < data.length; i++) {
        tinfo = data[i];
        tstamp = parseInt(tinfo.split('-')[1], 10);

        if (tinfo.match('^' + id + '-')) {
          dataPresent = True;

          if (now < tstamp) {
            dataValid = True;
          } else {
            data.splice(i, 1);
            i--;
          }
        } else {
          if (now >= tstamp) {
            data.splice(i, 1);
            i--;
          }
        }
      }

      return {
        dataPresent: dataPresent,
        dataValid: dataValid
      }
    },
    /*********************************************************************
     * Function manageSyncsSize(data): replaces id sync trackers that are soonest to expire until size is within limit
     *     data = array of id sync trackers
     * Returns:
     *     Nothing
     *********************************************************************/
    manageSyncsSize: function(data) {
      if (data.join('*').length > this.MAX_SYNCS_LENGTH) {
        data.sort(function(a, b) {
          return parseInt(a.split('-')[1], 10) - parseInt(b.split('-')[1], 10);
        });

        while (data.join('*').length > this.MAX_SYNCS_LENGTH) {
          data.shift();
        }
      }
    },
    /*********************************************************************
     * Function fireSync(onPage, config, message, data, field, now): sends id sync on page or in iframe
     *     onPage - fire on page
     *     config - id sync data
     *     message - id sync for iframe
     *     data - id sync cookie data
     *     field - cookie field
     *     now - snapshot of date in days since epoch
     * Returns:
     *     Nothing
     *********************************************************************/
    fireSync: function(onPage, config, message, data, field, now) {
      var self = this;

      if (onPage) {
        if (config['tag'] === 'img') {
          var urls = config['url'],
            protocol = visitor.loadSSL ? 'https:' : 'http:',
            i, l, url, protocolIsPrependable;

          for (i = 0, l = urls.length; i < l; i++) {
            url = urls[i];

            protocolIsPrependable = /^\/\//.test(url);

            var img = new Image();

            helpers.addListener(img, 'load', function(index, syncConfig, syncField, syncNow) {
              return function() {
                self.onPagePixels[index] = Null;
                visitor._readVisitor();

                var syncs = visitor._getField(field),
                  syncsData = [],
                  tempData;

                if (syncs) {
                  tempData = syncs.split('*');

                  var i, l, tinfo;

                  for (i = 0, l = tempData.length; i < l; i++) {
                    tinfo = tempData[i];

                    if (!tinfo.match('^' + syncConfig['id'] + '-')) {
                      syncsData.push(tinfo);
                    }
                  }

                }

                self.setSyncTrackingData(syncsData, syncConfig, syncField, syncNow);
              };
            }(this.onPagePixels.length, config, field, now));

            img.src = (protocolIsPrependable ? protocol : '') + url;

            this.onPagePixels.push(img);
          }
        }
      } else {
        this.addMessage(message);
        this.setSyncTrackingData(data, config, field, now);
      }
    },
    /*********************************************************************
     * Function addMessage(m): adds prefix to message, then queues for sending
     *     m = id sync message
     * Returns:
     *     Nothing
     *********************************************************************/
    addMessage: function(m) {
      var f = encodeURIComponent,
        identifier = visitor._enableErrorReporting ? f('---destpub-debug---') : f('---destpub---');

      this.messages.push(identifier + m);
    },
    /*********************************************************************
     * Function setSyncTrackingData(data, config, field, now) - write id sync conntrol data to cookie field
     *     data - id sync cookie data
     *     config - id sync data
     *     field - cookie field
     *     now - snapshot of date in days since epoch
     * Returns:
     *     Nothing
     *********************************************************************/
    setSyncTrackingData: function(data, config, field, now) {
      data.push(config['id'] + '-' + (now + Math.ceil(config['ttl'] / 60 / 24)));
      this.manageSyncsSize(data);
      visitor._setField(field, data.join('*'));
    },
    /*********************************************************************
     * Function sendMessages(): sends messages to iframe
     * Returns:
     *     Nothing
     *********************************************************************/
    sendMessages: function () {
      var self = this,
        message;

      if (this.messages.length) {
        message = this.messages.shift();
        visitor._xd.postMessage(message, this.url, this.iframe.contentWindow);
        this.messagesPosted.push(message);

        setTimeout(function () {
          self.sendMessages();
        }, this.messageSendingInterval);
      } else {
        this.sendingMessages = False;
      }
    },
    /*********************************************************************
     * Function receiveMessage(message): receives messages from iframe
     *     message = message from iframe
     * Returns:
     *     Nothing
     *********************************************************************/
    receiveMessage: function(message) {
      var prefix = /^---destpub-to-parent---/,
        split;

      if (typeof message === 'string' && prefix.test(message)) {
        split = message.replace(prefix, '').split('|');

        if (split[0] === 'canSetThirdPartyCookies') {
          this.canSetThirdPartyCookies = (split[1] === 'true') ? True : False;
          this.receivedThirdPartyCookiesNotification = True;
          this.requestToProcess();
        }

        this.messagesReceived.push(message);
      }
    },
    /*********************************************************************
     * Function processIDCallData(json): processes id sync data from /id call response
     *     json = id sync data from /id call response
     * Returns:
     *     Nothing
     *********************************************************************/
    processIDCallData: function(json) {
      if (this.url === Null || (json['subdomain'] && this.subdomain === 'nosubdomainreturned')) {
        if (typeof visitor._subdomain === 'string' && visitor._subdomain.length) {
          this.subdomain = visitor._subdomain;
        } else {
          this.subdomain = json['subdomain'] || '';
        }

        this.url = this.getUrl();
      }

      if (json['ibs'] instanceof Array && json['ibs'].length) {
        this.doAttachIframe = True;
      }

      if (this.readyToAttachIframe()) {
        if (!visitor.idSyncAttachIframeOnWindowLoad) {
          this.attachIframeASAP();
        } else if (thisClass.windowLoaded || d.readyState === 'complete' || d.readyState === 'loaded') {
          this.attachIframe();
        }
      }

      if (typeof visitor.idSyncIDCallResult === 'function') {
        visitor.idSyncIDCallResult(json);
      } else {
        this.requestToProcess(json);
      }

      if (typeof visitor.idSyncAfterIDCallResult === 'function') {
        visitor.idSyncAfterIDCallResult(json);
      }
    },
    /*********************************************************************
     * Function canMakeSyncIDCall(idTS, nowTS): checks if /id call specific to id syncs should be made
     *     idTS = timestamp of wait expiration
     *     nowTS = current date in days since epoch
     * Returns:
     *     boolean
     *********************************************************************/
    canMakeSyncIDCall: function(idTS, nowTS) {
      return visitor._forceSyncIDCall || !idTS || nowTS - idTS > constants.DAYS_BETWEEN_SYNC_ID_CALLS;
    },
    /*********************************************************************
     * Function attachIframeASAP(): attach iframe as soon as document.body is reached
     * Returns:
     *     Nothing
     *********************************************************************/
    attachIframeASAP: function() {
      var self = this;

      function tryToAttachIframe() {
        if (!self.startedAttachingIframe) {
          if (document.body) {
            self.attachIframe();
          } else {
            setTimeout(tryToAttachIframe, 30);
          }
        }
      }

      tryToAttachIframe();
    }
  };

  visitor._destinationPublishing = destinationPublishing;

  visitor.timeoutMetricsLog = [];

  var _timeoutMetrics = {
    d_timingapi: window.performance && window.performance.timing ? 1 : 0,
    performanceTiming: window.performance && window.performance.timing ? window.performance.timing : Null,
    windowLoad: Null,
    d_winload: Null,
    fieldGroupObj: {},
    metricsQueue: [],
    send: function(metrics) {
      if (!visitor.takeTimeoutMetrics) {
        return;
      }

      if (metrics === Object(metrics)) {
        var qsArray = [],
          f = encodeURIComponent,
          key, url;

        for (key in metrics) {
          if (metrics.hasOwnProperty(key)) {
            qsArray.push(f(key) + '=' + f(metrics[key]));
          }
        }

        url = 'http' + (visitor.loadSSL ? 's' : '') + '://'
          + 'dpm.demdex.net'
          + '/event?'
          + 'd_visid_ver=' + visitor.version
          + '&d_visid_stg_timeout=' + visitor.loadTimeout
          + '&'
          + qsArray.join('&')
          + '&d_orgid=' + f(visitor.marketingCloudOrgID)
          + '&d_timingapi=' + this.d_timingapi
          + '&d_winload=' + this.getWinLoad()
          + '&d_ld=' + this.millis();

        new Image().src = url;
        visitor.timeoutMetricsLog.push(url);
      }
    },
    getWinLoad: function() {
      if (this.d_winload === Null) {
        if (this.performanceTiming) {
          this.d_winload =  this.windowLoad - this.performanceTiming.navigationStart;
        } else {
          this.d_winload = this.windowLoad - thisClass.codeLoadEnd;
        }
      }

      return this.d_winload;
    },
    millis: function() {
      return new Date().getTime();
    },
    process: function(fieldGroup) {
      var fgo = this.fieldGroupObj[fieldGroup],
        metrics = {};

      metrics['d_visid_stg_timeout_captured'] = fgo.d_visid_stg_timeout_captured;
      metrics['d_visid_cors'] = fgo.d_visid_cors;
      metrics['d_fieldgroup'] = fieldGroup;
      metrics['d_settimeout_overriden'] = fgo.d_settimeout_overriden;

      if (fgo.timeout) {
        if (fgo.isActualTimeout) {
          metrics['d_visid_timedout'] = 1;
          metrics['d_visid_timeout'] = fgo.timeout - fgo.requestStart;
          metrics['d_visid_response'] = -1;
        } else {
          metrics['d_visid_timedout'] = 'n/a';
          metrics['d_visid_timeout'] = 'n/a';
          metrics['d_visid_response'] = 'n/a';
        }
      } else {
        metrics['d_visid_timedout'] = 0;
        metrics['d_visid_timeout'] = -1;
        metrics['d_visid_response'] = fgo.requestEnd - fgo.requestStart;
      }

      metrics['d_visid_url'] = fgo.url;

      if (!thisClass.windowLoaded) {
        this.metricsQueue.push(metrics);
      } else {
        this.send(metrics);
      }

      delete this.fieldGroupObj[fieldGroup];
    },
    releaseMetricsQueue: function() {
      for (var i = 0, l = this.metricsQueue.length; i < l; i++) {
        this.send(this.metricsQueue[i]);
      }
    },
    getSetTimeoutOverriden: function() {
      if (typeof setTimeout.toString === 'function') {
        if (setTimeout.toString().indexOf('[native code]') > -1) {
          return 0;
        } else {
          return 1;
        }
      }

      return -1;
    }
  };

  visitor._timeoutMetrics = _timeoutMetrics;

  var _callStateTracker = {
    isClientSideMarketingCloudVisitorID: Null,
    MCIDCallTimedOut: Null,
    AnalyticsIDCallTimedOut: Null,
    AAMIDCallTimedOut: Null,
    fieldGroupObj: {},
    setState: function(fieldGroup, setToValue) {
      switch (fieldGroup) {
        case fieldGroupMarketingCloud:
          if (setToValue === False) {
            if (this.MCIDCallTimedOut !== True) {
              this.MCIDCallTimedOut = False;
            }
          } else {
            this.MCIDCallTimedOut = setToValue;
          }
          break;
        case fieldGroupAnalytics:
          if (setToValue === False) {
            if (this.AnalyticsIDCallTimedOut !== True) {
              this.AnalyticsIDCallTimedOut = False;
            }
          } else {
            this.AnalyticsIDCallTimedOut = setToValue;
          }
          break;
        case fieldGroupAudienceManager:
          if (setToValue === False) {
            if (this.AAMIDCallTimedOut !== True) {
              this.AAMIDCallTimedOut = False;
            }
          } else {
            this.AAMIDCallTimedOut = setToValue;
          }
          break;
      }
    }
  };

  /*********************************************************************
   * Function isClientSideMarketingCloudVisitorID()
   * Returns:
   *     boolean or null if the MC /id call hasn't been made yet
   *********************************************************************/
  visitor.isClientSideMarketingCloudVisitorID = function() {
    return _callStateTracker.isClientSideMarketingCloudVisitorID;
  };

  /*********************************************************************
   * Function MCIDCallTimedOut()
   * Returns:
   *     boolean or null if the call hasn't been made yet
   *********************************************************************/
  visitor.MCIDCallTimedOut = function() {
    return _callStateTracker.MCIDCallTimedOut;
  };

  /*********************************************************************
   * Function AnalyticsIDCallTimedOut()
   * Returns:
   *     boolean or null if the call hasn't been made yet
   *********************************************************************/
  visitor.AnalyticsIDCallTimedOut = function() {
    return _callStateTracker.AnalyticsIDCallTimedOut;
  };

  /*********************************************************************
   * Function AAMIDCallTimedOut()
   * Returns:
   *     boolean or null if the call hasn't been made yet
   *********************************************************************/
  visitor.AAMIDCallTimedOut = function() {
    return _callStateTracker.AAMIDCallTimedOut;
  };

  /*********************************************************************
   * Function idSyncGetOnPageSyncInfo() - get controller info for on page id syncs
   * Returns:
   *     string
   *********************************************************************/
  visitor.idSyncGetOnPageSyncInfo = function() {
    visitor._readVisitor();

    return visitor._getField(fieldMarketingCloudSyncsOnPage);
  };

  /*********************************************************************
   * Function idSyncByURL(config) - manually fire an id sync with a custom url
   *
   * config.dpid - data provider id
   * config.url - custom url
   * config.minutesToLive - time until id sync is fired again
   *
   * Returns:
   *     string
   *********************************************************************/
  visitor.idSyncByURL = function(config) {
    var validation = validateIdSyncByURL(config || {});

    if (validation.error) {
      return validation.error;
    }

    var url = config['url'],
      f = encodeURIComponent,
      dp = destinationPublishing,
      declaredIdString, a;

    url = url.replace(/^https:/, '').replace(/^http:/, '');

    // First array element used to be declaredId.uuid
    declaredIdString = helpers.encodeAndBuildRequest(['', config['dpid'], config['dpuuid'] || ''], ',');
    a = ['ibs', f(config['dpid']), 'img', f(url), validation['ttl'], '', declaredIdString];
    dp.addMessage(a.join('|'));
    dp.requestToProcess();

    return 'Successfully queued';
  };

  function validateIdSyncByURL(config) {
    var DEFAULT_TTL = 20160, // 20160 minutes = 14 days
      url = config['url'],
      ttl = config['minutesToLive'],
      error = '';

    if (visitor.idSyncDisableSyncs) {
      error = error ? error : 'Error: id syncs have been disabled';
    }

    if (typeof config['dpid'] !== 'string' || !config['dpid'].length) {
      error = error ? error : 'Error: config.dpid is empty';
    }

    if (typeof config['url'] !== 'string' || !config['url'].length) {
      error = error ? error : 'Error: config.url is empty';
    }

    if (typeof ttl === 'undefined') {
      ttl = DEFAULT_TTL;
    } else {
      ttl = parseInt(ttl, 10);

      if (isNaN(ttl) || ttl <= 0) {
        error = error ? error : 'Error: config.minutesToLive needs to be a positive number';
      }
    }

    return {
      error: error,
      ttl: ttl
    };
  }

  /*********************************************************************
   * Function idSyncByDataSource(config) - manually fire an id sync with standard AAM url
   *
   * config.dpid - data provider id
   * config.dpuuid - data provider id for user
   * config.minutesToLive - time until id sync is fired again
   *
   * Returns:
   *     string
   *********************************************************************/
  visitor.idSyncByDataSource = function(config) {
    if (config !== Object(config) || typeof config['dpuuid'] !== 'string' || !config['dpuuid'].length) {
      return 'Error: config or config.dpuuid is empty';
    }

    config['url'] = '//dpm.demdex.net/ibs:dpid=' + config['dpid'] + '&dpuuid=' + config['dpuuid'];

    return visitor.idSyncByURL(config);
  };

  /* Init */
  if (marketingCloudOrgID.indexOf("@") < 0) {
    marketingCloudOrgID += "@AdobeOrg";
  }
  visitor.marketingCloudOrgID = marketingCloudOrgID;

  // Setup the config to use for cookies
  visitor.cookieName = "AMCV_" + marketingCloudOrgID;
  visitor.sessionCookieName = "AMCVS_" + marketingCloudOrgID;
  visitor.cookieDomain = visitor._getDomain();
  if (visitor.cookieDomain == w.location.hostname) {
    visitor.cookieDomain = "";
  }

  // Setup config for loading external data
  visitor.loadSSL = (w.location.protocol.toLowerCase().indexOf("https") >= 0);
  visitor.loadTimeout = 30000;
  visitor.CORSErrors = [];

  // Setup defaults
  visitor.marketingCloudServer = visitor.audienceManagerServer = "dpm.demdex.net";

  var fieldsNonBlockingExpiration = {};
  fieldsNonBlockingExpiration[fieldAudienceManagerLocationHint] = True;
  fieldsNonBlockingExpiration[fieldAudienceManagerBlob] = True;

  // Handle initConfig
  if ((initConfig) && (typeof(initConfig) == "object")) {
    // Apply initConfig
    var initVar;
    for (initVar in initConfig) {
      if (_isNOP(initVar)) {
        visitor[initVar] = initConfig[initVar];
      }
    }

    visitor.idSyncContainerID = visitor.idSyncContainerID || 0;

    // Internal initConfig options
    // _dpIframeSrc, _subdomain, _enableErrorReporting, _forceSyncIDCall, _doAttachIframe

    visitor._attemptToPopulateIdsFromUrl();

    visitor._readVisitor();

    var idTS = visitor._getField(fieldMarketingCloudIDCallTimeStamp),
      nowTS = Math.ceil(new Date().getTime() / constants.MILLIS_PER_DAY);

    if (!visitor.idSyncDisableSyncs && destinationPublishing.canMakeSyncIDCall(idTS, nowTS)) {
      visitor._setFieldExpire(fieldAudienceManagerBlob,-1);
      visitor._setField(fieldMarketingCloudIDCallTimeStamp, nowTS);
    }

    visitor.getMarketingCloudVisitorID();
    visitor.getAudienceManagerLocationHint();
    visitor.getAudienceManagerBlob();

    visitor._mergeServerState(visitor.serverState);
  } else {
    visitor._attemptToPopulateIdsFromUrl();
  }

  if (!visitor.idSyncDisableSyncs) {
    destinationPublishing.checkDPIframeSrc();

    var attachIframeIfReady = function() {
      var dp = destinationPublishing;

      if (dp.readyToAttachIframe()) {
        dp.attachIframe();
      }
    };

    helpers.addListener(window, 'load', function() {
      thisClass.windowLoaded = True;
      _timeoutMetrics.windowLoad = _timeoutMetrics.millis();
      _timeoutMetrics.releaseMetricsQueue();
      attachIframeIfReady()
    });

    try {
      visitor._xd.receiveMessage(function(message) {
        destinationPublishing.receiveMessage(message.data);
      }, destinationPublishing.iframeHost);
    } catch (__Error__) {}
  }
}

/*********************************************************************
 * Function getInstance(marketingCloudOrgID,initConfig): Finds instance for a marketingCloudOrgID
 *     marketingCloudOrgID = Marketing Cloud Organization ID to use
 *     initConfig          = Optional initial config object allowing the constructor to fire
 *                           off requests immediately instead of lazily
 * Returns:
 *     Instance
 *********************************************************************/
Visitor["getInstance"] = function(marketingCloudOrgID,initConfig) {
  /**
   * @type {Visitor}
   * @noalias
   */
  var visitor;
  var
    instanceList = window.s_c_il,
    instanceNum;

  if (marketingCloudOrgID.indexOf("@") < 0) {
    marketingCloudOrgID += "@AdobeOrg";
  }
  if (instanceList) {
    for (instanceNum = 0;instanceNum < instanceList.length;instanceNum++) {
      visitor = instanceList[instanceNum];
      if ((visitor) && (visitor._c == "Visitor") &&
        (visitor.marketingCloudOrgID == marketingCloudOrgID)) {
        return visitor;
      }
    }
  }
  return new Visitor(marketingCloudOrgID,initConfig);
};

// Set Visitor.windowLoaded to true on window load
(function() {
  // This is a hack to keep Google Closure Compiler from creating a global variable for true
  var thisClass = window['Visitor'],
    True = thisClass.True,
    False = thisClass.False;

  if (!True) {
    True = true;
  }

  if (!False) {
    False = false;
  }

  function loadCallback() {
    thisClass.windowLoaded = True;
  }

  if (window.addEventListener) {
    window.addEventListener('load', loadCallback);
  } else if (window.attachEvent) {
    window.attachEvent('onload', loadCallback);
  }

  thisClass.codeLoadEnd = new Date().getTime();
}());
