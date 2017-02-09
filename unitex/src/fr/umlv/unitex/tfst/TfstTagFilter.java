package fr.umlv.unitex.tfst;

import java.util.ArrayList;

public class TfstTagFilter {

	private boolean keepSurface = true;
	private boolean keepLemma = false;

	private boolean allExceptInfClasses = false;

	private String directListing;
	private String tagLetterNumber;
	private String tagPrefixString;

	public TfstTagFilter(boolean keepSurface, boolean keepLemma, boolean allExceptInfClasses, String directListing,
			String tagLetterNumber, String tagPrefixString) {
		this.keepSurface = keepSurface;
		this.keepLemma = keepLemma;
		this.allExceptInfClasses = allExceptInfClasses;
		this.directListing = directListing;
		this.tagLetterNumber = tagLetterNumber;
		this.tagPrefixString = tagPrefixString;
	}

	public String filterLine(String line) {
		String words[] = line.split(" ");

		StringBuilder stringBuilderLine = new StringBuilder();

		// We should not process the last token {S}
		for (int i = 0; i < words.length - 1; i++) {
			boolean isAmbiguous = words[i].matches("^\\(.*\\)$");

			if (isAmbiguous) {
				// Remove parentheses
				words[i] = words[i].substring(1, words[i].length() - 1);
				StringBuilder stringBuilderCandidates = new StringBuilder();

				// In this case, the word is one of several candidates
				// A candidate is a word
				String candidates[] = words[i].split("\\|");
				for (int j = 0; j < candidates.length; j++) {
					String filteredWords = filterWord(candidates[j]);
					stringBuilderCandidates.append(filteredWords);

					if (!filteredWords.isEmpty() && j < candidates.length - 1) {
						stringBuilderCandidates.append("|");
					}
				}

				String filteredCandidates = stringBuilderCandidates.toString();
				// Remove the last |
				if (!filteredCandidates.isEmpty() && filteredCandidates.endsWith("|")) {
					filteredCandidates = filteredCandidates.substring(0, filteredCandidates.length() - 1);
				}
				// Add the parentheses if ambiguous
				if (filteredCandidates.contains("|")) {
					filteredCandidates = "(" + filteredCandidates + ")";
				}

				// Reconstruct the line
				stringBuilderLine.append(filteredCandidates);

				if (!filteredCandidates.isEmpty() && i < words.length - 1) {
					stringBuilderLine.append(" ");
				}

			} else {
				String filteredWords = filterWord(words[i]);

				stringBuilderLine.append(filteredWords);

				if (!filteredWords.isEmpty() && i < words.length - 1) {
					stringBuilderLine.append(" ");
				}
			}
		}

		String filteredLine = stringBuilderLine.toString().trim() + " {S}\n";

		return filteredLine;
	}

	private String filterWord(String word) {

		// When the word is composed of multiple tokens
		String tokens[] = word.split("\\+");

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < tokens.length; i++) {
			String filteredToken = filterWordTags(tokens[i]);
			stringBuilder.append(filteredToken);

			if (!filteredToken.isEmpty() && i < tokens.length - 1) {
				stringBuilder.append("+");
			}
		}

		String filteredWord = stringBuilder.toString();
		// Remove the last +
		if (!filteredWord.isEmpty() && filteredWord.endsWith("+")) {
			filteredWord = filteredWord.substring(0, filteredWord.length() - 1);
		}

		return filteredWord;
	}

	private String filterWordTags(String word) {
		String wordTags[] = word.split("/");

		String tags[] = directListing.split("\\|");
		ArrayList<Integer> tagLengthes = new ArrayList<>();
		String tagPrefixes[] = tagPrefixString.split("\\|");

		String lengthes[] = tagLetterNumber.split("\\|");
		for (String length : lengthes) {
			try {
				int tagLength = Integer.parseInt(length);
				tagLengthes.add(tagLength);
			} catch (NumberFormatException e) {
			}
		}

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < wordTags.length; i++) {
			if (i == 0) { // Surface

				if (keepSurface) {
					stringBuilder.append(wordTags[i]).append("/");
				}
			} else if (i == 1) { // Lemma

				if (keepLemma) {
					stringBuilder.append(wordTags[i]).append("/");
				}
			} else { // Codes
				if (allExceptInfClasses) {
					if (!wordTags[i].contains("#")) {
						stringBuilder.append(wordTags[i]).append("/");
						continue;
					}
				} else {

					if (tagLetterNumber.isEmpty() && tagPrefixString.isEmpty() && directListing.isEmpty()) {
						stringBuilder.append(wordTags[i]).append("/");
						continue;
					}

					boolean keepCode = false;

					// Check tags length
					if (!tagLetterNumber.isEmpty()) {
						for (int tagLength : tagLengthes) {
							if (wordTags[i].length() == tagLength) {
								keepCode = true;
								break;
							}
						}
					}

					// Check prefixes
					if (!keepCode && !tagPrefixString.isEmpty()) {
						for (String tagPrefix : tagPrefixes) {
							if (wordTags[i].startsWith(tagPrefix)) {
								keepCode = true;
								break;
							}
						}
					}

					// Check tags
					if (!keepCode && !directListing.isEmpty()) {
						for (String tag : tags) {
							if (wordTags[i].equals(tag)) {
								keepCode = true;
								break;
							}
						}
					}

					if (keepCode) {
						stringBuilder.append(wordTags[i]).append("/");
					}

				}

			}
		}

		String filteredWord = stringBuilder.toString();
		// Remove the last /
		if (!filteredWord.isEmpty()) {
			filteredWord = filteredWord.substring(0, filteredWord.length() - 1);
		}

		return filteredWord;
	}
}
