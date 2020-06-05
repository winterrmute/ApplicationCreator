package com.wintermute.applicationcreator.applicationData;

import lombok.Data;

import java.util.List;

@Data
public class CoverLetter
{
    private String applicationAs;
    private String formOfAddress;
    private List<String> paragraphs;
    private Recipient recipient;
}
