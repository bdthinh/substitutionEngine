package tifmo.utils;

import ac.biu.nlp.normalization.BiuNormalizer;
import tifmo.en.EnWordNet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bdthinh on 10/25/14.
 */
public class Normalizer {
	private static BiuNormalizer biu;
	private static List<String> numbers;

	public static void init() {
		try {
			biu = new BiuNormalizer(new File(new File("").getCanonicalPath().concat("/resources/BiuNormalizer_rules.txt")));
			numbers = Arrays.asList("twenty-one", "twenty-two", "twenty-three", "twenty-four", "twenty-five", "twenty-six", "twenty-seven", "twenty-eight", "twenty-nine",
							"thirty-one", "thirty-two", "thirty-three", "thirty-four", "thirty-five", "thirty-six", "thirty-seven", "thirty-eight", "thirty-nine",
							"forty-one", "forty-two", "forty-three", "forty-four", "forty-five", "forty-six", "forty-seven", "forty-eight", "forty-nine",
							"fifty-one", "fifty-two", "fifty-three", "fifty-four", "fifty-five", "fifty-six", "fifty-seven", "fifty-eight", "fifty-nine",
							"sixty-one", "sixty-two", "sixty-three", "sixty-four", "sixty-five", "sixty-six", "sixty-seven", "sixty-eight", "sixty-nine",
							"seventy-one", "seventy-two", "seventy-three", "seventy-four", "seventy-five", "seventy-six", "seventy-seven", "seventy-eight", "seventy-nine",
							"eighty-one", "eighty-two", "eighty-three", "eighty-four", "eighty-five", "eighty-six", "eighty-seven", "eighty-eight", "eighty-nine",
							"ninety-one", "ninety-two", "ninety-three", "ninety-four", "ninety-five", "ninety-six", "ninety-seven", "ninety-eight", "ninety-nine");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String applyHyphenRule(String pw) {
		if (pw.indexOf("-") != -1) {
			if (pw == "")
				pw = ",";
			else if (pw.matches("-[A-Za-z].+"))
				pw.replaceAll("-", "(");
			else if (pw.substring(pw.length() - 1, pw.length()) == "-")
				pw.replaceAll("-", ")");
			else {
				String regex = "(.*[A-Za-z0-9])([^A-Za-z0-9]*)";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(pw);
				if (matcher.matches()) {
					String normal = matcher.group(1);
					if (numbers.contains(normal.toLowerCase())) {
						pw.replaceAll("-", " ");
					} else if (!EnWordNet.hasWord(normal)) {
						String[] ws = normal.split("-");
						boolean flag = false;
						for (String w : ws) {
							if (w.matches("[0-9\\.]+") || EnWordNet.hasWord(w)) {
								flag = true;
								break;
							}
						}
						if (flag)
							pw.replaceAll("-", " ");
					}
				}
			}
		}
		return pw;
	}

	private static String applySlashRule(String pw) {
		if (pw.matches("[a-zA-Z]+/[a-zA-Z]+"))
			pw.replaceAll("/", " ");
		return pw;
	}

	public static String apply(String text) {
		String output = text.trim();
		try {
			//hyphen hyphen rule
			output = output.replaceAll("--+", ". ");

			String[] tmpsp = output.split(" +");
			StringBuilder sb = new StringBuilder();
			for (String pw : tmpsp) {
				pw = applyHyphenRule(pw);
				pw = applySlashRule(pw);
				sb.append(pw).append(" ");
			}
			//return output after normalizing with string rules
			return biu.normalize(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


}
