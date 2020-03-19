package nl.nuggit.kwartet.model;

import org.springframework.stereotype.Component;

@Component
public class StringUtil {

    public String suffixWithNumber(String name) {
        int i = name.length() - 1;
        while (i > 0 && Character.isDigit(name.charAt(i))) {
            i--;
        }
        i++;
        String baseName = name.substring(0, i);
        String suffixStr = name.substring(i);
        int suffix;
        if (suffixStr.isEmpty()) {
            suffix = 0;
        } else {
            suffix = Integer.parseInt(suffixStr);
        }
        suffix++;
        return baseName + suffix;
    }
}
