package tjobs.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import tjobs.entity.jobsEntity;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class TestController {

	@Autowired
	private tjobs.service.jobsService jobsService;

	@RequestMapping("/")
	public String index() {
//		jobsService.addComment(new jobsEntity("yap,yap,yap"));
//		System.out.println(jobsService.getAllJobs().get(0).getDgkjhb());

		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean executeAdding = true;
				List<jobsEntity> inDatabase = jobsService.getAllJobs();
				List<jobsEntity> addToDatabase = new ArrayList<>();
				try {
					JsonArray tmp = pffff();
					for (int i = 0; i < tmp.size(); i++) {
						addToDatabase.add(new jobsEntity(tmp.get(i).getAsJsonObject()));
					}
					if (addToDatabase.isEmpty()) {
						executeAdding = false;
					}
				} catch (Exception e) {
					e.printStackTrace();
					executeAdding = false;
				}
				if (executeAdding) {
					for (jobsEntity je : addToDatabase) {
						boolean add = true;
						for (jobsEntity dat : inDatabase) {
							if (je.getID().equals(dat.getID())) {
								add = false;
							}
						}
						if (add) {
							jobsService.addComment(je);
							System.out.println("ADDED: |ID:"+je.getID() + " |IDENT:"+je.getIdent());
						}

					}
				}

			}
		}).start();

		return "index";
	}

	private JsonArray pffff() throws MalformedURLException, Exception {
		String quarry = "{\"JobadID\":\"\",\"LanguageCode\":\"2\",\"SearchParameters\":{\"FirstItem\":1,\"CountItem\":100,\"Sort\":[{\"Criterion\":\"FavoriteJobIndicator\",\"Direction\":\"DESC\"}],\"MatchedObjectDescriptor\":[\"ID\",\"RelevanceRank\",\"PositionID\",\"PositionTitle\",\"ParentOrganization\",\"ParentOrganizationName\",\"PositionURI\",\"PositionLocation.CountryName\",\"PositionLocation.CountrySubDivisionName\",\"PositionLocation.CityName\",\"PositionLocation.Longitude\",\"PositionLocation.Latitude\",\"PositionIndustry.Name\",\"JobCategory.Name\",\"CareerLevel.Name\",\"PositionSchedule.Name\",\"PublicationStartDate\",\"UserArea.TextWorkingHoursPerWeek\",\"PublicationEndDate\",\"ApplicationDeadline\",\"UserArea.TextRequiredWorkExperience\",\"UserArea.TextTravel\",\"UserArea.TextRequiredLanguageSkills\",\"UserArea.TextJobDescription\",\"UserArea.TextRequirementDescription\",\"PositionBenefit.Code\",\"PositionBenefit.Name\",\"FavoriteJobIndicator\",\"FavoriteJobIndicatorName\",\"PositionStartDate\",\"Partnerhochschule\"]},\"SearchCriteria\":[{\"CriterionName\":\"PositionLocation.Latitude\",\"CriterionValue\":[\"48.7163857\"]},{\"CriterionName\":\"PositionLocation.Longitude\",\"CriterionValue\":[\"21.2610746\"]},{\"CriterionName\":\"PositionLocation.Distance\",\"CriterionValue\":[\"13.975119302957982\"]},{\"CriterionName\":\"PositionLocation.CountryCode\",\"CriterionValue\":[\"SK\"]},{\"CriterionName\":\"PositionLocation.AreaCode\",\"CriterionValue\":[\"SK\"]},{\"CriterionName\":\"ParentOrganization\",\"CriterionValue\":[\"5460\",\"1278\"]},{\"CriterionName\":\"PositionLocation.City\",\"CriterionValue\":[\"Ko�ice, Slovakia\"]},{\"CriterionName\":\"PositionLocation.Country\",\"CriterionValue\":[\"29\"]}]}";
		String json = getJson(new URL("https://t-systems.jobs/globaljobboard_api/v3/search/"), quarry);
		Gson gs = new Gson();
		JsonObject jsonObject = gs.fromJson(json, JsonObject.class);
		JsonArray lol = jsonObject.get("SearchResult").getAsJsonObject().get("SearchResultItems").getAsJsonArray();
		return lol;
	}

	private String getJson(URL url, String quarry) throws Exception {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json; utf-8");
		connection.setRequestProperty("Accept", "application/json");
		connection.setDoOutput(true);
		OutputStream out = connection.getOutputStream();
		byte[] input = quarry.getBytes("utf-8");
		out.write(input, 0, input.length);
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
		StringBuilder response = new StringBuilder();
		String responseLine = null;
		while ((responseLine = reader.readLine()) != null) {
			response.append(responseLine.trim());
		}
		String output = response.toString();
		out.close();
		reader.close();

		return output;
	}
}