import org.apache.commons.io.FileUtils;
import sun.nio.ch.IOUtil;

//import javax.swing.text.Document;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

public class Process_Data {

    public static void main(String[] args) {
        File currentDir = new File("Coverage Test2"); // current directory
        try {
            makeInputFile(displayDirectoryContents(currentDir));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static boolean checkList(List<String> names, String answer)
    {
        for(String str: names)
        {
            if(str.equalsIgnoreCase(answer))
            {
                return false;
            }
        }
        return true;
    }

    public static List<Map.Entry<String,String>> displayDirectoryContents(File dir) {
        List<Map.Entry<String,String>> linePerTest= new java.util.ArrayList<>();
        List<String> functions = new java.util.ArrayList();
        try {
            int counter = 0;
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    //System.out.println("directory name:" + file.getName());
                    //if(file.getName().contains("io.") || file.getName().contains("classes")) {
                        linePerTest.addAll(displayDirectoryContents(file));
                    //}
                } else if(file.getParentFile().getName().equalsIgnoreCase(".classes")){
                    //System.out.println("     file:" + file.getName());

                    /*try {
                        String tmpPath = file.getPath();
                        tmpPath.replaceAll("\\.classes", "classes");
                        FileUtils.copyFile(file, new File("Parsable\\" + tmpPath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    if(checkList(functions, file.getName()))
                    {
                        functions.add(file.getName());
                    }
                    String[] filepath = file.getPath().split("\\\\");
                    String testName = filepath[(filepath.length - 4)];

                    String output = new Scanner(file).useDelimiter("\\Z").next();
                    Document doc = Jsoup.parse(output, "UTF-8");
                    Elements links = doc.getElementsByClass("fc");
                    for (Element link : links) {
                        Element linkText = link.select("i").first();
                        String tmp = linkText.text();
                        int num = Integer.parseInt(tmp);
                        int functionNumber = functions.indexOf(file.getName());
                        String lineNum = functionNumber + "-" + num;
                        //Map.Entry<String, String> newEntry = new java.util.AbstractMap.SimpleEntry<>(testName, lineNum);
                        linePerTest.add(new java.util.AbstractMap.SimpleEntry<>(testName, lineNum));
                        //System.out.println(linkText);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
       }
       return linePerTest;
    }

    public static void printMap(List<Map.Entry<String,String>> lineToTest)
    {

        for(Iterator<Map.Entry<String,String>> itr = lineToTest.iterator(); itr.hasNext();)
        {
            Map.Entry<String, String> entry=(Map.Entry<String, String>) itr.next();
            System.out.println("TestName: "+entry.getKey()+" LineNumber: "+entry.getValue());
        }
    }

    public static void makeInputFile(List<Map.Entry<String,String>> lineToTest) throws IOException
    {

        PrintStream file = new PrintStream("ParsedInput.txt");


        for(Iterator<Map.Entry<String,String>> itr = lineToTest.iterator(); itr.hasNext();)
        {
            Map.Entry<String, String> entry=(Map.Entry<String, String>) itr.next();
            file.println("TestName:"+entry.getKey()+": LineNumber:" + entry.getValue());
        }
        file.close();
    }
}
