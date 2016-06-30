package br.ufpe.cin.integrativocbr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class GryphonResultUtil {
	
	private static final String defaultJsonFile; 
	
	static {
		defaultJsonFile = "/results/db_localhost_3306_uniprot.json";
	}
	
	public static String readFile(String resultFile) throws IOException {
		StringBuilder result = new StringBuilder();
		InputStream fileIn = GryphonResultUtil.class.getResource(resultFile).openStream();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(fileIn));
			try {
				String line;
				while ((line = reader.readLine()) != null) {
					result.append(line);
					result.append("\n");
				}
				return result.toString();
			} finally {
				reader.close();
			}
		} finally {
			fileIn.close();
		}
	}
	
	public static String removeParenthesesAndOrganismPrefix(String label) {
		return label.replaceAll("\\(.*?\\)$", "")
				.replaceFirst("organism #", "")
				.replaceFirst("biological_process #", "")
				.replaceFirst("cellular_component #", "")
				.replaceAll("\\[.*?\\]", "").trim();
	}
	
	public static Set<GryphonResult> readResults(String resultFile) throws Exception {
		JSONObject jsonObj = new JSONObject(readFile(resultFile));
		JSONObject results = jsonObj.getJSONObject("results");
		JSONArray bindings = results.getJSONArray("bindings");
		Set<GryphonResult> resultList = new HashSet<GryphonResult>();

		System.out.println("List of Cases:");
		for (int i = 0; i < bindings.length(); i++) {
			JSONObject binding = bindings.getJSONObject(i);
			
			String[] names = JSONObject.getNames(binding);
			if (names != null && names.length > 0) {
				String[] labels = new String[names.length];
				for (int l = 0; l < names.length; l++) {
					labels[l] = binding.getJSONObject(names[l]).getString("value");
					labels[l] = removeParenthesesAndOrganismPrefix(labels[l]);
				}
				GryphonResult result = new GryphonResult();
				result.setTuples(labels);
				resultList.add(result);
				System.out.println(result);
			}
		}
		System.out.println("Number of Cases from Gryphon: " + resultList.size());
		return resultList;
	}
	
	public static Set<GryphonResult> readResults() throws Exception {
		return readResults(defaultJsonFile);
	}

}
