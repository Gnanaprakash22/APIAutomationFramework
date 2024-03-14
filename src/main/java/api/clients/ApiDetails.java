/* (C) Games24x7 */
package api.clients;


import api.config.ConfigUtils;
import api.logger.ILogger;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;

import java.util.HashMap;
import java.util.Map;
public class ApiDetails implements ILogger {

  private Response response;
  private FilterableRequestSpecification request;
  private Object requestObject;
  private String curl;
  private Map<String, Object> previousDetails;

  public ApiDetails(
      FilterableRequestSpecification request,
      Response response,
      Object requestObject,
      String curl) {
    this.response = response;
    this.request = request;
    this.requestObject = requestObject;
    this.curl = curl;
    previousDetails = new HashMap<>();
    extentLogging(request, response);
  }

  public ApiDetails(FilterableRequestSpecification request, Response response) {
    this.response = response;
    this.request = request;
    previousDetails = new HashMap<>();
    extentLogging(request, response);
  }

  public ApiDetails(FilterableRequestSpecification request, Response response, boolean extent) {
    this.response = response;
    this.request = request;
    previousDetails = new HashMap<>();
    if (extent) extentLogging(request, response);
  }

  public Response getResponse() {
    return response;
  }

  public String getCurl() {
    return curl;
  }

  public FilterableRequestSpecification getRequest() {
    return request;
  }

  public Object getRequestObject() {
    return requestObject;
  }

  public final void extentLogging(FilterableRequestSpecification request, Response response) {
    LogExtent.info("Request URI => " + request.getURI());
    if (request.getBody() != null) {
      LogExtent.jsonInfo("Request Body => ", request.getBody().toString());
    }
    LogExtent.info("Response Code => " + response.getStatusCode());
    if (response.getBody() != null) {
      if (ConfigUtils.getConfig("frameworklog") == null
          || ConfigUtils.getConfig("frameworklog").equals("true")) {
        if (ConfigUtils.getConfig("jsonlog") == null
            || ConfigUtils.getConfig("jsonlog").equals("true")) {
          LogExtent.jsonInfo("Response Body => ", response.getBody().asString());
        } else {
          LogExtent.info("Response Body => " + response.getBody().prettyPrint());
        }
      } else {
        LogExtent.info("Response Body => " + response.getBody().asString());
      }
    }
  }

  public void addPreviousDetails(String key, Object value) {
    previousDetails.put(key, value);
  }

  public Object getPreviousDetails(String key) {
    return previousDetails.get(key);
  }

  public Map getPreviousDetailsMap() {
    return previousDetails;
  }
}
