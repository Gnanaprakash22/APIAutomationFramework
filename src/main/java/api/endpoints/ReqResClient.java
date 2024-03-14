package api.endpoints;

import api.clients.ApiDetails;
import api.clients.BaseRestClient;
import api.payload.User;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;

public class ReqResClient extends BaseRestClient {

    private String baseUrl="https://reqres.in";

    //User module

    private String postUrl=baseUrl+"/api/users";
    private String getUrl=baseUrl+"/api/users/{id}";
    private String updateUrl=baseUrl+"/api/users/{id}";
    private String deleteUrl=baseUrl+"/api/users/{id}";

    public ApiDetails createUser(User user){
        FilterableRequestSpecification request=getRequestSpecification();
        request.body(user);
        Response response=request.post(postUrl);
        return new ApiDetails(request,response);
    }
}
