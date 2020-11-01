package com.wintermute.applicationcreator.data;

import com.wintermute.applicationcreator.model.Applicant;
import com.wintermute.applicationcreator.model.complex.CategoryGroup;
import com.wintermute.applicationcreator.model.Contact;
import com.wintermute.applicationcreator.model.CoverLetter;
import com.wintermute.applicationcreator.model.Language;
import com.wintermute.applicationcreator.model.complex.Career;
import com.wintermute.applicationcreator.model.complex.Project;
import com.wintermute.applicationcreator.model.complex.Skill;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generates line entry with filled data which is passed into final document.
 */
public class DocumentContentParser
{

    /**
     * @param target data set ordered by category.
     * @param section to create new tex document syntax.
     * @param isTable to define whether the document entry is a table element.
     * @param <T> type of applicant data.
     * @return parsed data to document tex format.
     */
    <T> String getParsedContentGroupedByCategory(Map<CategoryGroup, List<T>> target, String section, boolean isTable)
    {
        StringBuilder result = new StringBuilder();
        target.forEach((category, content) ->
        {
            result.append(getParsedCustomSection(section, category.getTitle()));
            result.append(isTable ? getTableHeader() : "");
            result.append(getParsedListContent(content));
            result.append(isTable ? getTableFooter() + "\n" : "");
        });
        return result.toString();
    }

    /**
     * @param target list of languages.
     * @return parsed languages to tex document format.
     */
    String getParsedLanguages(List<Language> target)
    {
        StringBuilder result = new StringBuilder(getParsedCustomSection("Languages"));
        result.append(getTableHeader());
        target.forEach(l -> result.append(getParsedSingleItem(l.getLanguage(), l.getLevelDesc())));
        result.append(getTableFooter());
        return result.toString();
    }

    /**
     * @param hobbies hobbies as list of strings.
     * @return custom section for hobbies.
     */
    String getParsedHobbies(List<String> hobbies)
    {
        StringBuilder result =
            new StringBuilder(getParsedCustomSection("Hobbies")).append("\n\n\t\\commaseparatedlist{");
        extractItemsToParsedList(hobbies, result);
        return result.append("}").toString();
    }

    /**
     * @param applicantsPersonalData personal data of applicant.
     * @return preconfigured curriculum vitae header.
     */
    String getParsedHeader(Applicant.PersonalInfo applicantsPersonalData)
    {
        return applicantsPersonalData.getFirstName() + "\\\\" + applicantsPersonalData.getLastName() + "\\\\"
            + applicantsPersonalData.getJobTitle() + "}{pics/pic.jpg";
    }

    /**
     * @param target mapped skills divided into categories, enclosed into groups.
     * @return tex custom section containing table with (sorted) listed skills divided into groups.
     */
    String getParsedSkills(Map<CategoryGroup, Map<CategoryGroup, List<Skill>>> target)
    {
        StringBuilder result = new StringBuilder();
        target.forEach((ck, cv) ->
        {
            result.append(getParsedCustomSection("Skills", ck.getTitle())).append(getTableHeader());
            cv.forEach((gk, gv) -> result.append(
                getParsedNewLineList(gk.getTitle(), gv.stream().map(Skill::getTitle).collect(Collectors.toList()))));
            result.append(getTableFooter());
        });
        return result.toString();
    }

    /**
     * @param city of residence of applicant.
     * @return date header.
     */
    String getParsedCoverLetterHeader(String city)
    {
        return city + ", den " + LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
    }

    public String getParsedApplicant(Applicant.PersonalInfo applicant)
    {
        return "\\about{\\thinfont\\coverlist{\\faUser\\ \\coversender " + applicant.getFullName() + "}\n"
            + "{\\faMapMarker\\ \\small " + applicant.getContact().getAddress() + "}\n" + "{\\small \\faPhone\\ "
            + applicant.getContact().getPhoneNumber() + "}\n" + "{\\small \\faAt\\ " + applicant.getContact().getEmail()
            + "}\n" + (applicant.getContact().getWebsite() != null ? "{\\small \\faGithub\\" + applicant
            .getContact()
            .getWebsite() + "}" : "}") + "\n";
    }

    public String getParsedApplicantInfo(Contact applicantsContact)
    {
        return "\\faEnvelopeO\\/" + applicantsContact.getAddress() + " | " + "\\faMapMarker\\/ "
            + applicantsContact.getCityWithZipcode() + " | " + "\\faPhone\\/ " + applicantsContact.getPhoneNumber()
            + " | " + "\\faAt\\protect\\/ " + applicantsContact.getEmail() + "\n";
    }

