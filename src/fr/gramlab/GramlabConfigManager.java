package fr.gramlab;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;

import fr.umlv.unitex.config.AbstractConfigModel;
import fr.umlv.unitex.config.Preferences;
import fr.umlv.unitex.grf.GraphPresentationInfo;
import fr.umlv.unitex.io.Encoding;

public class GramlabConfigManager extends AbstractConfigModel {

	public File getAlphabet(String language) {
		// TODO Auto-generated method stub
		return null;
	}

	public Font getConcordanceFont(String language) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getConcordanceFontName(String language) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getConcordanceFontSize(String language) {
		// TODO Auto-generated method stub
		return 0;
	}

	public File getConfigFileForLanguage(String language) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCurrentLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	public Encoding getEncoding(String language) {
		// TODO Auto-generated method stub
		return null;
	}

	public GraphPresentationInfo getGraphPresentationPreferences(String language) {
		// TODO Auto-generated method stub
		return null;
	}

	public File getGraphRepositoryPath(String language) {
		// TODO Auto-generated method stub
		return null;
	}

	public File getHtmlViewer(String language) {
		// TODO Auto-generated method stub
		return null;
	}

	public Font getInputFont(String language) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getInputFontSize(String language) {
		// TODO Auto-generated method stub
		return 0;
	}

	public File getLogDirectory(String language) {
		// TODO Auto-generated method stub
		return null;
	}

	public Preferences getPreferences(String language) {
		// TODO Auto-generated method stub
		return null;
	}

	public Font getTextFont(String language) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isCharByCharLanguage(String language) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isMorphologicalUseOfSpaceAllowed(String language) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRightToLeftForGraphs(String language) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRightToLeftForText(String language) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSemiticLanguage(String language) {
		// TODO Auto-generated method stub
		return false;
	}

	public ArrayList<File> morphologicalDictionaries(String language) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean mustLog(String language) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onlyCosmetic(String language) {
		// TODO Auto-generated method stub
		return false;
	}

	public void savePreferences(Preferences p, String language) {
		// TODO Auto-generated method stub
		
	}

	public boolean svnMonitoring(String language) {
		// TODO Auto-generated method stub
		return false;
	}
}
