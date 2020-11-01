package com.wintermute.applicationcreator.model;

import lombok.Data;

import java.util.List;

/**
 * Contains information enclosed in cover letter.
 *
 * @author wintermute.
 */
@Data
public class CoverLetter
{
    private String applicationTopic;
    private List<String> paragraphs;
    private Recipient recipient;
    private Applicant.PersonalInfo applicantsInfo;

    @Data
    public static class Recipient
    {
        private String company;
        private String contactPerson;
        private Contact contact;
    }
}
