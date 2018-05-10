/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import static leximir.delac.menu.MenuDelac.getNodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import util.CompoundsUtils;

/**
 *
 * @author rojo
 */
public class StrategieHelper {

    static Object[][] completeJTableStrategie(List<String> words,String strategy) throws IOException {
        Object[][] obj = new Object[words.size()][8];
        
        return obj;
    }
    private  List<String> getLemaFromXmlRule(String[] words, char separator, String[] POSwords) throws ParserConfigurationException, SAXException, IOException {
        boolean ruleIdFound = false;
        boolean flxFound = false;
        String flx = new String();
        String ruleNo = "";
        NodeList nList = getNodeList();
        String returns = "";
        List<String> ret = new ArrayList<>();
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                if (eElement.getAttribute("WordNo").equals(Integer.toString(words.length))) {
                    ruleIdFound = true;
                    NodeList ndRule = eElement.getElementsByTagName("Rule");
                    for (int i = 0; i < ndRule.getLength(); i++) {
                        Node nNodeRule = ndRule.item(i);
                        if (nNodeRule.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElementRule = (Element) nNodeRule;
                                flxFound = true;
                                flx = eElementRule.getAttribute("CFLX");
                                ruleNo = eElementRule.getAttribute("ID");
                                /**
                                 * **** For General Rules ****
                                 */
                                NodeList ndRuleGenCond = eElementRule.getElementsByTagName("RuleGenCond");
                                for (int j = 0; j < ndRuleGenCond.getLength(); j++) {
                                    Node nNodeRuleGenCond = ndRuleGenCond.item(j);
                                    if (nNodeRuleGenCond.getNodeType() == Node.ELEMENT_NODE) {
                                        Element eElementRuleGenCond = (Element) nNodeRuleGenCond;
                                        NodeList ndWord = eElementRuleGenCond.getElementsByTagName("Word");
                                        for (int k = 0; k < ndWord.getLength(); k++) {
                                            Node nNodeWord = ndWord.item(k);
                                            if (nNodeWord.getNodeType() == Node.ELEMENT_NODE) {
                                                Element eElementWord = (Element) nNodeWord;
                                                if (eElementWord.getAttribute("POS").equals(POSwords[k]) || eElementWord.getAttribute("POS").equals("MOT")) {
                                                    if (eElementWord.getAttribute("Flex").equals("false") || eElementWord.getAttribute("Flex") == null || eElementWord.getAttribute("Flex").equals("")) {
                                                        returns = returns+words[k]+"(flexion off)";
                                                        //returns = returns + words[k];
                                                    } else {
                                                        //String flex = getFlex(words[k], eElementWord.getAttribute("POS"));
                                                        returns = returns+words[k]+"(flexion on)";
                                                        //returns = returns + words[k] + flex;
                                                    }
                                                } else {
                                                    returns = returns+words[k]+"(flexion off)";
                                                    //returns = returns + words[k];
                                                }
                                            }
                                            returns = returns + separator;
                                        }
                                    }
                                }
                                /**
                                 * **** End For General Rules ****
                                 */
                                ret.add(returns.substring(0, returns.length() - 1) + "," + flx + "," + ruleNo);
                                returns = "";
                            
                        }
                    }
                }
            }
        }
        if (flxFound == false) {
            throw new NullPointerException( " flx not found in rule " + words.length);
        }
        if (ruleIdFound == false) {
            throw new NullPointerException(words.length + " rule not found");
        }
        return ret;
    }
}
