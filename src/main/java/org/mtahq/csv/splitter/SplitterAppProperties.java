/*
 * Copyright Metropolitan Transportation Authority NY
 * All Rights Reserved
 */
package org.mtahq.csv.splitter;

/**
 *
 * @author mnilsen
 */
public enum SplitterAppProperties implements IAppProperty {

    DEFAULT_OUTPUT_DIRECTORY_NAME("output"),
    DEFAULT_SPLIT_TIME_SECS("30"),
    DEFAULT_TSTAMP_DATE_FORMAT("YYYY-MM-dd'T'HH:mm:ss.SSSX"),
    DEFAULT_TSTAMP_COLUMN_IDX("0"),
    DEFAULT_DATA_COLUMN_IDX("5"),
    DEFAULT_FIRST_DATA_LINE("16");
    
    //  2018-04-24T09:35:04.028-05:00
    
    private final String defaultValue;

    private SplitterAppProperties(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    @Override
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
}
