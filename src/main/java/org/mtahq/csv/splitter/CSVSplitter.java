/*
 * Copyright Metropolitan Transportation Authority NY
 * All Rights Reserved
 */
package org.mtahq.csv.splitter;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

    private final String[] LABEL_FILE_HEADER = {"filename", "passengers", "bystanders", "operation"};
    private final String[] DATA_FILE_HEADER = {"TimeStamp", "Counter", "PresenceState",
        "MovementIntervalCount", "DetectionCount", "MovementSlowItem",
        "MovementFastItem", "DetectionDistance", "DetectionRadarCrossSection",
        "DetectionVelocity"};

    private final SimpleDateFormat tsFormat = new SimpleDateFormat(dateFormatPattern);
    private FileFilter csvFiles = (File pathname) -> pathname.getName().endsWith(".csv") || pathname.getName().endsWith(".CSV");
    private File inDir;
    private File outDir;
    private File labelFile;
    private CSVWriter labelFileWriter = null;

    public CSVSplitter() {
    }

    public static void main(String[] args) {
        Utils.config(".");
        if (args.length == 0) {
            System.out.println("Please specify 3 args:"
                    + "\n\t 1 - input directory"
                    + "\n\t 2 - output directory name"
                    + "\n\t 3 - label file name");
            System.exit(0);
        }
        CSVSplitter cs = new CSVSplitter();
        cs.processDirectory(args[0], args[1], args[2]);

    }

    public void processDirectory(String inDirName, String outDirName, String labelFileName) {

        this.inDir = new File(inDirName);
        if (!inDir.exists()) {
            System.out.println("The Input Directory does not exist");
            System.exit(0);
        }
        this.outDir = new File(outDirName);
        if (outDir.exists()) {
            if (outDir.isDirectory()) {
                Arrays.stream(outDir.listFiles()).forEach((f) -> f.delete());
            } else {
                System.out.println("The Output file is not a directory");
                System.exit(0);
            }
        } else {
            outDir.mkdirs();
        }

        File[] files = inDir.listFiles(this.csvFiles);
        Arrays.stream(files).forEach((f) -> this.processOneFile(f));
    }

    private void processOneFile(File f) {
        this.split(f, firstDataLine);
    }

    public void split(File f, int firstLine) {
        String baseFilename = f.getName().substring(0, f.getName().length() - 4);
        if (!f.exists()) {
            Log.getLog().warning(String.format("File %s does not exist", f.getAbsolutePath()));
            return;
        }
        try {
            
            Log.getLog().info(String.format("Processing file %s", f.getAbsolutePath()));
            List<String[]> linesToWrite = new ArrayList<>();
            Date firstTime = null;

            File fileOutDir = this.getOutputDirectory(baseFilename);
            File lblFile = new File(fileOutDir,"labels_" + baseFilename + ".csv");
            this.labelFileWriter = new CSVWriter(new FileWriter(lblFile));
            this.labelFileWriter.writeNext(LABEL_FILE_HEADER);
            CSVReader reader = new CSVReader(new FileReader(f), ';');
            int count = 1;
            int fileCount = 1;
            String[] line = reader.readNext();
            while (line != null) {
                if (count < this.firstDataLine) {
                    line = reader.readNext();
                    count++;
                    continue;
                }
                if (firstTime == null) {
                    firstTime = this.getTimestampForLine(line);
                } else {
                    Date linetime = this.getTimestampForLine(line);
                    if (linetime == null) {
                        line = reader.readNext();
                        count++;
                        continue;
                    }
                    long diff = TimeUnit.SECONDS.convert(linetime.getTime() - firstTime.getTime(), TimeUnit.MILLISECONDS);
                    if (diff > this.splitTime) {
                        this.writeLinesToFile(linesToWrite, fileCount, baseFilename, fileOutDir);
                        Log.getActivityLog().info(String.format("Splitting to file %s for line index %s", fileCount, count));
                        fileCount++;
                        firstTime = null;
                        linesToWrite.clear();
                    }
                    linesToWrite.add(line);
                }
                line = reader.readNext();
                count++;
            }
            this.labelFileWriter.close();
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

    private void writeLinesToFile(List<String[]> lines, int fileIndex, String baseFileName, File outDir) throws IOException {
        String fname = String.format("%s_%s.csv", baseFileName, fileIndex);
        File f = new File(outDir, fname);
        //  Write row to label file
        String[] row = {fname, "0", "0", "1"};
        this.labelFileWriter.writeNext(row);
        CSVWriter writer = new CSVWriter(new FileWriter(f));
        writer.writeNext(DATA_FILE_HEADER);
        writer.writeAll(lines);
        writer.close();
    }

    private File getOutputDirectory(String baseFilename) throws IOException {
        File dir = new File(this.outDir, baseFilename + "_out");
        Log.getActivityLog().info(String.format("Creating file output directory %s", dir.getCanonicalPath()));
        dir.mkdir();
        return dir;
    }

}
