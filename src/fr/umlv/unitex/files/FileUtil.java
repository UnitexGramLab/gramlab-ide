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

package fr.umlv.unitex.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;

/**
 * This class provides methods to get information about files, like path, file
 * name or extension.
 *
 * @author Sébastien Paumier
 */
public class FileUtil {

    /**
     * Returns the name without extension of a file.
     *
     * @param s the file name
     * @return the name without extension
     */
    public static String getFileNameWithoutExtension(String s) {
        int n = s.lastIndexOf('.');
        if ((n == -1) || (n < s.lastIndexOf(File.separatorChar)))
            return s;
        return s.substring(0, n);
    }

    /**
     * Returns the name without extension of a file.
     *
     * @param f the file name
     * @return the name without extension
     */
    public static String getFileNameWithoutExtension(File f) {
        return getFileNameWithoutExtension(f.getAbsolutePath());
    }

    /**
     * Returns the extension of a file.
     *
     * @param s the file name
     * @return the extension if there is one, the empty string otherwise
     */
    private static String getFileNameExtension(String s) {
        int n = s.lastIndexOf('.');
        if ((n == -1) || (n < s.lastIndexOf(File.separatorChar)))
            return "";
        return s.substring(n + 1);
    }

    /**
     * Returns the extension of a file. This method is equivalent to a call to
     * <code>Util.getFileNameExtension(f.getName())</code>.
     *
     * @param f the file
     * @return the extension if there is one, the empty string otherwise
     */
    public static String getFileNameExtension(File f) {
        return getFileNameExtension(f.getName());
    }

    /**
     * Returns the path name of a file, without the file name.
     *
     * @param s the file name
     * @return the path
     */
    public static String getFilePathWithoutFileName(String s) {
        int n = s.lastIndexOf(File.separatorChar);
        if (n == -1)
            return "";
        return s.substring(0, n + 1);
    }

    /**
     * Returns the path name of a file, without the file name. This method is
     * equivalent to a call to
     * <code>getFilePathWithoutFileName(f.getName())</code>.
     *
     * @param f the file
     * @return the path
     */
    public static String getFilePathWithoutFileName(File f) {
        return getFilePathWithoutFileName(f.getAbsolutePath());
    }

    /**
     * Returns the file name of a file, without the path.
     *
     * @param f the file
     * @return the file name, without the path
     */
    private static String getFileNameWithoutFilePath(File f) {
        return f.getName();
    }

    /**
     * Returns the file name of a file, without the path. This method is
     * equivalent to a call to
     * <code>getFileNameWithoutFilePath(new File(s))</code>.
     *
     * @param s the file name
     * @return the file name, without the path
     */
    public static String getFileNameWithoutFilePath(String s) {
        return getFileNameWithoutFilePath(new File(s));
    }

    /**
     * Returns the extension in lower case of a file
     *
     * @param s file name
     * @return the extension
     */
    public static String getExtensionInLowerCase(String s) {
        return getExtensionInLowerCase(new File(s));
    }

