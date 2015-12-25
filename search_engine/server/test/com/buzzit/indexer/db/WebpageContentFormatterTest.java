package com.buzzit.indexer.db;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class WebpageContentFormatterTest {
    @Test
    public void testFormatter() {
        String content = "<p>sth</p>\n<sth>\t<sth>\n\r\n\r";
        content = WebpageContentFormatter.getFormatContent(content);
        assertEquals(false, content.contains("\n"));
        assertEquals(false, content.contains("\r"));
        assertEquals(false, content.contains("\t"));
    }
}