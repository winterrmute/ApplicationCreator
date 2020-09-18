package com.wintermute.applicationcreator.data;

import com.wintermute.applicationcreator.datamodel.Career;
import com.wintermute.applicationcreator.datamodel.Language;
import com.wintermute.applicationcreator.datamodel.Project;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generates description how to handle tags which should be replaced through generated document parts.
 *
 * @author wintermute
 */
public class ReplaceTagHandlerGenerator {

    private final DocumentContentParser contentParser;

    public ReplaceTagHandlerGenerator() {
        contentParser = new DocumentContentParser();
    }

    /**
     * @param target information to replace the placeholder.
     * @return function replacing the placeholder with provided data.
     */
    public Function<String, String> createInlineEntry(String target) {
        return s -> s.replace(s, target);
    }

    /**
     * @param target list of languages.
     * @return function replacing the placeholder with provided data.
     */
    public Function<String, String> createLanguageEntries(List<Language> target) {
        List<String> languageEntry = target.stream().map(l -> "\n\t\\columnsubtitle{" + l.getLanguage() + "} & " +
                "\\singleitem{" + l.getLevelDesc() + "}\\\\").collect(Collectors.toList());

        return createMultiLineEntry(languageEntry);
    }

    public Function<String, String> createProjectEntries(List<Project> target) {
        List<String> projects = target.stream().map(contentParser::getProject).collect(Collectors.toList());
        return createMultiLineEntry(projects);
    }

    public Function<String, String> createCareerEntries(List<Career> target) {
        List<String> careers = target.stream().map(contentParser::getCareer).collect(Collectors.toList());
        return null;
    }

    private Function<String, String> createMultiLineEntry(List<String> target) {
        StringBuilder result = new StringBuilder();
        return s -> {
            target.forEach(t -> result.append(s).append("\n"));
            return result.toString();
        };
    }

}
