package com.wintermute.applicationcreator.applicationData;

import lombok.Data;

import java.util.List;

@Data
public class CoverLetterInfo
{
    private String applicationAs;
    private String formOfAddress;
    private List<String> paragraphs;
}
