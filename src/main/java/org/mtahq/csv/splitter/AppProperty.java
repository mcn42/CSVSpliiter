/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mtahq.csv.splitter;

/**
 *
 * @author MNILSEN
 */
public enum AppProperty implements IAppProperty {
    
    LOG_FILE_PREFIX("splitter");
    
    
    private final String defaultValue;
    
    private AppProperty(String defaultVal)
    {
        this.defaultValue = defaultVal;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
