package com.wintermute.applicationcreator.adapter;

import java.util.ArrayList;
import java.util.HashMap;
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
     * @return personal info in tex
     */
    public String generatePersonalInfo()
    {
        Map<String, Object> personalInfo = (Map<String, Object>) data.get("info");
        String result = "\\faEnvelopeO\\/ street | \\faMapMarker\\/ city | \\faPhone\\/ phonenumber "
            + "|\t\\faAt\\protect\\/ email";
        Map<String, String> replacements = new HashMap<>();
        replacements.put("street", extractEntry(personalInfo, "contact", "address"));
        replacements.put("city", extractEntry(personalInfo, "contact", "city"));
        replacements.put("phonenumber", extractEntry(personalInfo, "contact", "phone"));
        replacements.put("email", extractEntry(personalInfo, "contact", "email"));

        return prepareTexString(result, replacements);
    }

    /**
     * @param careerType of which details should be get.
     * @return all information of picked career.
     */
    public List<String> getCareerInfo(String careerType)
    {
        return generateCareer(careerType);
    }

    /**
     * @return skills organized by focus and category.
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

    private List<String> offsetLists(List<String> bigger, List<String>smaller){
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
            texLine = new StringBuilder("\\columntitle{").append(elem.getKey()).append("} & \newlinelist");
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
            texLine = new StringBuilder("\\columntitle{")
                .append(career.get("from"))
                .append(" -- ")
                .append(career.get("until"))
                .append("} & \\activity{");
            if ("educationalCareer".equals(careerType))
            {
                texLine.append(career.get("school")).append("}{").append(career.get("graduation"));
            } else
            {
                texLine
                    .append(career.get("company"))
                    .append("}{")
                    .append(career.get("job"))
                    .append("}{")
                    .append(career.get("description"));
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
