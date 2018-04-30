/*
 * Copyright Metropolitan Transportation Authority NY
 * All Rights Reserved
 */
package org.mtahq.csv.splitter;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mnilsen
 */
public class CSVSplitter {
    private final String outputDirBase = Utils.getAppProperties().get(SplitterAppProperties.DEFAULT_OUTPUT_DIRECTORY_NAME);
    private final long splitTime = Utils.getAppProperties().getLong(SplitterAppProperties.DEFAULT_SPLIT_TIME_SECS);
    private final String dateFormatPattern = Utils.getAppProperties().get(SplitterAppProperties.DEFAULT_TSTAMP_DATE_FORMAT);
    private final int firstDataLine = Utils.getAppProperties().getInt(SplitterAppProperties.DEFAULT_FIRST_DATA_LINE);
    private final int timestampColIndex = Utils.getAppProperties().getInt(SplitterAppProperties.DEFAULT_TSTAMP_COLUMN_IDX);
    
    private final SimpleDateFormat tsFormat = new SimpleDateFormat(dateFormatPattern);
    
    public CSVSplitter() {
    }
    
    public static void main(String[] args) {
        Utils.config(".");
        if(args.length == 0) {
            System.out.println("Please specify an input file name");
            System.exit(0);
        }
        CSVSplitter cs = new CSVSplitter();
        cs.split(args[0], 16);
        
    }
    
    public void split(String filename, int firstLine) {
        File f = new File(filename);
        String baseFilename = f.getName().substring(0,f.getName().length() - 4);
        if(!f.exists()) {
            Log.getLog().warning(String.format("File %s does not exist", filename));
            System.exit(1);
        }
        Log.getLog().info(String.format("Processing file %s", filename));
        List<String[]> linesToWrite = new ArrayList<>();
        Date firstTime = null;
        try {
            File outDir = this.getOutputDirectory();
            CSVReader reader = new CSVReader(new FileReader(f),';');
            int count = 1;
            int fileCount = 1;
            String[] line = reader.readNext();
            while(line != null) {
                if(count < this.firstDataLine) {
                    line = reader.readNext();
                    count++;
                    continue;
                }
                if(firstTime == null) {
                    firstTime = this.getTimestampForLine(line);
                } else {
                    Date linetime = this.getTimestampForLine(line);
                    if(linetime == null) {
                        line = reader.readNext();
                        count++;
                        continue;
                    }
                    long diff = TimeUnit.SECONDS.convert(linetime.getTime() - firstTime.getTime(), TimeUnit.MILLISECONDS);
                    if(diff > this.splitTime) {
                        this.writeLinesToFile(linesToWrite, fileCount, baseFilename,outDir);
                        Log.getActivityLog().info(String.format("Splitting to file %s for line index %s", fileCount,count));
                        fileCount++;
                        firstTime = null;
                        linesToWrite.clear();
                    }
                    linesToWrite.add(line);
                }
                line = reader.readNext();
                count++;
            }
            
        } catch (IOException ex) {
            Log.getLog().log(Level.SEVERE, "An IO error occurred", ex);
        } 
    }
    
    private Date getTimestampForLine(String[] line) {
        Date d = null;
        String ts = line[this.timestampColIndex];
        try {
            d = this.tsFormat.parse(ts);
        } catch (ParseException ex) {
            Log.getLog().log(Level.SEVERE, "Could not parse timestamp: " + ts, ex);
        }
        return d;
    }
    
    private void writeLinesToFile(List<String[]> lines,int fileIndex, String baseFileName,File outDir) throws IOException {
        File f = new File(outDir,String.format("%s_%s.csv", baseFileName,fileIndex));
        CSVWriter writer = new CSVWriter(new FileWriter(f));
        writer.writeAll(lines);
        writer.close();
    }
    
    private File getOutputDirectory() throws IOException {        
        File base = new File(".");
        String baseName = this.outputDirBase;
        File dir = new File(base,baseName);
        int idx = 0;
        while(dir.exists()) {
            idx++;
            String name = String.format("%s_%s",baseName,idx );
            dir = new File(name);
        }
        Log.getActivityLog().info(String.format("Creating output directory %s", dir.getCanonicalPath()));
        dir.mkdir();
        return dir;
    }
}
