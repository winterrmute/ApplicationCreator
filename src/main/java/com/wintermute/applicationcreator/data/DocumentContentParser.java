package com.wintermute.applicationcreator.data;

import com.wintermute.applicationcreator.data.sort.RatingComparator;
import com.wintermute.applicationcreator.model.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

/**
 * Generates line entry with filled data which is passed into final document.
 */
public class DocumentContentParser {

    /**
     * @param target  data set ordered by category.
     * @param section to create new tex document syntax.
     * @param isTable to define whether the document entry is a table element.
     * @param <T>     type of applicant data.
     * @return parsed data to document tex format.
     */
    <T> StringBuilder getParsedContentGroupedByCategory(Map<CategoryGroup, List<T>> target, String section,
                                                        boolean isTable) {
        StringBuilder result = new StringBuilder();
        target.forEach((category, content) ->
        {
            result.append(getParsedCustomSection(section, category.getTitle()));
            result.append(isTable ? getTableHeader() : "");
            result.append(getParsedListContent(content));
            result.append(isTable ? "\n\\end{longtable}\n\n" : "");
        });
        return result;
    }

    /**
     * @param target list of languages.
     * @return parsed languages to tex document format.
     */
    String getParsedLanguages(List<Language> target) {
        StringBuilder result = new StringBuilder(getParsedCustomSection("Languages", ""));
        result.append(getTableHeader());
        target.forEach(l -> result.append(getParsedSingleItem(l.getLanguage(), l.getLevelDesc())));
        result.append("\n\\end{longtable}\n");
        return result.toString();
    }

    /**
     * @param hobbies hobbies as list of strings.
     * @return custom section for hobbies.
     */
    String getParsedHobbies(List<String> hobbies) {
        StringBuilder result = new StringBuilder("\\customsection{Hob}{bies}\n\n\\commaseparatedlist{");
        extractItemsToParsedList(hobbies, result);
        return result.append("}").toString();
    }

    /**
     * @param applicantsPersonalData personal data of applicant.
     * @return preconfigured curriculum vitae header.
     */
    String getParsedHeader(PersonalInfo applicantsPersonalData) {
        return applicantsPersonalData.getFirstName() + "\\\\" + applicantsPersonalData.getLastName() + "\\\\" + applicantsPersonalData.getJobTitle() + "}{pics/pic.jpg";
    }

    /**
     * @param target ordered skills by category.
     * @return parsed data to document tex format.
     */
    String getParsedSkills(Map<CategoryGroup, List<Skill>> target) {
        StringBuilder result = new StringBuilder();
        target.forEach((k, v) ->
        {
            v.sort(new RatingComparator().reversed());
            result.append(getParsedCustomSection("Skills", k.getTitle())).append("\n");
            Map<String, List<String>> orderedSkillsByInnerCategory = getOrderedSkillsByInnerCategory(v);
            result.append(getTableHeader());
            orderedSkillsByInnerCategory.forEach((k1, v1) ->
                    result.append(getParsedNewLineList(k1, v1)));
            result.append("\n\\end{longtable}");
        });
        return result.toString();
    }

    /**
     * @param city of residence of applicant.
     * @return date header.
     */
    String getParsedCoverLetterHeader(String city) {
        return city + ", den " + LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
    }

    public String getParsedApplicant(Applicant applicant) {
        return "\\about{\\thinfont\\coverlist{\\faUser\\ \\coversender " +
                applicant.getPersonalInfo().getFullName() + "}\n" +
                "{\\faMapMarker\\ \\small " + applicant.getContact().getAddress() + "}\n" +
                "{\\small \\faPhone\\ " + applicant.getContact().getPhoneNumber() + "}\n" +
                "{\\small \\faAt\\ " + applicant.getContact().getEmail() + "}\n" +
                (applicant.getContact().getWebsite() != null ?
                        "{\\small \\faGithub\\" + applicant.getContact().getWebsite() + "}" : "}") + "\n";
    }

    public String getParsedApplicantInfo(Contact applicantsContact) {
        return "\\faEnvelopeO\\/" + applicantsContact.getAddress() + " | " +
                "\\faMapMarker\\/ " + applicantsContact.getCityWithZipcode() + " | " +
                "\\faPhone\\/ " + applicantsContact.getPhoneNumber() + " | " +
                "\\faAt\\protect\\/ " + applicantsContact.getEmail() + "\n";
    }

