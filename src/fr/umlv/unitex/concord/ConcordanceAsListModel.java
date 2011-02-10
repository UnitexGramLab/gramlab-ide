/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

package fr.umlv.unitex.concord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Arrays;

import javax.swing.AbstractListModel;
import javax.swing.SwingWorker;

import fr.umlv.unitex.text.Interval;


/**
 * This is a model for representing an HTML concordance file as the
 * list of its paragraphs. Paragraphs are delimited by new lines. It uses
 * a mapped file to avoid to store large data in memory.
 *
 * @author Sébastien Paumier
 */
public class ConcordanceAsListModel extends AbstractListModel {

    /**
     * An HTML concordance file always starts with a header of
     * HTML_START_LINES lines, then there are the real concordance lines, then
     * there are HTML_END_LINES that close open HTML tags.
     */
    int HTML_START_LINES = 7;
    private int HTML_END_LINES = 2;
    private int HTML_CONTROL_LINES = HTML_START_LINES + HTML_END_LINES;

    MappedByteBuffer buffer;
    private int dataLength;
    private SwingWorker<Void, Integer> worker;
    Interval selection;
    private FileChannel channel;
    private FileInputStream stream;
    private File file;
    static final Charset utf8 = Charset.forName("UTF-8");

    private int[] endOfLines;
    private int numberOfEOL;

    public void load(File f) {
        this.file = f;
        dataLength = (int) file.length();
        endOfLines = new int[0];
        numberOfEOL = 0;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        channel = stream.getChannel();
        try {
            buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, dataLength);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        worker = new SwingWorker<Void, Integer>() {

            @Override
            protected Void doInBackground() throws Exception {
                int lastStart = 0;
                for (int pos = 0; pos < dataLength; pos = pos + 1) {
                    int a = 0xFF & buffer.get(pos);
                    if (a == '\n') {
                        // if we have an end-of-line
                        publish(pos);
                        setProgress(100 * pos / dataLength);
                        lastStart = pos + 1;
                    }
                }
                if (lastStart < (dataLength - 1)) {
                    publish(dataLength - 1);
                    setProgress(100);
                }
                /* We publish a negative position in order to inform the
                     * progress method that there are no more ends of line.
                     */
                publish(-1);
                return null;
            }

            @SuppressWarnings("synthetic-access")
            @Override
            protected void process(java.util.List<Integer> chunks) {
                int oldSize = numberOfEOL;
                int newSize = oldSize + chunks.size();
                int multiplier = 1;
                /* We check if it is necessary to enlarge the EOL
                     * array */
                if (endOfLines.length == 0) {
                    endOfLines = new int[1];
                }
                while (multiplier * endOfLines.length < newSize) {
                    multiplier = 2 * multiplier;
                }
                int[] temp = endOfLines;
                if (multiplier != 1) {
                    temp = Arrays.copyOf(endOfLines, multiplier * endOfLines.length);
                }
                int insertPos = oldSize;
                for (Integer i : chunks) {
                    if (i < 0) {
                        /* We assume that a negative position means the end of
                               * the new lines, and, so, we resize the array. */
                        temp = Arrays.copyOf(temp, insertPos);
                        newSize = insertPos;
                        break;
                    }
                    temp[insertPos++] = i;
                }
                /* If we keep the following instructions in this order,
                     * there is no need to synchronize */
                endOfLines = temp;
                numberOfEOL = newSize;
                fireIntervalAdded(this, oldSize, newSize - 1);
            }

        };
        worker.execute();
    }

    public ConcordanceAsListModel() {
        super();
    }

    ConcordanceAsListModel(int html_start_lines, int html_end_lines) {
        this.HTML_START_LINES = html_start_lines;
        this.HTML_END_LINES = html_end_lines;
        this.HTML_CONTROL_LINES = HTML_START_LINES + HTML_END_LINES;
    }

    public int getSize() {
        int size = numberOfEOL - HTML_CONTROL_LINES;
        if (size < 0) return 0;
        return size;
    }


    /**
     * Returns the text corresponding to the paragraph #i.
     */
    String getElementReallyAt(int i) {
        Interval interval = getInterval(i);
        long start = interval.getStart() + 15; // we don't want neither the <tr><td nowrap>
        long end = interval.getEnd() - 12;     // nor the </td></tr>\r\n
        byte[] tmp = new byte[(int) (end - start + 1)];
        int z = 0;
        for (long pos = start; pos <= end; pos++) {
            tmp[z++] = buffer.get((int) pos);
        }
        return new String(tmp, utf8);
    }

    Interval getInterval(int i) {
        int end = endOfLines[i];
        int start = (i == 0) ? 0 : (endOfLines[i - 1] + 1);
        return new Interval(start, end);
    }

    /**
     * Returns the text corresponding to the concordance line #i.
     */
    public Object getElementAt(int i) {
        int realIndex = i + HTML_START_LINES;
        return getElementReallyAt(realIndex);
    }


    /**
     * Just to ask the view to refresh.
     */
    public void refresh() {
        fireContentsChanged(this, 0, getSize());
    }

    public void reset() {
        if (buffer != null) buffer = null;
        System.gc();
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            channel = null;
        }
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            stream = null;
        }
    }

}
