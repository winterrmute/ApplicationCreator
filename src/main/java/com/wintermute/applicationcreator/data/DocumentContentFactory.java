package com.wintermute.applicationcreator.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wintermute.applicationcreator.model.Applicant;
import com.wintermute.applicationcreator.model.CoverLetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides translated data objects for each document.
 *
 * @author wintermute
 */
public class DocumentContentFactory
{
    private static final Gson GSON = new Gson();

    /**
     * @param applicant JsonObject containing all information about the applicant.
     * @return translated, usable applicant data.
     */
    public Applicant getApplicant(JsonObject applicant)
    {
        DocumentComplexDataMapper objectTranslator = new DocumentComplexDataMapper();

        Applicant result = new Applicant();
        JsonObject info = applicant.getAsJsonObject("info");
        result.setPersonalInfo(GSON.fromJson(applicant.get("info"), Applicant.PersonalInfo.class));
        result.setHobbies(
            GSON.fromJson(info.getAsJsonArray("hobbies"), new TypeToken<ArrayList<String>>() {}.getType()));
        result.setLanguages(objectTranslator.languageMapper(GSON.fromJson(info.getAsJsonArray("spokenLanguages"),
            new TypeToken<List<Map<String, Object>>>() {}.getType())));

        result.setSkills(objectTranslator.skillsMapper(applicant.getAsJsonObject("skills")));

        result.setCareer(objectTranslator.careerMapper(applicant.getAsJsonObject("career")));

        result.setProjects(objectTranslator.projectMapper(applicant.getAsJsonObject("projects")));

        return result;
    }

    /**
     * @param coverLetter JsonObject containing all information about the cover letter.
     * @return translated, usable cover letter data.
     */
    public CoverLetter getCoverLetter(JsonObject coverLetter, Applicant.PersonalInfo applicantsPersonalInfo)
    {
        CoverLetter result = GSON.fromJson(coverLetter, CoverLetter.class);
        result.setApplicantsInfo(applicantsPersonalInfo);
        return result;
    }
}