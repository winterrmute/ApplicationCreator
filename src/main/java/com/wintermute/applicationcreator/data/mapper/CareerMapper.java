package com.wintermute.applicationcreator.data.mapper;

import com.wintermute.applicationcreator.model.Career;
import com.wintermute.applicationcreator.model.CategoryGroup;

import java.util.List;
import java.util.Map;

/**
 * Organizes career by category and time period.
 *
 * @author wintermute
 */
public class CareerMapper extends CategoryMapper
{
    public Map<CategoryGroup, List<Career>> getCareerForApplicant(Map<String, Map<String, Object>> data)
    {
        return getCategoryGroupForCategory(data, this::getCareer);
    }

    private Career getCareer(Map<String, Object> careerInfo)
    {
        boolean isSchool = careerInfo.get("school") != null;
        Career result = new Career();
        result.setTitle(isSchool ? careerInfo.get("school").toString() : careerInfo.get("company").toString());
        result.setDescription(
            isSchool ? careerInfo.get("graduation").toString() : careerInfo.get("description").toString());
        result.setFrom(careerInfo.get("from").toString());
        result.setUntil(careerInfo.get("until").toString());
        if (!isSchool)
        {
            result.setJob(careerInfo.get("job").toString());
        }
        return result;
    }
}
