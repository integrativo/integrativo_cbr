package br.ufpe.cin.integrativocbr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import jcolibri.exception.OntologyAccessException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GryphonResultUtil {
	
	private static final File defaultJsonFile; 
	
	static {
		defaultJsonFile = new File(GryphonResultUtil.class.getResource("/results").getFile(), 
				"db_localhost_3306_uniprot.json");
	}
	
	public static String readFile(File resultFile) throws IOException {
		StringBuilder result = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(resultFile));
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
	}
	
	public static String removeParenthesesAndOrganismPrefix(String label) {
		return label.replaceAll("\\(.*?\\)$", "")
				.replaceFirst("organism #", "")
				.replaceFirst("biological_process #", "")
				.replaceFirst("cellular_component #", "");
	}
	
	public static Set<GryphonResult> readResults(File resultFile) throws JSONException, IOException, OntologyAccessException {
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
	
	public static Set<GryphonResult> readResults() throws JSONException, IOException, OntologyAccessException {
		return readResults(defaultJsonFile);
	}

}
