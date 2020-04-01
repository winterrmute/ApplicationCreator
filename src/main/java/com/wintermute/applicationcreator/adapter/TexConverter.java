package com.wintermute.applicationcreator.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Converts data into tex statements for provided template.
 *
 * @author wintermute
 */
public class TexConverter
{
    private Map<String, Object> data;

    public TexConverter(Map<String, Object> data)
    {
        this.data = data;
    }

    public Map<String, Object> getConvertedData()
    {
        Map<String, Object> result = new HashMap<>();
        result.put("header_placeholder", getHeaderData());
        result.put("persnoal_information_placeholder", getPersonalInfo());
        result.put("career_placeholder", getCareer("professionalCareer"));
        result.put("education_placeholder", getCareer("educationalCareer"));
        final Map<String, List<String>> skills = getSkills();
        final Object[] keys = skills.keySet().toArray();
        result.put("skills_title1", "\\tabletitle{" + keys[0] + "}");
        result.put("skills_title2", "\\tabletitle{" + keys[1] + "}");
        result.put("soft_skills", getSoftSkills());
        result.put("first_category_skills_placeholder", skills.get(keys[0]));
        result.put("second_category_skills_placeholder", skills.get(keys[1]));
        result.put("projects_placeholder", getProjects());
        result.put("languages_placeholder", getSpokenLanguages());
        result.put("hobbies_placeholder", getHobbies());

        return result;
    }

    /**
     * @return header in tex
     */
    String getHeaderData()
    {
        Map<String, Object> headerData = (Map<String, Object>) data.get("info");
        String prefix = "\\headerbox{1.2cm}{darkgray}{white}{";
        String suffix = "}{pics/pic2.jpg}";
        StringBuilder texLine = new StringBuilder();
        buildStatement(texLine, prefix, headerData.get("firstName").toString(), "\\\\ ",
            headerData.get("lastName").toString(), suffix);
        return texLine.toString();
    }

    /**
     * @return personal info as tex
     */
    String getPersonalInfo()
    {
        Map<String, Object> personalInfo = (Map<String, Object>) data.get("info");
        String result =
            "\\faEnvelopeO\\/ street | \\faMapMarker\\/ city | \\faPhone\\/ phonenumber " + "|\\faAt\\protect\\/ email";
        Map<String, String> replacements = new HashMap<>();
        replacements.put("street", extractEntry(personalInfo, "contact", "address"));
        replacements.put("city", extractEntry(personalInfo, "contact", "city"));
        replacements.put("phonenumber", extractEntry(personalInfo, "contact", "phone"));
        replacements.put("email", extractEntry(personalInfo, "contact", "email"));

        return prepareTexString(result, replacements);
    }

    /**
     * @param careerType of which details should be get.
     * @return all information of picked career as tex.
     */
    List<String> getCareerInfo(String careerType)
    {
        return getCareer(careerType);
    }

    /**
     * @return skills organized by focus and category as tex.
     */
    Map<String, List<String>> getSkills()
    {
        Map<String, List<String>> orderedSkillsByCategories = new HashMap<>();
        Map<String, Object> skills = (Map<String, Object>) data.get("skills");

        for (String focus : skills.keySet())
        {
            if (!"soft".equals(focus))
            {
                List value = generateSkills(orderByCategory((List) skills.get(focus)));
                orderedSkillsByCategories.put(focus, value);
            }
        }
        return orderedSkillsByCategories;
    }

    /**
     * @return organized projects as tex.
     */
    List<String> getProjects()
    {
        List<String> result = new ArrayList<>();
        List<Map<String, Object>> projects = (List<Map<String, Object>>) data.get("projects");

        StringBuilder texLine;
        for (Map<String, Object> project : projects)
        {
            texLine = new StringBuilder();
            buildStatement(texLine, "\\columntitle{", (String) project.get("from"), " -- ",
                (String) project.get("until"), "} & \\activity{", (String) project.get("summary"), "}{",
                (String) project.get("position"), "}{", (String) project.get("description"), "}\\\\");

            getToolsForProject(texLine, (Map<String, List<String>>) project.get("tools"));

            result.add(texLine.toString());
        }

        return result;
    }

    /**
     * @return sorted languages by number contained in json and converted to tex.
     */
    List<String> getSpokenLanguages()
    {
        Map<String, Object> info = (Map<String, Object>) data.get("info");
        Map<String, String> spokenLanguages = sortByValue((Map<String, String>) info.get("spokenLanguages"));

        List<String> result = new ArrayList<>();
        StringBuilder texLine;
        for (Map.Entry<String, String> language : spokenLanguages.entrySet())
        {
            texLine = new StringBuilder();
            buildStatement(texLine, "\\columntitle{", language.getKey(),
                "} & \\singleitem{" + language.getValue().split("[0-9] ")[1], "}\\\\");
            result.add(texLine.toString());
        }
        return result;
    }

    /**
     * @return list of personal interests.
     */
    String getHobbies()
    {
        Map<String, Object> info = (Map<String, Object>) data.get("info");
        return getCommaSeparatedTex((List<String>) info.get("personalInterests"));
    }

