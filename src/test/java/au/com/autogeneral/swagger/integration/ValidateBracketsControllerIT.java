package au.com.autogeneral.swagger.integration;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Task related integration tests
 *
 *  @author Shawn Chang
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ValidateBracketsControllerIT {

    @LocalServerPort
    private int port;

    final private TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void testGetValid() throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>("", headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(createURLWithPort("/tasks/validateBrackets"))
                .queryParam("input", "[(]");
        String uri = builder.build(false).toUriString();
        ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.GET, entity, String.class);

        String expected = "{\n" +
                "  \"input\": \"[(]\",\n" +
                "  \"isBalanced\": false\n" +
                "}";

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        String obj = response.getBody();
        JSONAssert.assertEquals(expected, obj, false);
    }

    @Test
    public void testInvalid() throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>("", headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(createURLWithPort("/tasks/validateBrackets"))
                .queryParam("input", "");
        String uri = builder.build(false).toUriString();
        ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.GET, entity, String.class);

        String expected = "{\n" +
                "  \"details\": [\n" +
                "    {\n" +
                "      \"location\": \"params\",\n" +
                "      \"param\": \"text\",\n" +
                "      \"msg\": \"Must be between 1 and 50 chars long\",\n" +
                "      \"value\": \"\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"name\": \"ValidationError\"\n" +
                "}";

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String obj = response.getBody();
        JSONAssert.assertEquals(expected, obj, false);
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}