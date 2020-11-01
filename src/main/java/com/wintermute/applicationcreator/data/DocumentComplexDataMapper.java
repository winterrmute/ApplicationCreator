package com.wintermute.applicationcreator.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wintermute.applicationcreator.data.sort.DateComparator;
import com.wintermute.applicationcreator.data.sort.PositionComparator;
import com.wintermute.applicationcreator.model.complex.CategoryGroup;
import com.wintermute.applicationcreator.model.Language;
import com.wintermute.applicationcreator.model.complex.Career;
import com.wintermute.applicationcreator.model.complex.Project;
import com.wintermute.applicationcreator.model.complex.Skill;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Mapper for complex data divided into categories and/or grouped and/or containing fixed position.
 */
public class DocumentComplexDataMapper
{
    public static final Gson GSON = new Gson();

    /**
     * @param skillsDesc JsonObject containing skills categories enclosing skills divided into groups.
     * @return skills categories enclosing skills divided into groups.
     */
    public Map<CategoryGroup, Map<CategoryGroup, List<Skill>>> skillsMapper(JsonObject skillsDesc)
    {
        Map<CategoryGroup, Map<CategoryGroup, List<Skill>>> result = new TreeMap<>(new PositionComparator());

        //TODO: check if it can be optimized with recursion
        skillsDesc.entrySet().forEach(c ->
        {
            JsonObject categoryDesc = c.getValue().getAsJsonObject();
            CategoryGroup category =
                new CategoryGroup(categoryDesc.get("title").getAsString(), categoryDesc.get("position").getAsInt());
            result.put(category, new TreeMap<>(new PositionComparator()));
            categoryDesc.remove("title");
            categoryDesc.remove("position");
            categoryDesc.entrySet().forEach(subs ->
            {

                subs.getValue().getAsJsonArray().forEach(sub ->
                {
                    JsonObject subCategory = sub.getAsJsonObject();
                    CategoryGroup skillsGroup = new CategoryGroup(subCategory.get("title").getAsString(),
                        subCategory.get("position").getAsInt());
                    result.get(category).put(skillsGroup, new ArrayList<>());
                    subCategory.remove("title");
                    subCategory.remove("position");

                    subCategory.entrySet().forEach(sL ->
                    {
                        List<Skill> skillsInGroup = new ArrayList<>();
                        sL.getValue().getAsJsonArray().forEach(s -> skillsInGroup.add(GSON.fromJson(s, Skill.class)));
                        skillsInGroup.sort(new PositionComparator());
                        result.get(category).get(skillsGroup).addAll(skillsInGroup);
                    });
                });
            });
        });

        return result;
    }

    /**
     * @param languages list of spoken languages of user.
     * @return ascending sorted list of languages by its level.
     */
    public List<Language> languageMapper(List<Map<String, Object>> languages)
    {
        List<Language> result = new ArrayList<>();
        languages.forEach(l -> result.add(
            new Language(l.get("language").toString(), Integer.parseInt(l.get("position").toString()),
                l.get("levelDesc").toString())));
        result.sort(Comparator.comparingInt(Language::getRating).reversed());
        return result;
    }

    /**
     * @param careers inside of user data.
     * @return ascending sorted list of career's category and contained inside projects.
     */
    public Map<CategoryGroup, List<Career>> careerMapper(JsonObject careers)
    {
        return careerAndProjectMapper(careers, Career.class);
    }

    /**
     * @param projects inside of user data.
     * @return ascending sorted list of project's categories and contained inside projects.
     */
    public Map<CategoryGroup, List<Project>> projectMapper(JsonObject projects)
    {
        return careerAndProjectMapper(projects, Project.class);
    }

    private <T> Map<CategoryGroup, List<T>> careerAndProjectMapper(JsonObject data, Class<T> clazz)
    {

        Map<CategoryGroup, List<T>> result = new TreeMap<>(new PositionComparator());

        data.entrySet().forEach(cat ->
        {
            JsonObject categoryDesc = cat.getValue().getAsJsonObject();
            CategoryGroup category =
                new CategoryGroup(categoryDesc.get("title").getAsString(), categoryDesc.get("position").getAsInt());
            result.put(category, new ArrayList<>());
            categoryDesc.remove("title");
            categoryDesc.remove("position");
            categoryDesc.entrySet().forEach(car ->
            {
                ArrayList<T> careers = new ArrayList<>();
                car.getValue().getAsJsonArray().forEach(c -> careers.add(new Gson().fromJson(c, clazz)));
                careers.sort((Comparator<? super T>) new DateComparator());
                result.get(category).addAll(careers);
            });
        });
        return result;
    }
}
