package com.vilkovandrew.requres;

import com.vilkovandrew.requres.data.DataResource;
import com.vilkovandrew.requres.data.DataUser;
import com.vilkovandrew.requres.data.LoginResponse;
import com.vilkovandrew.requres.helpers.DataProvider;
import io.restassured.http.ContentType;
import io.restassured.path.xml.element.Node;
import org.apache.commons.lang3.ArrayUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;

import static com.vilkovandrew.requres.specification.Specifitation.*;
import static io.restassured.RestAssured.given;

public class APITests {

    @Test(dataProvider = "uniqueUserAvatarFilenames", dataProviderClass = DataProvider.class)
    public void uniqueUserAvatarFilenames(String baseUri, int statusCode, int pageNumber) {
        installSpec(requestSpec(baseUri), responseSpec(statusCode));

        List<DataUser> data = given()
                .when()
                .get("/api/users?page={pageNumber}", pageNumber)
                .getBody()
                .jsonPath().getList("data", DataUser.class);

        Assert.assertFalse(data.isEmpty(), "Ожидался список пользователей, получили пустой список");

        List<String> avatarFileNames = data.stream()
                .map(DataUser::getAvatar)
                .map(s -> {
                    String[] str = s.split("/");
                    return str[str.length - 1];
                })
                .collect(Collectors.toList());

        for (String fileName : avatarFileNames) {
            Assert.assertEquals(avatarFileNames.stream()
                    .filter(fileName::equalsIgnoreCase)
                    .count(), 1, "Ожидали уникальное имя файлы, имя файла '" + fileName + "' повторяется");
        }

        deleteSpec();
    }

    @Test(dataProvider = "successfulLoginTest", dataProviderClass = DataProvider.class)
    public void loginSuccessfulTest(String baseUri, int statusCode, Map<String, String> requestData, String expectedValue) {
        installSpec(requestSpec(baseUri), responseSpec(statusCode));

        LoginResponse loginResponse = given()
                .when()
                .body(requestData)
                .post("/api/login")
                .body()
                .as(LoginResponse.class);

        Assert.assertNotNull(loginResponse.getToken(), "Ожидалось наличие токена, токен в ответе отсутствует");
        Assert.assertEquals(loginResponse.getToken(), expectedValue, "Значение ожидалось '" + expectedValue + "', получили: " + loginResponse.getToken());

        deleteSpec();
    }

    @Test(dataProvider = "unsuccessfulLoginTest", dataProviderClass = DataProvider.class)
    public void unsuccessfulLoginTest(String baseUri, int statusCode, Map<String, String> requestData, String expectedValue) {
        installSpec(requestSpec(baseUri), responseSpec(statusCode));

        LoginResponse loginResponse = given()
                .when()
                .body(requestData)
                .post("/api/login")
                .body()
                .as(LoginResponse.class);

        Assert.assertNotNull(loginResponse.getError(), "Ожидалось наличие ключа 'error', ключ в ответе отсутствует");
        Assert.assertEquals(loginResponse.getError(), expectedValue, "Ожидалось значение '" + expectedValue + "', получили: " + loginResponse.getError());

        deleteSpec();
    }

    @Test(dataProvider = "dataSortedFromYear", dataProviderClass = DataProvider.class)
    public void dataSortedFromYear(String baseUri, int statusCode) {
        installSpec(requestSpec(baseUri), responseSpec(statusCode));

        List<DataResource> data = given()
                .when()
                .get("/api/unknown")
                .body()
                .jsonPath()
                .getList("data", DataResource.class);

        Assert.assertFalse(data.isEmpty(), "Ожидали не пустой список, получили пустой список");
        int[] years = data.stream().mapToInt(DataResource::getYear).toArray();
        Assert.assertTrue(ArrayUtils.isSorted(years), "Ожидалось что данные будут отсортированны по годам, текущий порядок: " + Arrays.toString(years));

        deleteSpec();
    }

    @Test(dataProvider = "tagCounter", dataProviderClass = DataProvider.class)
    public void tagNumberTest(String baseUri, int statusCode, int expectedCount) {
        installSpec(requestSpec(baseUri), responseSpec(statusCode));
        Node node = given()
                .accept(ContentType.XML)
                .when()
                .get()
                .xmlPath().get();

        int actualTagCount = getTagCount(node);

        Assert.assertEquals(actualTagCount, expectedCount,
                "Ожидалось количество тегов равное " + expectedCount + ", текущее количество: " + actualTagCount);

        deleteSpec();
    }

    private int getTagCount(Node node) {
        if (Objects.isNull(node)) return 0;
        if (node.children().isEmpty()) return 1;
        int result = 1;
        int subNodes = 0;
        Iterator<Node> nodeIterator = node.children().nodeIterator();
        while (nodeIterator.hasNext()) {
            subNodes += getTagCount(nodeIterator.next());
        }
        result += subNodes;
        return result;
    }
}
