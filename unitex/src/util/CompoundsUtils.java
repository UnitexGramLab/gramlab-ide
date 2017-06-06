/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.HeadlessException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.StaticValue;

/**
 *
 * @author rojo
 */
public class CompoundsUtils {
    public static List<String> getDlfInFile(String value) throws IOException, HeadlessException {
        List<String> result = new ArrayList<>();
        String tempPath = StaticValue.delafTmpPathDelac;
        Utils.generateDelaf(tempPath, value);
        String path = StaticValue.text_sntAbsPath;
        ArrayList<String> readFile = Utils.readFile(path);
        int count = 0;
        for (String s : readFile) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < s.length(); i++) {
                if (count == 0) {
                    i = i++;
                    count++;
                    continue;
                }
                sb.append(s.charAt(i));
                i++;
            }
            if (!sb.toString().isEmpty()) {
                result.add(sb.toString());
            }
        }
        return result;
    }
}
