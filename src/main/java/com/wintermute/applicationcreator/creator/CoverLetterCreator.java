package com.wintermute.applicationcreator.creator;

import com.wintermute.applicationcreator.applicationData.Applicant;
import com.wintermute.applicationcreator.applicationData.CoverLetter;
import com.wintermute.applicationcreator.applicationData.Recipient;

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
public class CoverLetterCreator extends TexCreator
{

    private Applicant applicant;
    private Recipient recipient;
    private CoverLetter coverLetter;

    public CoverLetterCreator(Applicant applicant, CoverLetter coverLetter)
    {
        super(null);
        this.applicant = applicant;
        this.coverLetter = coverLetter;
        this.recipient = coverLetter.getRecipient();
    }

    @Override
    public void create(File file)
    {
        File out = writeNewFile("coverLetter");
        try (BufferedReader br = new BufferedReader(new FileReader(file)); FileWriter fw = new FileWriter(out))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                if (line.contains(":header_date:"))
                {
                    line = writeIntoFile(line, ":header_date:",
                        applicant.getContact().getCity() + ", den " + LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(
                            FormatStyle.LONG)));
                }
                if (line.contains(":name:"))
                {
                    line = writeIntoFile(line, ":name:", applicant.getFirstName() + " " + applicant.getLastName());
                }
                if (line.contains(":address:"))
                {
                    line = writeIntoFile(line, ":address:",
                        applicant.getContact().getZipCode() + " " + applicant.getContact().getCity() //
                            + ", " + applicant.getContact().getAddress());
                }
                if (line.contains(":phone:"))
                {
                    line = writeIntoFile(line, ":phone:", applicant.getContact().getPhoneNumber());
                }
                if (line.contains(":email:"))
                {
                    line = writeIntoFile(line, ":email:", applicant.getContact().getEmail());
                }
                if (line.contains(":website:"))
                {
                    line = writeIntoFile(line, ":website:", applicant.getContact().getWebsite());
                }

                if (line.contains(":company:"))
                {
                    line = writeIntoFile(line, ":company:", recipient.getCompany());
                }
                if (line.contains(":contactPerson:"))
                {
                    if (recipient.getContactPerson() == null)
                    {
                        line = writeIntoFile(line, ":contactPerson:", "");
                    } else
                    {
                        line = writeIntoFile(line, ":contactPerson:", recipient.getContactPerson());
                    }
                }
                if (line.contains(":recipientAddress:"))
                {
                    line = writeIntoFile(line, ":recipientAddress:", recipient.getContact().getAddress());
                }
                if (line.contains(":recipientCity:"))
                {
                    line = writeIntoFile(line, ":recipientCity:",
                        recipient.getContact().getZipCode() + " " + recipient.getContact().getCity());
                }

                if (line.contains(":applicationTopic:"))
                {
                    line = writeIntoFile(line, ":applicationTopic:", coverLetter.getApplicationAs());
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

    private String writeIntoFile(String toReplace, String holder, String target)
    {
        return toReplace.replace(holder, target);
    }
}
