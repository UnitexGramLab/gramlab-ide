/*
 * Unitex
 *
 * Copyright (C) 2001-2009 Universit� Paris-Est Marne-la-Vall�e <unitex@univ-mlv.fr>
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

/**
 * This class represent the bounds of a sequence in the text that goes with
 * a sentence automaton.
 * 
 * @author paumier
 */
public class Bounds {

    /* Offsets in chars relative to the beginning of the sentence */
    private int global_start_in_chars;
    private int global_end_in_chars;
    
    /* Offsets in tokens and positions in chars inside those tokens.
     * 
     * start_in_chars=-1 => start_in_chars=0 
     * end_in_chars=-1   => end_in_chars=last char of the token 
     */ 
    private int start_in_tokens;
    private int start_in_chars;
    private int end_in_tokens;
    private int end_in_chars;
    
    
    public Bounds(int global_start_in_chars,int global_end_in_chars) {
        //System.out.println(global_start_in_chars+" -> "+global_end_in_chars);
        this.global_start_in_chars=global_start_in_chars;
        this.global_end_in_chars=global_end_in_chars;
        global_to_relative();
    }
    
    public Bounds(int start_in_tokens,int start_in_chars,
            int end_in_tokens,int end_in_chars) {
        this.start_in_tokens=start_in_tokens;
        this.start_in_chars=start_in_chars;
        this.end_in_tokens=end_in_tokens;
        this.end_in_chars=end_in_chars;
        relative_to_global();
        //System.out.println("2e constructeur: "+global_start_in_chars+" -> "+global_end_in_chars);
    }
    
    private void global_to_relative() {
        start_in_tokens=0;
        int current_length=0;
        while (current_length+TokensInfo.getTokenLength(start_in_tokens)<=global_start_in_chars) {
            current_length=current_length+TokensInfo.getTokenLength(start_in_tokens);
            start_in_tokens++;
        }
        if (global_start_in_chars!=current_length) {
            start_in_chars=global_start_in_chars-current_length;
        } else {
            start_in_chars=-1;
        }
        
        end_in_tokens=start_in_tokens;
        while (current_length+TokensInfo.getTokenLength(end_in_tokens)<=global_end_in_chars) {
            current_length=current_length+TokensInfo.getTokenLength(end_in_tokens);
            end_in_tokens++;
        }
        if (global_end_in_chars!=current_length) {
            end_in_chars=global_end_in_chars-current_length;
        } else {
            end_in_chars=-1;
        }
    }

    private void relative_to_global() {
        global_start_in_chars=0;
        for (int i=0;i<start_in_tokens;i++) {
            global_start_in_chars=global_start_in_chars+TokensInfo.getTokenLength(i);
        }
        if (start_in_chars!=-1) {
            global_start_in_chars=global_start_in_chars+start_in_chars;
        }
        global_end_in_chars=0;
        int last=end_in_tokens;
        if (end_in_chars==-1) {
            last++;
        }
        for (int i=0;i<last;i++) {
            global_end_in_chars=global_end_in_chars+TokensInfo.getTokenLength(i);
        }
        if (end_in_chars!=-1) {
            global_end_in_chars=global_end_in_chars+end_in_chars;
        } else {
            /* We want to set the ending position on the last char, not after it */
            global_end_in_chars--;
        }
    }

    public int getGlobal_start_in_chars() {
        return global_start_in_chars;
    }

    public int getGlobal_end_in_chars() {
        return global_end_in_chars;
    }

    public int getStart_in_tokens() {
        return start_in_tokens;
    }

    public int getStart_in_chars() {
        return start_in_chars;
    }

    public int getEnd_in_tokens() {
        return end_in_tokens;
    }

    public int getEnd_in_chars() {
        return end_in_chars;
    }
    
    @Override
    public String toString() {
        return start_in_tokens+" "+start_in_chars+" "+end_in_tokens+" "+end_in_chars; 
    }
    
}
