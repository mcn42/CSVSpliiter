/*
 * Copyright Metropolitan Transportation Authority NY
 * All Rights Reserved
 */
package org.mtahq.csv.splitter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mnilsen
 */
public class ParseTest {
    static {
        Utils.config(".");
    }
    private static final String dateFormatPattern = Utils.getAppProperties().get(SplitterAppProperties.DEFAULT_TSTAMP_DATE_FORMAT);
    private static final SimpleDateFormat tsFormat = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss.SSSX");
    
    public static void main(String[] args) {
        
        
        try {
            Date d = tsFormat.parse("2018-04-24T09:35:04.028-05:00");
        } catch (ParseException ex) {
            Logger.getLogger(ParseTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
