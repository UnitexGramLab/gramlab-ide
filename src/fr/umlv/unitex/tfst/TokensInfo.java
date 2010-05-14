/*
 * Unitex
 *
 * Copyright (C) 2001-2010 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 *
 */

package fr.umlv.unitex.tfst;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class TokensInfo {

    private static int[] info;
    
    public static int getToken(int n) {
        return info[2*n];
    }

    public static int getTokenLength(int n) {
        return info[2*n+1];
    }
    
    public static void loadTokensInfo(File f) throws FileNotFoundException {
        Scanner scanner=new Scanner(f,"UTF-16");
        ArrayList<Integer> l=new ArrayList<Integer>();
        while (scanner.hasNextInt()) {
            int n=scanner.nextInt();
            if (n<0) {
                throw new AssertionError("Negative token number: "+n);
            }
            l.add(n);
            if (!scanner.hasNextInt()) {
                throw new AssertionError("Invalid token info file");
            }
            n=scanner.nextInt();
            if (n<-1) {
                throw new AssertionError("Invalid token bound: "+n+" ; should be >=-1");
            }
            l.add(n);
        }
        if (scanner.hasNext()) {
            throw new AssertionError("Invalid token info file");
        }
        scanner.close();
        int size=l.size();
        info=new int[size];
        for (int i=0;i<size;i++) {
            info[i]=l.get(i);
        }
    }
}
