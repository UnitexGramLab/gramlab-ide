/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 * *
 * regle a faire pour lundi : recuperer la regle du genRule pour Spec rule
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.swing.JTable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author rojo
 */
public class Ccompounds {

    private JTable jTableRule;
    private File pathFileXml;
    private String[] words;
    private char separator;
    private String[] posOfthisWordInJtable;
    private String sinSem;
    private JTable jTableReturn;

    public Ccompounds(JTable jtable1, String pathtext, String[] text, char separator, String[] pos, String simsem) {
        this.jTableRule = jtable1;
        this.pathFileXml = new File(pathtext);
        this.words = text;
        this.separator = separator;
        this.posOfthisWordInJtable = pos;
        this.sinSem = simsem;
    }

    /**
     * *
     * cette fonction prend un mot composé et retourne tous les mots
     * correspondant en fonction de la règle dans le fichier xml
     *
     * @param words mot a traiter
     * @param separator separateur du mot
     * @param POSwords POS se trouvant dans JtableDlf
     * @param SinSem SinSem du mot se trouvant dans le fichier compoundsTest
     * @return retourne une liste de string au format "words", "FLX",
     * "Rule","Spec/Gen","SinSem" pour chaque regle trouver
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws TransformerException
     */
    public List<String> getLemaFromXmlRule() throws ParserConfigurationException, SAXException, IOException, TransformerException {
        String flx = new String();
        String ruleNo = "";
        NodeList nList = getNodeList();
        String returns = "";
        List<String> ret = new ArrayList<>();
        HashMap<String, HashMap<String, String>> attribut = new HashMap<>();
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                if (eElement.getAttribute("WordNo").equals(Integer.toString(words.length))) {
                    NodeList ndRule = eElement.getElementsByTagName("Rule");
                    for (int i = 0; i < ndRule.getLength(); i++) {
                        Node nNodeRule = ndRule.item(i);
                        if (nNodeRule.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElementRule = (Element) nNodeRule;
                            flx = eElementRule.getAttribute("CFLX");
                            ruleNo = eElementRule.getAttribute("ID");
                            NodeList ndRuleGenCond = eElementRule.getElementsByTagName("RuleGenCond");
                            List<Boolean> allOk = new ArrayList<>();
                            for (int j = 0; j < ndRuleGenCond.getLength(); j++) {
                                Node nNodeRuleGenCond = ndRuleGenCond.item(j);
                                if (nNodeRuleGenCond.getNodeType() == Node.ELEMENT_NODE) {
                                    Element eElementRuleGenCond = (Element) nNodeRuleGenCond;
                                    NodeList ndWord = eElementRuleGenCond.getElementsByTagName("Word");
                                    for (int k = 0; k < ndWord.getLength(); k++) {
                                        HashMap<String, String> tempCondition = new HashMap<>();
                                        Node nNodeWord = ndWord.item(k);
                                        if (nNodeWord.getNodeType() == Node.ELEMENT_NODE) {
                                            Element eElementWord = (Element) nNodeWord;
                                            if (eElementWord.getAttribute("Flex") != null) {
                                                tempCondition.put("Flex", eElementWord.getAttribute("Flex"));
                                            }
                                            
                                            if (eElementWord.getAttribute("POS") != null) {
                                                tempCondition.put("POS", eElementWord.getAttribute("POS"));
                                            }
                                            if (eElementWord.getAttribute("Anim") != null) {
                                                tempCondition.put("Anim", eElementWord.getAttribute("Anim"));
                                            }
                                            if (eElementWord.getAttribute("Case") != null) {
                                                tempCondition.put("Case", eElementWord.getAttribute("Case"));
                                            }
                                            if (eElementWord.getAttribute("Nb") != null) {
                                                tempCondition.put("Nb", eElementWord.getAttribute("Nb"));
                                            }
                                            if (eElementWord.getAttribute("Num") != null) {
                                                tempCondition.put("Num", eElementWord.getAttribute("Num"));
                                            }
                                            if (eElementWord.getAttribute("Gen") != null) {
                                                tempCondition.put("Gen", eElementWord.getAttribute("Gen"));
                                            }
                                            if (eElementWord.getAttribute("Det") != null) {
                                                tempCondition.put("Det", eElementWord.getAttribute("Det"));
                                            }
                                            if (eElementWord.getAttribute("Degree") != null) {
                                                tempCondition.put("Degree", eElementWord.getAttribute("Degree"));
                                            }
                                            attribut.put(eElementWord.getAttribute("ID"), tempCondition);
                                            String pos = eElementWord.getAttribute("POS");
                                            boolean indic = lemaExistsInTable(jTableRule, words[k]);
                                            String[] posXML = eElementWord.getAttribute("POS").split(",").length > 0 ? eElementWord.getAttribute("POS").split(",") : new String[]{eElementWord.getAttribute("POS")};
                                            List<String> posXml = Arrays.asList(posXML);
                                            if (!pos.equals("!SDIC") && indic && posXml.contains(posOfthisWordInJtable[k]) || eElementWord.getAttribute("POS").equals("MOT")) {
                                                allOk.add(true);
                                                HashMap<String, String> condition = new HashMap<>();
                                                if (eElementWord.getAttribute("Flex").equals("false") || eElementWord.getAttribute("Flex") == null || eElementWord.getAttribute("Flex").equals("")) {
                                                    // oan flex false
                                                    returns = returns + words[k];
                                                } else {
                                                    if (eElementWord.getAttribute("Flex") != null) {
                                                        condition.put("Flex", eElementWord.getAttribute("Flex"));
                                                    }
                                                    if (eElementWord.getAttribute("Anim") != null) {
                                                        condition.put("Anim", eElementWord.getAttribute("Anim"));
                                                    }
                                                    if (eElementWord.getAttribute("Case") != null) {
                                                        condition.put("Case", eElementWord.getAttribute("Case"));
                                                    }
                                                    if (eElementWord.getAttribute("Num") != null) {
                                                        condition.put("Num", eElementWord.getAttribute("Num"));
                                                    }
                                                    if (eElementWord.getAttribute("Nb") != null) {
                                                        condition.put("Nb", eElementWord.getAttribute("Nb"));
                                                    }
                                                    if (eElementWord.getAttribute("Gen") != null) {
                                                        condition.put("Gen", eElementWord.getAttribute("Gen"));
                                                    }
                                                    if (eElementWord.getAttribute("Det") != null) {
                                                        condition.put("Det", eElementWord.getAttribute("Det"));
                                                    }
                                                    if (eElementWord.getAttribute("Degree") != null) {
                                                        condition.put("Degree", eElementWord.getAttribute("Degree"));
                                                    }
                                                    String flex = getFlex(words[k], posOfthisWordInJtable[k], condition);
                                                    returns = returns + words[k] + flex;
                                                }
                                            } else {
                                                if (pos.equals("!SDIC") && !indic) {
                                                    allOk.add(true);
                                                } else {
                                                    allOk.add(false);
                                                }
                                                returns = returns + words[k];
                                            }
                                            returns = returns + separator;
                                        }

                                    }
                                }

                            }

                            returns = !allOk.contains(false) ? returns : "";

                            returns = getGenRule(eElementRule, posOfthisWordInJtable, returns, words, separator);
                            if (!returns.equals("")) {
                                ret.add(returns.substring(0, returns.length() - 1) + "," + flx + "," + ruleNo + ",GenRule,0," + sinSem);
                            }
                            returns = "";

                            ///// This if for special Rule
                            NodeList ndRuleSpecCond = eElementRule.getElementsByTagName("RuleSpecCond");
                            
                            for (int j = 0; j < ndRuleSpecCond.getLength(); j++) {
                                String idSpecRule="";
                                allOk.clear();
                                Node nNodeRuleGenCond = ndRuleSpecCond.item(j);
                                if (nNodeRuleGenCond.getNodeType() == Node.ELEMENT_NODE) {
                                    Element eElementRuleSpecCond = (Element) nNodeRuleGenCond;
                                    idSpecRule = eElementRuleSpecCond.getAttribute("ID");
                                    NodeList ndWord = eElementRuleSpecCond.getElementsByTagName("Word");
                                    for (int k = 0; k < ndWord.getLength(); k++) {
                                        Node nNodeWord = ndWord.item(k);
                                        if (nNodeWord.getNodeType() == Node.ELEMENT_NODE) {
                                            Element eElementWord = (Element) nNodeWord;
                                            String Flex  = attribut.get(eElementWord.getAttribute("ID")).get("Flex" );
                                            String pos  = eElementWord.getAttribute("POS");
                                            String posGen = attribut.get(eElementWord.getAttribute("ID")).get("POS" );
                                            if(eElementWord.getAttribute("Flex").equals(""))eElementWord.setAttribute("Flex", attribut.get(eElementWord.getAttribute("ID")).get("Flex" ));
                                            if(eElementWord.getAttribute("POS").equals(""))eElementWord.setAttribute("POS", attribut.get(eElementWord.getAttribute("ID")).get("POS" ));
                                            if(eElementWord.getAttribute("Anim").equals(""))eElementWord.setAttribute("Anim", attribut.get(eElementWord.getAttribute("ID")).get("Anim" ));
                                            if(eElementWord.getAttribute("Case").equals(""))eElementWord.setAttribute("Case", attribut.get(eElementWord.getAttribute("ID")).get("Case" ));
                                            if(eElementWord.getAttribute("Num").equals(""))eElementWord.setAttribute("Num", attribut.get(eElementWord.getAttribute("ID")).get("Num" ));
                                            if(eElementWord.getAttribute("Gen").equals(""))eElementWord.setAttribute("Gen", attribut.get(eElementWord.getAttribute("ID")).get("Gen" ));
                                            if(eElementWord.getAttribute("Det").equals(""))eElementWord.setAttribute("Det", attribut.get(eElementWord.getAttribute("ID")).get("Det" ));
                                            if(eElementWord.getAttribute("Degree").equals(""))eElementWord.setAttribute("Degree", attribut.get(eElementWord.getAttribute("ID")).get("Degree" ));
                                            boolean indic=lemaExistsInTable(jTableRule,words[k]);
                                            String[] posXML = eElementWord.getAttribute("POS").split(",").length>0?eElementWord.getAttribute("POS").split(","):new String[]{eElementWord.getAttribute("POS")};
                                            List<String> posXml=Arrays.asList(posXML);
                                            if (!eElementWord.getAttribute("POS").equals("!SDIC")
                                                    &&indic&&posXml.contains(posOfthisWordInJtable[k]) 
                                                    || eElementWord.getAttribute("POS").equals("MOT")) {
                                                allOk.add(true);
                                                HashMap<String,String> condition = new HashMap<>();
                                                if (eElementWord.getAttribute("Flex").equals("false") || eElementWord.getAttribute("Flex") == null || eElementWord.getAttribute("Flex").equals("")) {
                                                    returns = returns + words[k];
                                                } else {
                                                    if(eElementWord.getAttribute("Flex") != null)condition.put("Flex", eElementWord.getAttribute("Flex"));
                                                    if(eElementWord.getAttribute("Anim") != null)condition.put("Anim", eElementWord.getAttribute("Anim"));
                                                    if(eElementWord.getAttribute("Case") != null)condition.put("Case", eElementWord.getAttribute("Case"));
                                                    if(eElementWord.getAttribute("Num") != null)condition.put("Num", eElementWord.getAttribute("Num"));
                                                    if(eElementWord.getAttribute("Gen") != null)condition.put("Gen", eElementWord.getAttribute("Gen"));
                                                    if(eElementWord.getAttribute("Det") != null)condition.put("Det", eElementWord.getAttribute("Det"));
                                                    if(eElementWord.getAttribute("Degree") != null)condition.put("Degree", eElementWord.getAttribute("Degree"));
                                                    String flex = getFlex(words[k], eElementWord.getAttribute("POS"),condition);
                                                    returns = returns + words[k] + flex;
                                                }
                                            } else {
                                                if(eElementWord.getAttribute("POS").equals("!SDIC")&&!indic)allOk.add(true);
                                                else allOk.add(false);
                                                returns = returns + words[k];
                                            }
                                            returns = returns + separator;
                                        }
                                        
                                    }
                                }
                                returns = !allOk.contains(false)?returns:"";
                                if(!returns.equals(""))ret.add(returns.substring(0, returns.length() - 1) + "," + flx + "," + ruleNo+",SpecRule,"+idSpecRule+","+sinSem);
                                returns = "";

                            }
                            //

                            //returns = getGenRule(eElementRule, posOfthisWordInJtable, returns, words, separator);
                        }
                    }
                }
            }
        }
        return ret;
    }

    public NodeList getNodeList() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(pathFileXml);
        doc.getDocumentElement().normalize();
        return doc.getElementsByTagName("Rules");
    }

    private String getGenRule(Element eElementRule, String[] POSwords, String returns, String[] words, char separator) {
        NodeList ndRuleGenCond = eElementRule.getElementsByTagName("RuleGenCond");
        boolean allOk = false;
        String pos = "";
        for (int j = 0; j < ndRuleGenCond.getLength(); j++) {
            Node nNodeRuleGenCond = ndRuleGenCond.item(j);
            if (nNodeRuleGenCond.getNodeType() == Node.ELEMENT_NODE) {
                Element eElementRuleGenCond = (Element) nNodeRuleGenCond;
                NodeList ndWord = eElementRuleGenCond.getElementsByTagName("Word");
                for (int k = 0; k < ndWord.getLength(); k++) {
                    Node nNodeWord = ndWord.item(k);
                    if (nNodeWord.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElementWord = (Element) nNodeWord;
                        if (lemaExistsInTable(jTableRule, words[k])) {
                            if (eElementWord.getAttribute("POS").equals(POSwords[k]) || eElementWord.getAttribute("POS").equals("MOT")) {
                                allOk = true;
                                if (eElementWord.getAttribute("Flex").equals("false") || eElementWord.getAttribute("Flex") == null || eElementWord.getAttribute("Flex").equals("")) {
                                    returns = returns + words[k];
                                } else {
                                    HashMap<String, String> condition = new HashMap<>();
                                    if (eElementWord.getAttribute("Anim") != null) {
                                        condition.put("Anim", eElementWord.getAttribute("Anim"));
                                    }
                                    if (eElementWord.getAttribute("Case") != null) {
                                        condition.put("Case", eElementWord.getAttribute("Case"));
                                    }
                                    if (eElementWord.getAttribute("Num") != null) {
                                        condition.put("Num", eElementWord.getAttribute("Num"));
                                    }
                                    if (eElementWord.getAttribute("Gen") != null) {
                                        condition.put("Gen", eElementWord.getAttribute("Gen"));
                                    }
                                    if (eElementWord.getAttribute("Det") != null) {
                                        condition.put("Det", eElementWord.getAttribute("Det"));
                                    }
                                    if (eElementWord.getAttribute("Degree") != null) {
                                        condition.put("Degree", eElementWord.getAttribute("Degree"));
                                    }
                                    String flex = getFlex(words[k], eElementWord.getAttribute("POS"), condition);
                                    returns = returns + words[k] + flex;
                                }
                            } else {
                                allOk = false;
                                returns = returns + words[k];
                            }
                        } // si lema n'est pas dans dic
                        else {
                            // general rule non satisfaisant, regarder special cond
                            NodeList ndRuleSpecCond = eElementRule.getElementsByTagName("RuleSpecCond");
                            for (int m = 0; m < ndRuleSpecCond.getLength(); m++) {
                                Node nNodeRuleSpecCond = ndRuleSpecCond.item(m);
                                if (nNodeRuleSpecCond.getNodeType() == Node.ELEMENT_NODE) {
                                    Element eElementRuleSpecCond = (Element) nNodeRuleSpecCond;
                                    NodeList ndWordRuleSpecCond = eElementRuleSpecCond.getElementsByTagName("Word");
                                    for (int n = 0; n < ndWordRuleSpecCond.getLength(); n++) {
                                        Node nNodeWordRuleSpecCond = ndWordRuleSpecCond.item(n);
                                        if (nNodeWordRuleSpecCond.getNodeType() == Node.ELEMENT_NODE) {
                                            Element eElementWordRuleSpecCond = (Element) nNodeWordRuleSpecCond;
                                            if (eElementWordRuleSpecCond.getAttribute("POS").equals("!SDIC")) {
                                                returns = returns + words[k] + "(!dic )";
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                    returns = returns + separator;
                }
            }
        }

        return allOk ? returns : "";
    }

    //cherche si ce lema est dans le tableau

    public boolean lemaExistsInTable(JTable table, String lema) {
        try {
            // Get row and column count
            int rowCount = table.getRowCount();

            // Get Current Table Entry
            String curEntry = lema;

            // Check against all entries
            for (int i = 0; i < rowCount; i++) {
                String rowEntry = "";
                rowEntry = table.getValueAt(i, 1).toString();
                
                if (rowEntry.equalsIgnoreCase(curEntry)) {
                    return true;
                }
            }
            return false;
        } catch (java.lang.NullPointerException e) {
            return false;
        }
    }

    private String getFlex(String words, String poss, HashMap<String, String> condition) {
        String flexion = "";
        List<String> gramCats = new ArrayList<>();
        String lema = "";
        String flex = "";
        for (int k = 0; k < jTableRule.getRowCount(); k++) {
            if (jTableRule.getValueAt(k, 1) != null) {
                if (!((String) jTableRule.getValueAt(k, 5)).equals(words)) {
                    flexion = "";
                } else {
                    if (words.equals((String) jTableRule.getValueAt(k, 5)) && (poss.equals((String) jTableRule.getValueAt(k, 4)) || poss.equals("MOT"))) {//get lemma in Table
                        lema = (String) jTableRule.getValueAt(k, 1);
                        flex = (String) jTableRule.getValueAt(k, 2);
                        String gramcat = (String) jTableRule.getValueAt(k, 3);
                        gramCats.add(gramcat);

                    }
                }
            }
        }
        String GramCatret = "";
        for (String s : gramCats) {
            if (condition.containsKey("Anim")) {
                if (!s.contains(condition.get("Anim"))) {
                    continue;
                }
            }
            if (condition.containsKey("Num")) {
                if (!s.contains(condition.get("Num"))) {
                    continue;
                }
            }
            if (condition.containsKey("Gen")) {
                if (!s.contains(condition.get("Gen"))) {
                    continue;
                }
            }
            if (condition.containsKey("Det")) {
                if (!s.contains(condition.get("Det"))) {
                    continue;
                }
            }
            if (condition.containsKey("Degree")) {
                if (!s.contains(condition.get("Degree"))) {
                    continue;
                }
            }
            if (condition.containsKey("Case")) {
                if (!s.contains(condition.get("Case"))) {
                    continue;
                }
            }
            GramCatret = s;
        }
        flexion = lema.equals("") || flex.equals("") || GramCatret.equals("") ? "" : "(" + lema + "." + flex + ":" + GramCatret + ")";
        return flexion;
    }
    private String getFlexForFalse(String words, String poss, HashMap<String, String> condition) {
        String flexion = "";
        List<String> gramCats = new ArrayList<>();
        String lema = "";
        String flex = "";
        for (int k = 0; k < jTableRule.getRowCount(); k++) {
            if (jTableRule.getValueAt(k, 1) != null) {
                if (!((String) jTableRule.getValueAt(k, 5)).equals(words)) {
                    flexion = "";
                } else {
                    if (words.equals((String) jTableRule.getValueAt(k, 5)) && (poss.equals((String) jTableRule.getValueAt(k, 4)) || poss.equals("MOT"))) {//get lemma in Table
                        lema = (String) jTableRule.getValueAt(k, 1);
                        flex = (String) jTableRule.getValueAt(k, 2);
                        String gramcat = (String) jTableRule.getValueAt(k, 3);
                        gramCats.add(gramcat);

                    }
                }
            }
        }
        String GramCatret = "";
        for (String s : gramCats) {
            if (condition.containsKey("Anim")) {
                if (!s.contains(condition.get("Anim"))) {
                    continue;
                }
            }
            if (condition.containsKey("Num")) {
                if (!s.contains(condition.get("Num"))) {
                    continue;
                }
            }
            if (condition.containsKey("Gen")) {
                if (!s.contains(condition.get("Gen"))) {
                    continue;
                }
            }
            if (condition.containsKey("Det")) {
                if (!s.contains(condition.get("Det"))) {
                    continue;
                }
            }
            if (condition.containsKey("Degree")) {
                if (!s.contains(condition.get("Degree"))) {
                    continue;
                }
            }
            if (condition.containsKey("Case")) {
                if (!s.contains(condition.get("Case"))) {
                    continue;
                }
            }
            GramCatret = s;
        }
        for (int k = 0; k < jTableRule.getRowCount(); k++) {
            if (jTableRule.getValueAt(k, 1) != null) {
                if (((String) jTableRule.getValueAt(k, 5)).equals(words)&&((String) jTableRule.getValueAt(k, 3)).equals(GramCatret)) {
                    return (String) jTableRule.getValueAt(k, 1);
                } 
            }
        }
        return words;
    }
}
