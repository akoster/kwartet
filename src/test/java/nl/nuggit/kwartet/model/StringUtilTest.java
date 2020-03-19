package nl.nuggit.kwartet.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StringUtilTest {

    private StringUtil instance = new StringUtil();

    @Test
    void suffixWithNumber() {
        assertEquals("aap1", instance.suffixWithNumber("aap"));
        assertEquals("noot124", instance.suffixWithNumber("noot123"));
        assertEquals("mies1000", instance.suffixWithNumber("mies999"));
        assertEquals("w1m1", instance.suffixWithNumber("w1m"));
        assertEquals("1", instance.suffixWithNumber(""));
    }
}