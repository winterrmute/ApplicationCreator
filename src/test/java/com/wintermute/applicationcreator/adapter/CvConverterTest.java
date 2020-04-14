package com.wintermute.applicationcreator.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

/**
 * Tests the result of converted curriculum vitae data into tex.
 *
 * @author wintermute
 */
public class CvConverterTest extends TexConverterBase
{
    @Test
    public void checkConvertedData()
    {
        CvConverter underTest = new CvConverter(dataCollector.getData());
        Map<String, Object> data = underTest.getConvertedData();

        assertThat(data.get("header_placeholder").toString()).isEqualTo(
            "\\headerbox{1.2cm}{darkgray}{white}{Azathoth\\\\ Outer\\\\God}{pics/pic.jpg}");
        assertThat(data.get("persnoal_information_placeholder")).isEqualTo(
            "\\faEnvelopeO\\/ Somwhere in universe |\\faMapMarker\\/ arkham |\\faPhone\\/ prayer |\\faAt\\protect\\/ "
                + "prayer@god.org ");

        List<String> education = (List<String>) data.get("education_placeholder");
        assertThat(education.get(0)).isEqualTo("\\columntitle{08/1204 -- 06/1209} & \\activity{University of universe"
            + " chaos study}{Bachelor of Science}\\\\");

        List<String> professional = (List<String>) data.get("career_placeholder");
        assertThat(professional.get(0)).isEqualTo(
            "\\columntitle{05/1209 -- today} & \\activity{Unknown}{protagonist in " + "necronomicon}{god}\\\\");

        List<String> projects = (List<String>) data.get("projects_placeholder");
        assertThat(projects.size()).isEqualTo(2);
        assertThat(projects.get(0)).startsWith(
            "\\columntitle{09.1215 -- 12.1220} & \\activity{warping reality}{Outer god}{distortion of space}\\");

        List<String> languages = (List<String>) data.get("languages_placeholder");
        assertThat(languages.size()).isEqualTo(4);
        assertThat(languages.get(0)).isEqualTo("\\columntitle{R’lyehian} & \\singleitem{native speaker}\\\\");

        assertThat(data.get("hobbies_placeholder")).isEqualTo("\\commaseparatedlist{Destroying worlds}{Beeing god}");

        assertThat(data.get("soft_skills")).isEqualTo(
            "\\commaseparatedlist{being god}{being blind}{motivated to do really bad things}");
        System.out.println();
    }
}