    /**
     * @return list of personal strenghts.
     */
    String getSoftSkills()
    {
        Map<String, Object> skills = (Map<String, Object>) data.get("skills");
        return getCommaSeparatedTex((List<String>) skills.get("soft"));
    }

    private String getCommaSeparatedTex(List<String> input)
    {
        StringBuilder texLine = new StringBuilder("\\commaseparatedlist");
        for (String elem : input)
        {
            buildStatement(texLine, "{" + elem + "}");
        }
        return texLine.toString();
    }

    private static Map<String, String> sortByValue(Map<String, String> target)
    {
        List<Map.Entry<String, String>> list = new LinkedList<>(target.entrySet());
        Collections.sort(list, Comparator.comparing(Map.Entry::getValue));

        Map<String, String> sortedMap = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    private void getToolsForProject(StringBuilder target, Map<String, List<String>> tools)
    {
        for (Map.Entry<String, List<String>> category : tools.entrySet())
        {
            if (!category.getValue().get(0).equals("")){
                buildStatement(target, " \\columnsubtitle{", category.getKey(), "} & \\commaseparatedlist");
                for (String elem : category.getValue())
                {
                    buildStatement(target, "{", elem, "}");
                }
                if (category.getValue().size() == 1)
                {
                    target.append("\\newline");
                }
                target.append("\\\\");
            }
        }
    }

    private void buildStatement(StringBuilder target, String... keys)
    {
        for (String key : keys)
        {
            target.append(key);
        }
    }

    private int calculateOffset(Collection first, Collection second)
    {
        final int max = Math.max(first.size(), second.size());
        final int min = Math.min(first.size(), second.size());
        return max - min;
    }

    private List<String> generateSkills(Map<String, Object> skillsByCategory)
    {
        List<String> result = new ArrayList<>();
        StringBuilder texLine;
        for (Map.Entry<String, Object> elem : skillsByCategory.entrySet())
        {
            texLine = new StringBuilder("\\columntitle{").append(elem.getKey()).append("} & \\newlinelist");

            for (String skill : (List<String>) elem.getValue())
            {
                if (skill == null)
                {
                    result.add("& \\\\");
                } else
                {
                    texLine.append("{").append(skill).append("}");
                }
                if (((List<String>) elem.getValue()).size() == 1)
                {
                    texLine.append("\\newline");
                }
            }
            result.add(texLine.append("\\\\").toString());
        }
        if (result.contains("& \\\\"))
        {
            repairContainingBlank(result);
        }
        return result;
    }

    private void repairContainingBlank(List<String> list)
    {
        List<String> toMove = new ArrayList<>();
        for (String elem : list)
        {
            if ("& \\\\".equals(elem))
            {
                toMove.add(elem);
            }
        }
        list.removeAll(toMove);
        list.addAll(toMove);
        list.remove("\\\\");
    }

    private Map<String, List<String>> orderByCategory(List<Map<String, Object>> skills)
    {
        Map<String, List<String>> result = new HashMap<>();
        List<String> listByCategory;
        for (Map<String, Object> skill : skills)
        {
            String category = (String) skill.get("category");

            result.putIfAbsent(category, new ArrayList<>());
            listByCategory = result.get(category);
            listByCategory.add((String) skill.get("description"));
            result.put(category, listByCategory);
        }
        return result;
    }

    private List<String> getCareer(String careerType)
    {
        Map<String, Object> career = (Map<String, Object>) data.get("career");
        return getCareerByType((List<Map<String, Object>>) career.get(careerType), careerType);
    }

    private List<String> getCareerByType(List<Map<String, Object>> careerInfo, String careerType)
    {
        List<String> result = new ArrayList<>();
        StringBuilder texLine;
        for (Map<String, Object> career : careerInfo)
        {
            texLine = new StringBuilder();
            buildStatement(texLine, "\\columntitle{", (String) career.get("from"), " -- ", (String) career.get("until"),
                "} & \\activity{");
            if ("educationalCareer".equals(careerType))
            {
                buildStatement(texLine, (String) career.get("school"), "}{", (String) career.get("graduation"));
            } else
            {
                buildStatement(texLine, (String) career.get("company"), "}{", (String) career.get("job"), "}{",
                    (String) career.get("description"));
            }
            texLine.append("}\\\\");
            result.add(texLine.toString());
        }
        return result;
    }

    private String prepareTexString(String template, Map<String, String> replacements)
    {
        StringBuilder result = new StringBuilder();
        for (String word : template.split(" "))
        {
            if (replacements.keySet().contains(word))
            {
                result.append(" ").append(replacements.get(word)).append(" ");
            } else
            {
                result.append(word);
            }
        }
        return result.toString();
    }

    private String extractEntry(Map<String, Object> data, String... keys)
    {
        for (String key : keys)
        {
            if (data.get(key) instanceof String)
            {
                return (String) data.get(key);
            } else
            {
                data = (Map) data.get(key);
            }
        }
        return null;
    }
}
