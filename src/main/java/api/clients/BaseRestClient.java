package api.clients;

import api.config.ConfigUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.FilterableRequestSpecification;

public class BaseRestClient {

    String serviceEndPoint;

    public String getServiceEndPoint() {
        return serviceEndPoint;
    }

    public void setServiceEndPoint(String serviceEndPoint) {
        this.serviceEndPoint = serviceEndPoint;
    }

    protected FilterableRequestSpecification getRequestSpecification(ContentType contentType) {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setContentType(contentType);
        getFilters(requestSpecBuilder);
        return (FilterableRequestSpecification) RestAssured.given(requestSpecBuilder.build());
    }

    protected FilterableRequestSpecification getRequestSpecification() {
        return (FilterableRequestSpecification) getRequestSpecification(ContentType.JSON);
    }


    private void getFilters(RequestSpecBuilder requestSpecBuilder) {
        if (ConfigUtils.getConfig("frameworklog") == null
                || ConfigUtils.getConfig("frameworklog").equals("true")) {
            requestSpecBuilder.addFilter(new ErrorLoggingFilter());
            requestSpecBuilder.addFilter(new RequestLoggingFilter());
            requestSpecBuilder.addFilter(new ResponseLoggingFilter());
        }
    }

    protected String getBody(Object object) {
        String jsonString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();

            jsonString = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    protected FilterableRequestSpecification getRequestSpecificationWoCurl(ContentType contentType) {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setContentType(contentType);
        getFilters(requestSpecBuilder);
        return (FilterableRequestSpecification) RestAssured.given(requestSpecBuilder.build());
    }

    protected FilterableRequestSpecification getRequestSpecificationWoConfig() {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        return (FilterableRequestSpecification) RestAssured.given(requestSpecBuilder.build());
    }
}
