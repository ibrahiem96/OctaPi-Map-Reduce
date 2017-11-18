import org.apache.hadoop.mapred.lib.MultipleInputs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.mapreduce.Job;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.conf.Configuration;

//import javax.security.auth.login.Configuration;


public class Coverage {

    static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
    {
        public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
            try {
                        //System.out.println("     file:" + file.getName());
                        String outputFile = value.toString();
                        String[] splits = outputFile.split(":");
                        Text name = new Text(splits[3]);
                        Text line = new Text(splits[1]);
                        output.collect(name, line);



            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text>
    {
        public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
        {
            String tests = "";
            while(values.hasNext())
            {

                tests = tests + " " + values.next().toString();
            }
            output.collect(key, new Text(tests));
        }
    }

    public static void addInputFiles(JobConf job, File dir) {
        try {
            File[] files = dir.listFiles();
            for (File file : files) {
                System.out.println(file.getName());
                if (file.isDirectory()) {
                    //System.out.println("directory name:" + file.getName());
                    addInputFiles(job, file);
                } else if (file.getParentFile().getName().equalsIgnoreCase("classes")) {
                    System.out.println("name: " + file.getName());
                    MultipleInputs.addInputPath(job, new Path(file.getPath()), TextInputFormat.class, Map.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        JobConf job = new JobConf(Coverage.class);
        //Configuration conf = new Configuration();
        //Job job = new Job(conf, "coverage");
        //job.setJarByClass(Coverage.class);

        job.setJobName("coverageResult");
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setMapperClass(Map.class);

        //File dir = new File(args[0]);
        //addInputFiles(job, dir);

        job.setReducerClass(Reduce.class);
        job.setJarByClass(Coverage.class);
        job.setInputFormat(TextInputFormat.class);
        job.setOutputFormat(TextOutputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        JobClient.runJob(job);



    }
}
