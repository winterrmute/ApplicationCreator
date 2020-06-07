package com.wintermute.applicationcreator.creator;

import com.wintermute.applicationcreator.applicationData.Applicant;
import com.wintermute.applicationcreator.applicationData.Career;
import com.wintermute.applicationcreator.applicationData.Language;
import com.wintermute.applicationcreator.applicationData.Project;
import com.wintermute.applicationcreator.applicationData.Skill;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Creates application curriculum vitae in latex from extracted data.
 *
 * @author wintermute
 */
public class CvCreator extends TexCreator
{

    private final Applicant applicant;

    public CvCreator(Applicant applicant)
    {
        this.applicant = applicant;
    }

    @Override
    public void create(File file)
    {
        File out = writeNewFile("cv");
        try (BufferedReader br = new BufferedReader(new FileReader(file)); FileWriter fw = new FileWriter(out))
        {
            String line;
            while ((line = br.readLine()) != null)
            {

                if (line.contains(":header"))
                {
                    line = writeIntoFile(line, ":header:}",
                        applicant.getPersonalInfo().getFirstName() + "\\\\" + applicant.getPersonalInfo().getLastName()
                            + "\\\\" + applicant.getPersonalInfo().getJobTitle() + "}{pics/pic.jpg}");
                }
                if (line.contains(":street:"))
                {
                    line = writeIntoFile(line, ":street:", applicant.getContact().getAddress());
                }
                if (line.contains(":city:"))
                {
                    line = writeIntoFile(line, ":city:",
                        applicant.getContact().getCityWithZipcode());
                }
                if (line.contains(":phonenumber:"))
                {
                    line = writeIntoFile(line, ":phonenumber:", applicant.getContact().getPhoneNumber());
                }
                if (line.contains(":email:"))
                {
                    line = writeIntoFile(line, ":email:", applicant.getContact().getEmail());
                }
                if (line.contains(":career:"))
                {
                    line = writeIntoFile(line, ":career:", "");
                    handleCareer(fw, "professionalCareer");
                }
                if (line.contains(":education:"))
                {
                    line = writeIntoFile(line, ":education:", "");
                    handleCareer(fw, "educationalCareer");
                }
                if (line.contains(":skills:"))
                {
                    for (String skillsCategory : applicant.getSkills().keySet())
                    {
                        Map<String, List<Skill>> skillsGroups = applicant.getSkills().get(skillsCategory);
                        fw.write("\\customsection{Ski}{lls (" + skillsCategory + ")}\n\n");
                        fw.write("\\begin{longtable}{p{11em}| p{25em}}\n\n");
                        for (String groupName : skillsGroups.keySet())
                        {
                            fw.write("\t\\columnsubtitle{" + groupName + "} & \\newlinelist");
                            for (Skill skill : skillsGroups.get(groupName))
                            {
                                fw.write("{" + skill.getName() + "}");
                            }
                            fw.write("\\\\\n\n");
                        }
                        fw.write("\\end{longtable}");
                    }
                    continue;
                }
                if (line.contains(":projects:"))
                {
                    line = writeIntoFile(line, ":projects:", "");
                    for (String projectType : applicant.getProjects().keySet())
                    {
                        List<String> projects = extractProject(projectType);
                        for (String entry : projects)
                        {
                            fw.write(entry);
                        }
                    }
                }
                if (line.contains(":languages:"))
                {
                    line = writeIntoFile(line, ":languages:", "");
                    for (Language language : applicant.getLanguages())
                    {
                        fw.write(
                            "\\columntitle{" + language.getLanguage() + "} & \\singleitem{" + language.getLevelDesc()
                                + "}\\\\\n");
                    }
                    fw.write("\\\\");
                }
                if (line.contains(":softSkills:"))
                {
                    line = writeIntoFile(line, ":softSkills:", "");
                    fw.write(buildCommaSeparatedList(applicant.getSoftSkills()));
                }
                if (line.contains(":hobbies:"))
                {
                    line = writeIntoFile(line, ":hobbies:", "");
                    fw.write(buildCommaSeparatedList(applicant.getHobbies()));
                }
                fw.write(line);
                fw.write("\n");
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void handleCareer(FileWriter fw, String careerType) throws IOException
    {
        List<String> career = extractCareer(careerType);
        for (String entry : career)
        {
            fw.write(entry);
        }
    }

    private List<String> extractProject(String projectType)
    {
        List<String> result = new ArrayList<>();
        StringBuilder input;
        for (Project project : applicant.getProjects().get(projectType))
        {
            input = new StringBuilder("\\begin{longtable}{p{11em}| p{25em} c}\n");
            input.append("\\columntitle{") //
                .append(project.getFrom()).append("-").append(project.getUntil()).append("} ") //
                .append("& \\activity{").append(project.getTitle()).append("}"); //
            input.append(project.getTitle().length() > 37 ? "\\\\\\\\" : "\\\\");

            if (!"".equals(project.getPosition()))
            {
                input.append(columnSubtitle("position", project.getPosition()));
            }

            input.append(columnSubtitle("description", project.getDescription()));

            if (project.getProgrammingLanguages() != null)
            {
                input.append(columnSubtitle("languages", project.getProgrammingLanguages()));
            }

            if (project.getFrameworks() != null)
            {
                input.append(columnSubtitle("frameworks", project.getFrameworks()));
            }

            if (project.getTools() != null)
            {
                input.append(columnSubtitle("tools", project.getTools()));
            }

            if (!"".equals(project.getGithubLink()) && project.getGithubLink() != null)
            {
                input.append(columnSubtitle("github Link", project.getGithubLink()));
            }
            input.append("\n").append("\\end{longtable}\n\n");

            result.add(input.toString());
        }

        return result;
    }

    private String columnSubtitle(String key, String value)
    {
        return new StringBuilder("\n\t\\columnsubtitle{").append(key).append("} & ") //
            .append("\\singleitem{").append(value).append("}\\\\").toString();
    }

    private String columnSubtitle(String key, List<String> listToFill)
    {
        return new StringBuilder("\n\t\\columnsubtitle{").append(key).append("} & ") //
            .append(buildCommaSeparatedList(listToFill)).append("\\\\").toString();
    }

    private String buildCommaSeparatedList(List<String> listToFill)
    {
        StringBuilder result = new StringBuilder("\\commaseparatedlist");
        listToFill.forEach(i -> result.append("{").append(i).append("}"));
        return result.toString();
    }

    private List<String> extractCareer(String careerType)
    {
        List<String> result = new ArrayList<>();
        StringBuilder input;
        for (Career career : applicant.getCareer().get(careerType))
        {
            input =
                new StringBuilder("\\columntitle{").append(career.getFrom()).append(" - ").append(career.getUntil()) //
                    .append("} & \\activity{").append(career.getTitle()).append("}");
            if ("professionalCareer".equals(careerType))
            {
                input.append("{").append(career.getJob()).append("}");
            }
            input.append("{").append(career.getDescription()).append("}\\\\").append("\n\n");
            result.add(input.toString());
        }
        return result;
    }
}
