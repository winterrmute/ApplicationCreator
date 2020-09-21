package com.wintermute.applicationcreator.data.mapper;

import com.wintermute.applicationcreator.model.CategoryGroup;
import com.wintermute.applicationcreator.model.Skill;

import java.util.List;
import java.util.Map;

/**
 * Organizes skills by group and rating.
 *
 * @author wintermute
 */
public class SkillsMapper extends CategoryMapper
{
    /**
     * @return organized skills.
     */
    public Map<CategoryGroup, List<Skill>> getSkillsForApplicant(Map<String, Map<String, Object>> data)
    {
        return getCategoryGroupForCategory(data, this::getSkill);
    }

    private Skill getSkill(Map<String, Object> skillInfo)
    {
        Skill result = new Skill(skillInfo.get("category").toString());
        result.setTitle(skillInfo.get("description").toString());
        result.setRating(Integer.parseInt(skillInfo.get("rating").toString()));
        return result;
    }
}
