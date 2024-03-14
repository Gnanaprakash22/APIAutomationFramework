package api.endpoints;

import api.payload.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
//Create , Read , Update , Delete request the user API

public class UserEndPoints {

     public static Response createUser(User payload){
        Response response=given().contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payload)
                .when()
                .post(Routes.postUrl);

       return response;
    }

    public static Response readUser(int  id){
        Response response=given()
                .pathParam("id",id)
                .when()
                .get(Routes.getUrl);

        return response;
    }

    public static Response updateUser(int id, User payload){
        Response response=given().contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payload)
                .pathParam("id",id)
                .when()
                .put(Routes.updateUrl);

        return response;
    }

    public static Response deleteUser(int id){
        Response response=given()
                .pathParam("id",id)
                .when()
                .delete(Routes.deleteUrl);

        return response;
    }

}
