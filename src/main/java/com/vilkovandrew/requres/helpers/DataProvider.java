package com.vilkovandrew.requres.helpers;

import java.util.HashMap;
import java.util.Map;

public class DataProvider {

    @org.testng.annotations.DataProvider(name = "uniqueUserAvatarFilenames")
    public Object[][] uniqueUserAvatarFilenamesTest() {
        return new Object[][]{
                {"https://reqres.in/", 200, 2}
        };
    }

    @org.testng.annotations.DataProvider(name = "unsuccessfulLoginTest")
    public Object[][] secondTaskLoginUnSuccessfulTest() {
        Map<String, String> requestData = new HashMap<>();
        requestData.put("email", "eve.holt@reqres.in");

        return new Object[][]{
                {"https://reqres.in/", 400, requestData, "Missing password"}
        };
    }

    @org.testng.annotations.DataProvider(name = "successfulLoginTest")
    public Object[][] secondTaskLoginSuccessfulTest() {
        Map<String, String> requestData = new HashMap<>();
        requestData.put("email", "eve.holt@reqres.in");
        requestData.put("password", "cityslicka");

        return new Object[][]{
                {"https://reqres.in/", 200, requestData, "QpwL5tke4Pnpja7X4"}
        };
    }

    @org.testng.annotations.DataProvider(name = "dataSortedFromYear")
    public Object[][] dataSortedFromYear() {
        return new Object[][]{
                {"https://reqres.in/", 200}
        };
    }

    @org.testng.annotations.DataProvider(name = "tagCounter")
    public Object[][] tagCounter() {
        return new Object[][]{
                {"https://gateway.autodns.com/", 200, 14}
        };
    }
}
