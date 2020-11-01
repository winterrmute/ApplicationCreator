package com.wintermute.applicationcreator.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Contains language information including rating and verbal description.
 *
 * @author wintermute
 */
@Data
@AllArgsConstructor
public class Language
{
    private String language;
    private int rating;
    private String levelDesc;
}
