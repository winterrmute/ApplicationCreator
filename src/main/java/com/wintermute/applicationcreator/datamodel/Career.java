package com.wintermute.applicationcreator.datamodel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor //TODO: deleteme
@EqualsAndHashCode(callSuper=false)
public class Career extends WithDate
{
    private String title;
    private String category;
    private String job;
    private String description;

    /**
     * Creates an instance
     *
     * @param category to differ between educational and professional.
     */
    public Career(String category) {
        this.category = category;
    }
}
