package com.eft.mockwebserverproblem;

import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class Util {
    public String getRequestMessageFromPath(String filepath) {
        try(InputStream stream = getClass().getClassLoader().getResourceAsStream(filepath)) {
            assert stream != null;
            try(Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);) {
                return FileCopyUtils.copyToString(reader);
            }
        } catch (IOException e) {
            System.err.printf("#### Can't open resource file %s. Error: %s\n", filepath, e.getMessage());
        }
        return "no content";
    }
}
