package com.wintermute.applicationcreator.document;

import java.io.File;
import java.io.IOException;

/**
 * This class describes the process of creating specified types of documents and required information.
 *
 * @author wintermute
 */
public abstract class DocumentCreator
{

    /**
     * Takes template and generates document of it.
     *
     * @param template for specified type of document.
     */
    public abstract void createDocument(File template);

    String getPreparedDocumentPart(String toReplace, String holder, String target)
    {
        return toReplace.replace(holder, target);
    }

    File writeNewFile(String name)
    {
        final String path = "src/main/resources/texTemplate/" + name + ".tex";
        File result = new File(path);
        try
        {
            if (result.createNewFile())
            {
                System.out.println("File created: " + result.getName());
            } else
            {
                System.out.println("File already exists.");
            }
        } catch (IOException e)
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return result;
    }
}
