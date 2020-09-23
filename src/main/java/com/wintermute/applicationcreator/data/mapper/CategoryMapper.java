package com.wintermute.applicationcreator.data.mapper;

import com.wintermute.applicationcreator.data.sort.RatingComparator;
import com.wintermute.applicationcreator.model.CategoryGroup;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Provides functionality to map content of categories into groups.
 */
public abstract class CategoryMapper
{
    /**
     * @return all category groups with its content.
     */
    protected <T> Map<CategoryGroup, List<T>> getCategoryGroupForCategory(Map<String, Map<String, Object>> data,
                                                                          Function<Map<String, Object>, T> getEntityFromType)
    {
        Map<CategoryGroup, List<T>> result = new TreeMap<>(new RatingComparator());
        //TODO: check for same entries but in different categories. Sometimes they will be merged. It´s critical.
        data.forEach((categoryGroup, content) -> result.put(
            new CategoryGroup(categoryGroup, content.get("title").toString(),
                Integer.parseInt(content.get("position").toString())),
            getContentForCategoryGroup((List<Map<String, Object>>) content.get("children"), getEntityFromType)));
        return result;
    }

    private <T> List<T> getContentForCategoryGroup(List<Map<String, Object>> target,
                                                   Function<Map<String, Object>, T> getEntityFromType)
    {
        return target.stream().map(getEntityFromType).collect(Collectors.toList());
    }
}
