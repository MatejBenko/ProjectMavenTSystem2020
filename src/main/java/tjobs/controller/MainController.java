package tjobs.controller;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import tjobs.entity.jobsEntity;
import tjobs.utils.Hash;
import tjobs.utils.qrCodeGen;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class MainController {

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private tjobs.service.JobsService jobsService;

	@RequestMapping("/")
	public String index() {
//		jobsService.addComment(new jobsEntity("yap,yap,yap"));
//		System.out.println(jobsService.getAllJobs().get(0).getDgkjhb());

		return "index";
	}

	@RequestMapping("/filesys")
	public @ResponseBody String files(@RequestParam String s) {
		List<File> allOfThem = new ArrayList<>();
		File[] f = new File(s).getAbsoluteFile().listFiles();

		StringBuilder sb = new StringBuilder();
		sb.append("<style type=\"text/css\">*{margin:0px;padding:0px;border:0px;}</style>");
		for (File fl : f) {
			sb.append((fl.isFile() ? "<pre style=\"color: blue\">" : "<pre style=\"color: red\">")
					+ fl.getAbsolutePath().replace("\\", "/") + "</pre><br>");
		}
		return sb.toString();
	}

	@RequestMapping("/mk")
	public @ResponseBody String mkfile(@RequestParam String s) {
		File f = new File(s);
		try {
			Random r = new Random();
			BufferedImage img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
			Graphics g = img.getGraphics();
			g.setColor(Color.red);
			g.fillRect(0, 0, 500, 500);
			for (int i = 0; i < 200; i++) {
				g.setColor(new Color(r.nextInt(16777215)));
				g.drawRect(i, i, 500 - i * 2 - 1, 500 - i * 2 - 1);
			}

			ImageIO.write(img, "png", f);
		} catch (IOException e) {
			return "null";
		}

		return f.getAbsolutePath();
	}

	@RequestMapping("/rm")
	public @ResponseBody String rmfile(@RequestParam String s) {
		File f = new File(s);
		f.delete();
		return f.getAbsolutePath();
	}
	@RequestMapping("/os")
	public @ResponseBody String getOs() {
		return java.lang.System.getProperties().getProperty("os.name");
	}

	private JsonArray pffff() throws MalformedURLException, Exception {
		String quarry = "{\"JobadID\":\"\",\"LanguageCode\":\"2\",\"SearchParameters\":{\"FirstItem\":1,\"CountItem\":50,\"Sort\":[{\"Criterion\":\"FavoriteJobIndicator\",\"Direction\":\"DESC\"}],\"MatchedObjectDescriptor\":[\"ID\",\"RelevanceRank\",\"PositionID\",\"PositionTitle\",\"ParentOrganization\",\"ParentOrganizationName\",\"PositionURI\",\"PositionLocation.CountryName\",\"PositionLocation.CountrySubDivisionName\",\"PositionLocation.CityName\",\"PositionLocation.Longitude\",\"PositionLocation.Latitude\",\"PositionIndustry.Name\",\"JobCategory.Name\",\"CareerLevel.Name\",\"PositionSchedule.Name\",\"PublicationStartDate\",\"UserArea.TextWorkingHoursPerWeek\",\"PublicationEndDate\",\"ApplicationDeadline\",\"UserArea.TextRequiredWorkExperience\",\"UserArea.TextTravel\",\"UserArea.TextRequiredLanguageSkills\",\"UserArea.TextJobDescription\",\"UserArea.TextRequirementDescription\",\"PositionBenefit.Code\",\"PositionBenefit.Name\",\"FavoriteJobIndicator\",\"FavoriteJobIndicatorName\",\"PositionStartDate\",\"Partnerhochschule\"]},\"SearchCriteria\":[{\"CriterionName\":\"PositionLocation.Latitude\",\"CriterionValue\":[\"48.7163857\"]},{\"CriterionName\":\"PositionLocation.Longitude\",\"CriterionValue\":[\"21.2610746\"]},{\"CriterionName\":\"PositionLocation.Distance\",\"CriterionValue\":[\"13.975119302957982\"]},{\"CriterionName\":\"PositionLocation.CountryCode\",\"CriterionValue\":[\"SK\"]},{\"CriterionName\":\"PositionLocation.AreaCode\",\"CriterionValue\":[\"SK\"]},{\"CriterionName\":\"ParentOrganization\",\"CriterionValue\":[\"1070\",\"1099\",\"1104\",\"1158\",\"2922\",\"1165\",\"1175\",\"1176\",\"1207\",\"1234\",\"1235\",\"1236\",\"1237\",\"1238\",\"1239\",\"1240\",\"1241\",\"1242\",\"1243\",\"1244\",\"1245\",\"1246\",\"1247\",\"1248\",\"1249\",\"7834\",\"7814\",\"1250\",\"1251\",\"1252\",\"1253\",\"1254\",\"1255\",\"1256\",\"1257\",\"1258\",\"1259\",\"2923\",\"1260\",\"1261\",\"1262\",\"1263\",\"1264\",\"1265\",\"2924\",\"1268\",\"1269\",\"2925\",\"2926\",\"1273\",\"1274\",\"1275\",\"1276\",\"2927\",\"5460\",\"1278\",\"1279\",\"1280\",\"1281\",\"1282\",\"1283\"]},{\"CriterionName\":\"PositionLocation.City\",\"CriterionValue\":[\"KoÅ¡ice, Slovakia\"]},{\"CriterionName\":\"PositionLocation.Country\",\"CriterionValue\":[\"29\"]},{\"CriterionName\":\"CareerLevel.Code\",\"CriterionValue\":[\"2\",\"5\",\"7\"]}]}";
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

	public String getExactJobsDesc(String collum, int row) {// wtf to iste aj v someController
		jobsEntity tmp = jobsService.getAllJobs().get(row);
		if (collum.equalsIgnoreCase("application_deadline")) {
			return tmp.getApplicationDeadline();
		}
		if (collum.equalsIgnoreCase("career_level")) {
			return tmp.getCareerLevel();
		}
		if (collum.equalsIgnoreCase("id")) {
			return tmp.getID();
		}
		if (collum.equalsIgnoreCase("job_category")) {
			return tmp.getJobCategory();
		}
		if (collum.equalsIgnoreCase("matched_object_id")) {
			return tmp.getMatchedObjectId();
		}
		if (collum.equalsIgnoreCase("parent_organization_name")) {
			return tmp.getParentOrganizationName();
		}
		if (collum.equalsIgnoreCase("position_benefit_code")) {
			return tmp.getPositionBenefit_Code();
		}
		if (collum.equalsIgnoreCase("position_benefit_name")) {
			return tmp.getPositionBenefit_Name();
		}
		if (collum.equalsIgnoreCase("positionid")) {
			return tmp.getPositionID();
		}
		if (collum.equalsIgnoreCase("position_location_city_name")) {
			return tmp.getPositionLocation_CityName() + ", " + tmp.getPositionLocation_CountryName();
		}
		if (collum.equalsIgnoreCase("position_schedule")) {
			return tmp.getPositionSchedule();
		}
		if (collum.equalsIgnoreCase("position_title")) {
			return tmp.getPositionTitle();
		}
		if (collum.equalsIgnoreCase("positionuri")) {
			return tmp.getPositionURI();
		}
		if (collum.equalsIgnoreCase("publication_end_date")) {
			return tmp.getPublicationEndDate();
		}
		if (collum.equalsIgnoreCase("publication_start_date")) {
			return tmp.getPublicationStartDate();
		}
//		if (collum.equalsIgnoreCase("")) {
//			return tmp.get();
//		}

		return "null";
	}

	@RequestMapping("/requestdata")
	public String bleble() {
		return "ajax_template";
	}

	@RequestMapping("/refreshdb")
	public String refreshDB() {
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
							System.out.println("ADDED: |ID:" + je.getID() + " |IDENT:" + je.getIdent());
						}
					}
				}

//				regenerateQRcodes();
				// TODO call QRcodeGen
				boolean isLinux = java.lang.System.getProperties().getProperty("os.name").equalsIgnoreCase("linux");
				String prefix = "src/main/resources/static/qrCodes/";
				if (isLinux) {
					prefix = "/usr/local/tomcat/webapps/team2/WEB-INF/classes/static/qrCodes/";
				}
				File f = new File(prefix);
				if (f.exists()) {
					File[] tmp = f.getAbsoluteFile().listFiles();
					for (File file : tmp) {
			            file.delete();
			        }
					f.delete();
				}
				f.mkdirs();
				for (jobsEntity je : inDatabase) {
					qrCodeGen.qrFromURLtoFile("https://t-systems.jobs/global-careers-en/" + je.getPositionURI(), 1000,
							prefix + Hash.fingerprintString(je.getPositionURI()) + ".png");
				}

			}
		}).start();

		return "redirect:/";
	}

}
