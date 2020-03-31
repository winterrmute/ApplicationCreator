package com.wintermute.applicationcreator.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TexConverter
{
    private Map<String, Object> data;

    public TexConverter(Map<String, Object> data)
    {
        this.data = data;
    }

    /**
     * @return header in tex
     */
    public String generateHeader()
    {
        Map<String, Object> headerData = (Map<String, Object>) data.get("info");
        String prefix = "\\headerbox{1.2cm}{darkgray}{white}{";
        String suffix = "}{pics/pic.jpg}";
        return new StringBuilder(prefix)
            .append(headerData.get("firstName").toString())
            .append("\\\\ ")
            .append(headerData.get("lastName").toString())
            .append(suffix)
            .toString();
    }

    /**
     * @return personal info as tex
     */
    public String generatePersonalInfo()
    {
        Map<String, Object> personalInfo = (Map<String, Object>) data.get("info");
        String result = "\\faEnvelopeO\\/ street | \\faMapMarker\\/ city | \\faPhone\\/ phonenumber "
            + "|\\faAt\\protect\\/ email";
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
    public List<String> getCareerInfo(String careerType)
    {
        return generateCareer(careerType);
    }

    /**
     * @return skills organized by focus and category as tex.
     */
    public Map<String, List<String>> getSkills()
    {
        Map<String, Object> skills = (Map<String, Object>) data.get("skills");
        Map<String, List<String>> orderedSkillsByCategories = new HashMap<>();
        for (String focus : skills.keySet())
        {
            if (!"soft".equals(focus))
            {
                orderedSkillsByCategories.put(focus, generateSkills(orderByCategory((List) skills.get(focus))));
            }
        }
        return offsetSkills(orderedSkillsByCategories);
    }

    /**
     * @return organized projects as tex.
     */
    public List<String> getProjects()
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
    public List<String> getSpokenLanguages()
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
    public String getHobbys()
    {
        Map<String, Object> info = (Map<String, Object>) data.get("info");
        return getCommaSeparatedTex((List<String>) info.get("personalInterests"));
    }

    /**
     * @return list of personal strenghts.
     */
    public String getPersonalStrenghts()
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
            buildStatement(target, " \\columnsubtitle{", category.getKey(), "} & \\commaseparatedlist");
            for (String elem : category.getValue())
            {
                buildStatement(target, "{", elem, "}");
            }
            target.append("\\\\");
        }
    }

    private void buildStatement(StringBuilder target, String... keys)
    {
        for (String key : keys)
        {
            target.append(key);
        }
    }

    private Map<String, List<String>> offsetSkills(Map<String, List<String>> skillsLists)
    {
        List<String> keys = new ArrayList<>(skillsLists.keySet());
        List<String> firstCategry = skillsLists.get(keys.get(0));
        List<String> secondCategory = skillsLists.get(keys.get(1));

        if (firstCategry.size() > secondCategory.size())
        {
            List<String> offsetList = offsetLists(firstCategry, secondCategory);
            skillsLists.put(keys.get(1), offsetList);
        } else
        {
            List<String> offsetList = offsetLists(secondCategory, firstCategry);
            skillsLists.put(keys.get(0), offsetList);
        }
        return skillsLists;
    }

    private List<String> offsetLists(List<String> bigger, List<String> smaller)
    {
        for (int i = 0; i < bigger.size() - smaller.size(); i++)
        {
            smaller.add("& \\\\");
        }
        return smaller;
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
                texLine.append("{").append(skill).append("}");
            }
            result.add(texLine.toString());
        }
        return result;
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

    private List<String> generateCareer(String careerType)
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