    /**
     * @param recipient recipient data of application.
     * @return preconfigured recipient tex block.
     */
    String getParsedRecipient(Recipient recipient) {
        return "\\receipient{\\coverlist{{\\normalsize\\bodyfont " +
                recipient.getCompany() + "\n" +
                (recipient.getContactPerson() != null ? recipient.getContactPerson() : "") + "\n" +
                recipient.getContact().getAddress() + "\n" + recipient.getContact().getCityWithZipcode();
    }

    private Map<String, List<String>> getOrderedSkillsByInnerCategory(List<Skill> target) {
        Map<String, List<String>> skillsByInnerCategory = new HashMap<>();
        target.forEach(s ->
        {
            if (skillsByInnerCategory.get(s.getCategory()) == null) {
                skillsByInnerCategory.computeIfAbsent(s.getCategory(), r -> new ArrayList<>(List.of(s.getTitle())));
            } else {
                skillsByInnerCategory.get(s.getCategory()).add(s.getTitle());
            }
        });
        return skillsByInnerCategory;
    }

    private <T> String getParsedListContent(List<T> content) {
        StringBuilder result = new StringBuilder();
        Iterator<T> iterator = content.iterator();
        while (iterator.hasNext()) {
            T next = iterator.next();
            if (next instanceof Project) {
                result.append(getParsedProject((Project) next)).append(iterator.hasNext() ? "\n" : "");
            } else if (next instanceof Career) {
                result.append(getParsedCareer((Career) next)).append(iterator.hasNext() ? "\n" : "");
            }
        }
        return result.toString();
    }

    private String getParsedProject(Project project) {
        StringBuilder result = new StringBuilder(getTableHeader())
                .append(getParsedActivity(project.getFrom() + " - " + project.getUntil(), project.getTitle()))
                .append("\\\\")
                .append(getParsedSingleItem("position", project.getPosition()))
                .append(getParsedSingleItem("description", project.getDescription()));
        result.append(
                project.getGithubLink() != null ? getParsedCommaSeparatedList("github", List.of(project.getGithubLink()))
                        : "");
        result.append(project.getProgrammingLanguages() != null ? getParsedCommaSeparatedList("languages",
                project.getProgrammingLanguages()) : "");
        result.append(
                project.getFrameworks() != null ? getParsedCommaSeparatedList("frameworks", project.getFrameworks()) : "");
        result.append(project.getTools() != null ? getParsedCommaSeparatedList("tools", project.getTools()) : "");
        return result.append("\\end{longtable}\n\n").toString();
    }

    @NotNull
    private String getTableHeader() {
        return "\\begin{longtable}{p{11em}| p{25em}}\n";
    }

    private String getParsedCareer(Career career) {
        return getParsedActivity(career.getFrom() + " - " + career.getUntil(), career.getTitle()) + "{"
                + career.getDescription() + "}\\\\";
    }

    private String getParsedCustomSection(String section, String... category) {
        return "\\customsection{" + section.substring(0, 3) + "}{" + section.substring(3) + (category != null ? " ("
                + category[0] + ")}" : "}");
    }

    private String getParsedActivity(String fieldHeader, String activity) {
        return "\\columntitle{" + fieldHeader + "} & \\activity{" + activity + "}";
    }

    private String getParsedSingleItem(String fieldHeader, String item) {
        return getParsedColumnSubTitle(fieldHeader) + " & \\singleitem{" + item + "}\\\\\n";
    }

    private String getParsedColumnSubTitle(String fieldHeader) {
        return "\t\\columnsubtitle{" + fieldHeader + "}";
    }

    private String getParsedNewLineList(String title, List<String> items) {
        return getParsedList(title, items, "newlinelist");
    }

    private String getParsedCommaSeparatedList(String title, List<String> items) {
        return getParsedList(title, items, "commaseparatedlist");
    }

    private String getParsedList(String title, List<String> items, String texListTag) {
        StringBuilder result =
                new StringBuilder(getParsedColumnSubTitle(title)).append(" & \\").append(texListTag).append("{");
        extractItemsToParsedList(items, result);
        return result.append("}\\\\\n").toString();
    }

    private void extractItemsToParsedList(List<String> items, StringBuilder result) {
        Iterator<String> iterator = items.iterator();
        while (iterator.hasNext()) {
            result.append(iterator.next()).append(iterator.hasNext() ? "," : "");
        }
    }
}
