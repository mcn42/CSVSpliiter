/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mtahq.csv.splitter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 *
 * @author MNILSEN
 */
public class PlainFormatter extends Formatter {
    private static SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss.S");
    @Override
    public String format(LogRecord record) {
        return String.format("%s: %s\n", df.format(new Date(record.getMillis())),record.getMessage());
    }
    
}
