package com.wintermute.applicationcreator.creator;

import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Paths;

public class TexCreatorTest
{
    @Test
    public void testCreatingFile() throws URISyntaxException
    {
        TexCreator underTest = new TexCreator("kk.json");
        underTest.createTexFile(
            Paths.get(TexCreatorTest.class.getClassLoader().getResource("cv.tex").toURI()).toFile());
    }
}