    /**
     * @param recipient recipient data of application.
     * @return preconfigured recipient tex block.
     */
    String getParsedRecipient(CoverLetter.Recipient recipient)
    {
        return "\\receipient{\\coverlist{{\\normalsize\\bodyfont " + recipient.getCompany() + "}\n" + (
            recipient.getContactPerson() != null ? recipient.getContactPerson() : "") + "\n" + recipient
            .getContact()
            .getAddress() + "\n" + recipient.getContact().getCityWithZipcode();
    }

    private <T> String getParsedListContent(List<T> content)
    {
        StringBuilder result = new StringBuilder();
        Iterator<T> iterator = content.iterator();
        while (iterator.hasNext())
        {
            T next = iterator.next();
            if (next instanceof Project)
            {
                result.append(getParsedProject((Project) next)).append(iterator.hasNext() ? "\n" : "");
            } else if (next instanceof Career)
            {
                result.append(getParsedCareer((Career) next)).append(iterator.hasNext() ? "\n" : "");
            }
        }
        return result.toString();
    }

    private String getParsedProject(Project project)
    {
        StringBuilder result = new StringBuilder(getTableHeader())
            .append("\t\t")
            .append(getParsedActivity(project.getFrom() + " - " + project.getUntil(), project.getTitle()))
            .append("\\\\\n\t")
            .append(getParsedSingleItem("role", project.getRole()))
            .append("\t")
            .append(getParsedSingleItem("description", project.getDescription()));
        result.append(project.getProgrammingLanguages() != null ? getParsedCommaSeparatedList("languages",
            project.getProgrammingLanguages()) : "");
        result.append(
            project.getFrameworks() != null ? getParsedCommaSeparatedList("frameworks", project.getFrameworks()) : "");
        result.append(project.getTools() != null ? getParsedCommaSeparatedList("tools", project.getTools()) : "");
        result.append(project.getGithubLink() != null ? getParsedSingleItem("github", project.getGithubLink()) : "");
        return result.append(getTableFooter()).toString();
    }

    @NotNull
    private String getTableHeader()
    {
        return "\n\t\\begin{longtable}{p{11em}| p{25em}}\n";
    }

    @NotNull
    private String getTableFooter()
    {
        return "\n\t\\end{longtable}\n";
    }

    private String getParsedCareer(Career career)
    {
        StringBuilder result = new StringBuilder("\t\t").append(
            getParsedActivity(career.getFrom() + " - " + career.getUntil(), career.getTitle()));
        if (career.getJob() != null)
        {
            String s = result
                .
                    append("{")
                .append(career.getJob())
                .append("}{")
                .append(career.getDescription())
                .append("}\\\\")
                .toString();
            return s;
        }
        String s = result.append("{").append(career.getGraduation()).append("}\\\\").toString();
        return s;
    }

    private String getParsedCustomSection(String section, String... category)
    {
        return "\\customsection{" + section.substring(0, 3) + "}{" + section.substring(3) + (category.length > 0 ? " ("
            + category[0] + ")}" : "}");
    }

    private String getParsedActivity(String fieldHeader, String activity)
    {
        return "\\columntitle{" + fieldHeader + "} & \\activity{" + activity + "}";
    }

    private String getParsedSingleItem(String fieldHeader, String item)
    {
        return getParsedColumnSubTitle(fieldHeader) + " & \\singleitem{" + item + "}\\\\\n";
    }

    private String getParsedColumnSubTitle(String fieldHeader)
    {
        return "\t\\columnsubtitle{" + fieldHeader + "}";
    }

    private String getParsedNewLineList(String title, List<String> items)
    {
        return getParsedList(title, items, "newlinelist");
    }

    private String getParsedCommaSeparatedList(String title, List<String> items)
    {
        return getParsedList(title, items, "commaseparatedlist");
    }

    private String getParsedList(String title, List<String> items, String texListTag)
    {
        StringBuilder result = new StringBuilder("\t")
            .append(getParsedColumnSubTitle(title))
            .append(" & \\")
            .append(texListTag)
            .append("{");
        extractItemsToParsedList(items, result);
        return result.append("}\\\\\n").toString();
    }

    private void extractItemsToParsedList(List<String> items, StringBuilder result)
    {
        Iterator<String> iterator = items.iterator();
        while (iterator.hasNext())
        {
            result.append(iterator.next()).append(iterator.hasNext() ? "}{" : "");
        }
    }
}