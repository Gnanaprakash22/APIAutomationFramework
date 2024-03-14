package api.test;

import api.clients.ApiDetails;
import api.endpoints.ReqResClient;
import api.payload.JobData;
import api.payload.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UserTests {

    Faker faker;
    User userPayload;

    @BeforeClass
    public void setupData(){
        faker=new Faker();
        userPayload=new User();

        userPayload.setId("13631954");
        userPayload.setJob("Lead Engineer");


//        userPayload.setId(faker.idNumber().hashCode());
//        userPayload.setUserName(faker.name().username());
//        userPayload.setEmail(faker.internet().safeEmailAddress());
//        userPayload.setFirstName(faker.name().firstName());
//        userPayload.setLastName((faker.name().lastName()));
//        userPayload.setPassword(faker.internet().password(5,10));
//        userPayload.setPhone(faker.phoneNumber().cellPhone());
    }

    @Test
    public void testPostUser() throws JsonProcessingException {
//        Response response=UserEndPoints.createUser(userPayload);
//        response.then().log().all();
        ReqResClient reqResClient=new ReqResClient();
        ApiDetails details = reqResClient.createUser(userPayload);
        Assert.assertEquals(details.getResponse().getStatusCode(), HttpStatus.SC_CREATED);
//        Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_CREATED);
        ObjectMapper objectMapper=new ObjectMapper();
//        JobData jobData = objectMapper.readValue(response.getBody().asString(), JobData.class);
        JobData jobData = objectMapper.readValue(details.getResponse().getBody().asString(), JobData.class);
        Assert.assertEquals(jobData.getJob(),"Lead Engineer");
        Assert.assertEquals(jobData.getId(),"13631954");
//        Assert.assertTrue(jobData.getCreatedAt().toString().matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z"));
    }

    @Test
    public void testGetUser(){

    }
}
