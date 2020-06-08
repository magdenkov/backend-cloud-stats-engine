package com.magdenkov.cloudstatsengine;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration(classes = CloudStatsEngineApplication.class)
public class CloudStatsEngineApplicationTests {

	private MockMvc mvc;

	@Autowired
	private WebApplicationContext context;

	private String EXPECTED_RESULT_FOR_75 = "{\n" +
			"    \"originalInput\": [\n" +
			"        {\n" +
			"            \"depth\": \"1000.0\",\n" +
			"            \"gammaRay\": \"80\",\n" +
			"            \"rhob\": \"-999.25\"\n" +
			"        },\n" +
			"        {\n" +
			"            \"depth\": \"1000.5\",\n" +
			"            \"gammaRay\": \"80\",\n" +
			"            \"rhob\": \"-999.25\"\n" +
			"        },\n" +
			"        {\n" +
			"            \"depth\": \"1001.0\",\n" +
			"            \"gammaRay\": \"85\",\n" +
			"            \"rhob\": \"-999.25\"\n" +
			"        },\n" +
			"        {\n" +
			"            \"depth\": \"1001.5\",\n" +
			"            \"gammaRay\": \"85\",\n" +
			"            \"rhob\": \"2.5\"\n" +
			"        },\n" +
			"        {\n" +
			"            \"depth\": \"1002.0\",\n" +
			"            \"gammaRay\": \"75\",\n" +
			"            \"rhob\": \"2.6\"\n" +
			"        },\n" +
			"        {\n" +
			"            \"depth\": \"1002.5\",\n" +
			"            \"gammaRay\": \"75\",\n" +
			"            \"rhob\": \"2.7\"\n" +
			"        }\n" +
			"    ],\n" +
			"    \"guavaCalculatedPercentile\": {\n" +
			"        \"depth\": \"1001.8750\",\n" +
			"        \"gammaRay\": \"83.7500\",\n" +
			"        \"rhob\": \"2.6500\"\n" +
			"    },\n" +
			"    \"apacheCalculatedPercentile\": {\n" +
			"        \"depth\": \"1002.1250\",\n" +
			"        \"gammaRay\": \"85.0000\",\n" +
			"        \"rhob\": \"2.7000\"\n" +
			"    }\n" +
			"}";

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.webAppContextSetup(context)
				.build();
	}

	@Test
	public void checkPercentileCalcOnGivenDataSet() throws Exception {
		// given
		String inputCsv = "DEPTH,GR,RHOB\n" +
				"1000.0,80,-999.25\n" +
				"1000.5,80,-999.25\n" +
				"1001.0,85,-999.25\n" +
				"1001.5,85,2.5\n" +
				"1002.0,75,2.6\n" +
				"1002.5,75,2.7";
		final MockMultipartFile csvdata = new MockMultipartFile("file", "testdata.csv", "text/plain", inputCsv.getBytes());

		// when
		final String actualJsonResponse = mvc.perform(MockMvcRequestBuilders.multipart("/files/upload")
				.file(csvdata)
				.param("percentile", "75"))
				.andExpect(status().is(200))
				.andReturn().getResponse().getContentAsString();

		// then
		JSONAssert.assertEquals(EXPECTED_RESULT_FOR_75, actualJsonResponse, JSONCompareMode.LENIENT);
	}

	@Test
	public void checkForInvalidPercentileInput() throws Exception {
		// given
		String inputCsv = "DEPTH,GR,RHOB\n" +
				"1002.5,75,2.7";
		final MockMultipartFile csvdata = new MockMultipartFile("file", "testdata.csv", "text/plain", inputCsv.getBytes());

		// when
		mvc.perform(MockMvcRequestBuilders.multipart("/files/upload")
				.file(csvdata)
				.param("percentile", "101")) // percentile can't be greater then 100
		//then
				.andExpect(status().is(400)); // bad request
	}
}
