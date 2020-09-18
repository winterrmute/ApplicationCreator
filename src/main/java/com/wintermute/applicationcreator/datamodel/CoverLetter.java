package com.wintermute.applicationcreator.datamodel;

import lombok.Data;

import java.util.List;

@Data
public class CoverLetter
{
    private String applicationTopic;
    private String formOfAddress;
    private List<String> paragraphs;
    private Recipient recipient;
}
