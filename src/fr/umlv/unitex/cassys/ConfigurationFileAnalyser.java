package fr.umlv.unitex.cassys;

public class ConfigurationFileAnalyser {

    private String fileName;

    private boolean mergeMode;

    private boolean replaceMode;

    private boolean commentFound;


    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return the mergeMode
     */
    public boolean isMergeMode() {
        return mergeMode;
    }

    /**
     * @return the replaceMode
     */
    public boolean isReplaceMode() {
        return replaceMode;
    }

    /**
     * @return the commentFound
     */
    public boolean isCommentFound() {
        return commentFound;
    }


    public ConfigurationFileAnalyser(String line) throws EmptyLineException, InvalidLineException {

        // extract line comment
        String lineSubstring[] = line.split("#", 2);

        if (lineSubstring.length > 1) {
            commentFound = true;
        }

        if (lineSubstring[0] == null || lineSubstring[0].equals("")) {
            throw new EmptyLineException();
        }

        String lineCore[] = lineSubstring[0].split(" ");

        if (!lineCore[0].startsWith("\"") || !lineCore[0].endsWith("\"")) {
            throw new InvalidLineException(lineSubstring[0] + " --> FileName must start and end with quote\n");
        }

        lineCore[0] = lineCore[0].substring(1, lineCore[0].length() - 1);

        if (lineCore.length > 1) {
            fileName = lineCore[0];
            if (lineCore[1].equals("M") || lineCore[1].equals("Merge")
                    || lineCore[1].equals("merge")) {
                mergeMode = true;
                replaceMode = false;
            } else if (lineCore[1].equals("R") || lineCore[1].equals("Replace")
                    || lineCore[1].equals("replace")) {
                mergeMode = false;
                replaceMode = true;
            } else {
                throw new InvalidLineException(lineSubstring[0] + " --> Second argument should be Merge or Replace\n");
            }
        } else {
            throw new InvalidLineException(lineSubstring[0] + " --> FileName should be followed by a white space and Merge or Replace\n");
        }
    }

    public class InvalidLineException extends Exception {
        public InvalidLineException(String s) {
            super(s);
        }

        public InvalidLineException() {
            /* NOP */
        }
    }

    public class EmptyLineException extends Exception {
        public EmptyLineException() {
            /* NOP */
        }
    }

}
