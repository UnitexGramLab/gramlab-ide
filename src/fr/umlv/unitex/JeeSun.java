package fr.umlv.unitex;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import fr.umlv.unitex.io.GraphIO;

public class JeeSun {

    
    
    static void convert(File f,File f2) {
        System.err.println(f.getAbsolutePath());
        GraphIO g=GraphIO.loadGraph(f);
        boolean modification;
        do {
            modification=false;
            for (int i=0;i<g.nBoxes;i++) {
                GraphBox b=(GraphBox) g.boxes.get(i);
                if (b.lines.size()>1) {
                    /* We have a box to process */
                    if (b.lines.size()>2) {
                        System.err.println("3 splits in file "+f.getPath());
                        break;
                    }
                    splitBox(g,i);
                    modification=true;
                    break;
                }
            }
        } while (modification);
        g.saveGraph(f2);
    }
    
    
    @SuppressWarnings("unchecked")
    private static void splitBox(GraphIO g, int i) {
        GraphBox b=(GraphBox) g.boxes.get(i);
        
        GraphBox newBox=new GraphBox(b.x,b.Y+50,b.type,(GraphicalZone)b.parentGraphicalZone);
        newBox.content=b.content;
        b.content=processContent1(b.content);
        newBox.content=processContent2(newBox.content);
        newBox.lines.add(b.lines.get(1));
        b.lines.remove(1);
        newBox.transitions=(ArrayList<GenericGraphBox>) b.transitions.clone();
        for (int j=0;j<g.nBoxes;j++) {
            GraphBox tmp=(GraphBox) g.boxes.get(j);
            if (tmp.transitions.contains(b)) {
                tmp.transitions.add(newBox);
            }
        }
        g.boxes.add(newBox);
    }


    
    private static String processContent1(String content) {
        int pos_first_plus=content.indexOf('+');
        int pos_accolade=content.indexOf('{');
        int pos_second_plus=content.indexOf('+',pos_accolade);
        int pos_virgule=content.indexOf(',',pos_second_plus);
        /*System.err.println("** "+content+" **");
        System.err.println("+  "+pos_first_plus);
        System.err.println("{  "+pos_accolade);
        System.err.println("+#2 "+pos_second_plus);
        System.err.println(",  "+pos_virgule);*/
        return content.substring(0,pos_first_plus)+"/"+content.substring(pos_accolade,pos_second_plus)
               +content.substring(pos_virgule);
    }

    private static String processContent2(String content) {
        int pos_first_plus=content.indexOf('+');
        int pos_accolade=content.indexOf('{');
        int pos_second_plus=content.indexOf('+',pos_accolade);
        return content.substring(pos_first_plus+1,pos_accolade+1)+content.substring(pos_second_plus+1);
    }


    static void convertDir(File d,File d2,boolean renameJO) {
        File[] files=d.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".grf");
            }
        });
        
        for (File f:files) {
            String newName=f.getName();
            if (renameJO && newName.startsWith("JO")) {
                if (newName.equals("JO10.grf")) {
                    newName="JN10.grf";
                }
                else if (newName.equals("JO20.grf")) {
                    newName="JN20.grf";
                }
                else if (newName.equals("JO.grf")) {
                    newName="JN.grf";
                } else {
                    newName="JN0"+newName.substring(2);
                }
            }
            File f2=new File(d2,newName);
            convert(f,f2);
        }
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        /*File d1=new File("D:\\KoreanB\\EA\\DEC6_2_2.grf");
        File d2=new File("D:\\KoreanC\\EA\\DEC6_2_2.grf");
        convert(d1,d2);*/
        
        /*File d1=new File("D:\\KoreanB\\EA");
        File d2=new File("D:\\KoreanC\\EA");
        convertDir(d1,d2,false);*/

        /*File d3=new File("D:\\KoreanB\\EV");
        File d4=new File("D:\\KoreanC\\EV");
        convertDir(d3,d4,false);*/

        /*File d5=new File("D:\\KoreanB\\JD");
        File d6=new File("D:\\KoreanC\\JD");
        convertDir(d5,d6,false);*/
        
        File d7=new File("D:\\KoreanB\\JN");
        File d8=new File("D:\\KoreanC\\JN");
        convertDir(d7,d8,true);

    }

}
