package com.wintermute.applicationcreator.wrapper;

import com.wintermute.applicationcreator.ApplicationCreator;
import com.wintermute.applicationcreator.adapter.TexConverter;
import com.wintermute.applicationcreator.creator.TexCreator;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Map;

/**
 * This class wraps the usage of {@link TexCreator}.
 *
 * @author wintermute
 */
public class TexWrapper
{
    /**
     * Converts input data to TexFormat
     *
     * @param converter type of document converter.
     * @param <T> type of converter.
     * @return converted data by selected data converter.
     */
    public <T extends TexConverter> Map<String, Object> convert(T converter)
    {
        return converter.getConvertedData();
    }

    /**
     * Creates tex file based on specified document. ATM possible CV or CoverLetter.
     *
     * @param creator of which document to create
     * @param file target directory to save created file
     * @param <T> TexCreator type
     */
    public <T extends TexCreator> void createTex(T creator, String file)
    {
        try
        {
            creator.create(Paths.get(ApplicationCreator.class.getClassLoader().getResource(file).toURI()).toFile());
        } catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
    }
}