    /**
     * Returns the extension in lower case of a file
     *
     * @param f the file
     * @return the extension
     */
    public static String getExtensionInLowerCase(File f) {
        String ext = "";
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    /**
     * Takes a file and looks for the text between the tags <code>&lt;title&gt;</code>
     * and <code>&lt;/title&gt;</code>.
     * <p/>
     * WARNING: the result string is taken rawly and
     * might be wrongly encoded if the title is not in ASCII.
     *
     * @param file the HTML file
     * @return the title of the page; <code>null</code> if the pattern is not found in the
     *         first 200 bytes of the file
     */
    public static String getHtmlPageTitle(File file) {
        Pattern p = Pattern.compile("<title>(.*)</title>");
        FileInputStream input;
        byte[] bytes = new byte[200];
        try {
            input = new FileInputStream(file);
            input.read(bytes, 0, 200);
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        String charBuffer = new String(bytes);
        Matcher matcher = p.matcher(charBuffer);
        if (matcher.find()) {
            return charBuffer.subSequence(matcher.start(1), matcher.end(1))
                    .toString();
        }
        return null;
    }

    public static void write(String s, File f) {
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            OutputStreamWriter writer=ConfigManager.getManager().getEncoding(null).getOutputStreamWriter(f);
            writer.write(s, 0, s.length());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    /**
     * Creates a description of f relative to parent. For instance:
     * 
     * parent=/tmp/grf/toto.grf
     * file=/tmp/grf/sub/foo.grf
     * => sub/foo.grf
     * 
     * parent=/tmp/grf/toto.grf
     * file=/dst/foo.grf
     * => ../../dst/foo.grf
     */
	public static String getRelativePath(File parent,File f) {
		String s=isAncestor(parent.getParentFile(),f.getParentFile());
		if (s!=null) {
			/* Case 1: f is in the same dir or a child dir */
			return s+f.getName();
		}
		/* Case 2: we have to find the common dir ancestor */
		File ancestor=commonAncestor(parent.getParentFile(),f.getParentFile());
		if (ancestor==null) {
			/* If files are on different drives under Windows,
			 * we have no choice: we must use f's absolute path */
			return f.getAbsolutePath();
		}
		s=f.getName();
		f=f.getParentFile();
		while (!f.equals(ancestor)) {
			s=f.getName()+File.separator+s;
			f=f.getParentFile();
		}
		int n=dirsUpToDir(ancestor,parent);
		for (int i=0;i<n;i++) s=".."+File.separator+s;
		return s;
	}


	/**
	 * Returns the common directory ancestor, or null if there is none 
	 * because the files are on different drives under Windows.
	 */
	private static File commonAncestor(File dir1, File dir2) {
		int n1=dirsUpToRoot(dir1);
		int n2=dirsUpToRoot(dir2);
		if (n1<n2) {
			dir2=moveUp(n2-n1,dir2);
		} else if (n1>n2) {
			dir1=moveUp(n1-n2,dir1);
		}
		/* At this point, we have dir1 and dir2 at the same depth from the root */
		while (n1>=0) {
			if (dir1.equals(dir2)) {
				return dir1;
			}
			n1--;
			dir1=dir1.getParentFile();
			dir2=dir2.getParentFile();
		}
		/* If we get there, we are in a Windows case like dir1=C:\ and dir2=D:\ */
		return null;
	}

	/**
	 * Performs n 'cd ..' on the given directory
	 */
	private static File moveUp(int n,File dir) {
		while (n!=0) {
			n--;
			dir=dir.getParentFile();
		}
		return dir;
	}

	/**
	 * Returns the number of directories there are to cross up before reaching
	 * the root:
	 * 
	 * /tmp/sub/ => 2
	 */
	private static int dirsUpToRoot(File dir) {
		int n=0;
		while (!isRoot(dir)) {
			n++;
			dir=dir.getParentFile();
		}
		return n;
	}

	/**
	 * Returns the number of directories there are to cross up before reaching
	 * the parent directory:
	 * 
	 * /tmp/sub/
	 * /tmp/sub/titi/tata/toto/ => 2
	 */
	private static int dirsUpToDir(File parent,File dir) {
		int n=0;
		while (!dir.equals(parent)) {
			n++;
			dir=dir.getParentFile();
		}
		return n;
	}

	
	private static boolean isRoot(File dir) {
		for (File f:File.listRoots()) {
			if (f.equals(dir)) return true;
		}
		return false;
	}

	/**
	 * Return a non null relative path if dir1 is equals to dir2 or is an ancestor of dir2
	 */
	private static String isAncestor(File dir1,File dir2) {
		String s="";
		while (dir2!=null && !dir2.equals(dir1)) {
			s=dir2.getName()+File.separator+s;
			dir2=dir2.getParentFile();
		}
		if (dir2!=null) return s;
		return null;
	}


    /**
     * Tries to find the language directory corresponding to the given file.
     *
     * @param f
     * @return the language directory, or null if no language directory was
     *         found in the path of f
     */
    public static File getLanguageDirForFile(File f) {
        if (f == null) return null;
        File parent = f.getParentFile();
        while (true) {
            if (parent == null) return null;
            if (parent.equals(Config.getUserDir()) ||
                    parent.equals(Config.getUnitexDir())) break;
            f = parent;
            parent = parent.getParentFile();
        }
        if (!ConfigManager.getManager().isValidLanguageName(f.getName())) return null;
        return f;
    }

    /**
     * Copies files. The source is specified by a file name that can contain *
     * and ? jokers.
     *
     * @param src  source
     * @param dest destination
     */
    public static void copyFileByName(File src, File dest) {
        final File path_src = src.getParentFile();
        final String expression = src.getName();
        if (dest.isDirectory()) {
            File files_list[] = path_src
                    .listFiles(new RegFileFilter(expression));
            if (files_list != null) {
                for (File F : files_list) {
                    if (!F.isDirectory()) {
                        copyFile(F, new File(dest, F.getName()));
                    }
                }
            }
        } else
            copyFile(src, dest);
    }

    /**
     * Copies a directory recursively.
     *
     * @param src  source directory
     * @param dest destination directory
     */

    public static void copyDirRec(File src, File dest) {

        if (!src.isDirectory()) {
            return;
        }
        if (!dest.exists()) {
            dest.mkdirs();
        }
        File files_list[] = src.listFiles();
        if (files_list == null) {
            return;
        }
        for (File f : files_list) {
            if (f.isDirectory()) {
                copyDirRec(f, new File(dest, f.getName()));
            } else if (f.isFile()) {
                copyFile(f, new File(dest, f.getName()));
            }
        }
    }

    /**
     * Deletes files. The source is specified by a file name that can contain *
     * and ? jokers.
     *
     * @param src source
     */
    public static void deleteFileByName(File src) {
    	if (src==null) return;
        final File path_src = src.getParentFile();
        if (path_src==null) return;
        final String expression = src.getName();
        File files_list[] = path_src.listFiles(new RegFileFilter(
                expression));
        if (files_list != null) {
            for (File aFiles_list : files_list) {
            	aFiles_list.delete();
            }
        }
    }

    /**
     * Deletes files. The source is specified by a file name that can contains *
     * and ? jokers. This method differs from deleteFileByName because it cannot
     * delete directories.
     *
     * @param src source
     */
    public static void removeFile(File src) {
    	if (src==null) return;
        final File path_src = src.getParentFile();
        if (path_src==null) return;
        final String expression = src.getName();
        final File files_list[] = path_src.listFiles(new RegFileFilter(
                expression));
        for (final File aFiles_list : files_list) {
            final File F;
            if (!(F = aFiles_list).isDirectory()) {
                F.delete();
            }
        }
    }

    /**
     * Copy one file.
     *
     * @param src  source file
     * @param dest destination file
     */
    public static void copyFile(File src, File dest) {
        try {
            final FileInputStream fis = new FileInputStream(src);
            final FileOutputStream fos = new FileOutputStream(dest);
            copyStream(fis, fos);
            fis.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyStream(InputStream fis, OutputStream fos) {
        try {
            final byte[] buf = new byte[2048];
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}