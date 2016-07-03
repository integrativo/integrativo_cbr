package br.ufpe.cin.integrativocbr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class GryphonResultUtil {
	
	private static final File defaultJsonFile = new File(System.getProperty("user.home") + "/mst/GryphonFramework/integrationExample/results/db_localhost_3306_uniprot.json"); 
	
	public static String readFile(File resultFile) throws IOException {
		StringBuilder result = new StringBuilder();
		// InputStream fileIn = GryphonResultUtil.class.getResource(resultFile).openStream();
		InputStream fileIn = new FileInputStream(resultFile);
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
				.replaceFirst("protein_name #", "")
				.replaceFirst("molecule #", "")
				.replaceFirst("\\[Includes:.*", "")
				.replaceAll("\\[.*?\\]", "").trim();
	}
	
	public static List<GryphonResult> readResults(File resultFile) throws Exception {
		JSONObject jsonObj = new JSONObject(readFile(resultFile));
		JSONObject results = jsonObj.getJSONObject("results");
		JSONArray bindings = results.getJSONArray("bindings");
		List<GryphonResult> resultList = new ArrayList<GryphonResult>();

		System.out.println("List of Cases:");
		for (int i = 0; i < bindings.length(); i++) {
			JSONObject binding = bindings.getJSONObject(i);
			
			String[] names = sortNames(JSONObject.getNames(binding));
			names = sortNames(names);
			if (i == 0) {
				System.out.println("First result column order: " + Arrays.toString(names));
			}
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
	
	private static String[] sortNames(String[] names) {
		String[] newOrder = new String[] {"labelx", "labels1", "labels2", "labels3"};
		String[] sortedNames = new String[names.length];
		
		int index = 0;
		for (String newOrderName : newOrder) {
			for (String name : names) {
				if (name.equals(newOrderName)) {
					sortedNames[index++] = name;
				}
			}
		}
		return sortedNames;
	}

	public static List<GryphonResult> readResults() throws Exception {
		return readResults(defaultJsonFile);
	}

}
