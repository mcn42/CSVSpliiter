/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mtahq.csv.splitter;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author MNILSEN
 */
public class Log {
    private static Logger log = Logger.getLogger("org.mtahq.csv.splitter");
    private static Logger activityLog = Logger.getLogger("org.mtahq.csv.splitter.activity");
    
    public static void configure(String logPrefix)
    {   
        log.setUseParentHandlers(false);
        activityLog.setUseParentHandlers(false);
        log.setLevel(Level.ALL);
        
        try {
            String path = "./logs";
            File dir = new File(path);
            if(!dir.exists())
            {
                log.info(String.format("Creating Log directory at %s",dir.getCanonicalPath()));
                dir.mkdirs();
            }
            FileHandler fh = new FileHandler(path + "/" + logPrefix + ".log",1000000,6,false);
            fh.setFormatter(new SimpleFormatter());      
            fh.setLevel(Level.ALL);
            log.addHandler(fh);
            
            ConsoleHandler ch = new ConsoleHandler();
            ch.setLevel(Level.INFO);
            log.addHandler(ch);
                        
            fh = new FileHandler(path + "/" + logPrefix + "_error.log",1000000,6,false);
            fh.setFormatter(new SimpleFormatter());      
            fh.setLevel(Level.WARNING);
            log.addHandler(fh);
            
            fh = new FileHandler(path + "/" + logPrefix + "_activity.log",1000000,6,false);
            fh.setFormatter(new PlainFormatter());      
            fh.setLevel(Level.ALL);
            activityLog.addHandler(fh);
            
        } catch (IOException e) {
            log.log(Level.SEVERE,"Failed to add logging FileHandler",e);
        }
        
    }

    public static Logger getLog() {
        return log;
    }

    public static Logger getActivityLog() {
        return activityLog;
    }
    
    
}
