package com.wintermute.applicationcreator.document;

import com.wintermute.applicationcreator.model.Applicant;
import com.wintermute.applicationcreator.model.CoverLetter;
import com.wintermute.applicationcreator.model.Recipient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Creates application cover letter in latex from extracted data.
 *
 * @author wintermute
 */
public class CoverLetterCreator extends DocumentCreator
{

    private final Applicant applicant;
    private final CoverLetter coverLetter;

    public CoverLetterCreator(Applicant applicant, CoverLetter coverLetter)
    {
        this.applicant = applicant;
        this.coverLetter = coverLetter;
    }

    @Override
    public void createDocument(File template)
    {
        File out = writeNewFile("coverLetter");
        try (BufferedReader br = new BufferedReader(new FileReader(template)); FileWriter fw = new FileWriter(out))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                if (line.contains(":header_date:"))
                {
                    line = getPreparedDocumentPart(line, ":header_date:", applicant.getContact().getCity() + ", den " + LocalDate
                        .now()
                        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
                }
                if (line.contains(":name:"))
                {
                    line = getPreparedDocumentPart(line, ":name:", applicant.getPersonalInfo().getFullName());
                }
                if (line.contains(":address:"))
                {
                    line = getPreparedDocumentPart(line, ":address:",
                        applicant.getContact().getCityWithZipcode() + ", " + applicant.getContact().getAddress());
                }
                if (line.contains(":phone:"))
                {
                    line = getPreparedDocumentPart(line, ":phone:", applicant.getContact().getPhoneNumber());
                }
                if (line.contains(":email:"))
                {
                    line = getPreparedDocumentPart(line, ":email:", applicant.getContact().getEmail());
                }
                if (line.contains(":website:"))
                {
                    line = getPreparedDocumentPart(line, ":website:", applicant.getContact().getWebsite());
                }

                Recipient recipient = coverLetter.getRecipient();
                if (line.contains(":company:"))
                {
                    line = getPreparedDocumentPart(line, ":company:", recipient.getCompany());
                }
                if (line.contains(":contactPerson:"))
                {
                    if (recipient.getContactPerson() == null)
                    {
                        line = getPreparedDocumentPart(line, ":contactPerson:", "");
                    } else
                    {
                        line = getPreparedDocumentPart(line, ":contactPerson:", recipient.getContactPerson());
                    }
                }
                if (line.contains(":recipientAddress:"))
                {
                    line = getPreparedDocumentPart(line, ":recipientAddress:", recipient.getContact().getAddress());
                }
                if (line.contains(":recipientCity:"))
                {
                    line = getPreparedDocumentPart(line, ":recipientCity:",
                        recipient.getContact().getCityWithZipcode());
                }

                if (line.contains(":applicationTopic:"))
                {
                    line = getPreparedDocumentPart(line, ":applicationTopic:", coverLetter.getApplicationTopic());
                }
                if (line.contains(":text:"))
                {
                    line = line.replace(":text:", "");
                    fw.write(line);

                    for (String elem : coverLetter.getParagraphs())
                    {
                        fw.write("\\coverparagraph{" + elem + "}\n\n");
                    }
                    continue;
                }
                fw.write(line);
                fw.write("\n");
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}