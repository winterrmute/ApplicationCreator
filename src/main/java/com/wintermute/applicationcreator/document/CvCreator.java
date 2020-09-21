package com.wintermute.applicationcreator.document;

import com.wintermute.applicationcreator.model.*;

import java.io.*;
import java.util.List;

/**
 * Creates application curriculum vitae in latex from extracted data.
 *
 * @author wintermute
 */
public class CvCreator extends DocumentCreator {

    private final Applicant applicant;

    public CvCreator(Applicant applicant) {
        this.applicant = applicant;
    }

    @Override
    public void createDocument(File template) {
        File out = writeNewFile("cv");
        try (BufferedReader br = new BufferedReader(new FileReader(template)); FileWriter fw = new FileWriter(out)) {
            String line;
            while ((line = br.readLine()) != null) {

                if (line.contains(":header:")) {
                    line = getPreparedDocumentPart(line, ":header:}",
                            applicant.getPersonalInfo().getFirstName() + "\\\\" + applicant.getPersonalInfo().getLastName()
                                    + "\\\\" + applicant.getPersonalInfo().getJobTitle() + "}{pics/pic.jpg}");
                }
                if (line.contains(":street:")) {
                    line = getPreparedDocumentPart(line, ":street:", applicant.getContact().getAddress());
                }
                if (line.contains(":city:")) {
                    line = getPreparedDocumentPart(line, ":city:",
                            applicant.getContact().getCityWithZipcode());
                }
                if (line.contains(":phonenumber:")) {
                    line = getPreparedDocumentPart(line, ":phonenumber:", applicant.getContact().getPhoneNumber());
                }
                if (line.contains(":email:")) {
                    line = getPreparedDocumentPart(line, ":email:", applicant.getContact().getEmail());
                }
//                if (line.contains(":skills:")) {
//                    for (String skillsCategory : applicant.getSkills().keySet()) {
//                        Map<String, List<Skill>> skillsGroups = applicant.getSkills().get(skillsCategory);
//                        fw.write("\\customsection{Ski}{lls (" + skillsCategory + ")}\n\n");
//                        fw.write("\\begin{longtable}{p{11em}| p{25em}}\n\n");
//                        for (String groupName : skillsGroups.keySet()) {
//                            fw.write("\t\\columnsubtitle{" + groupName + "} & \\newlinelist");
//                            for (Skill skill : skillsGroups.get(groupName)) {
//                                fw.write("{" + skill.getName() + "}");
//                            }
//                            fw.write("\\\\\n\n");
//                        }
//                        fw.write("\\end{longtable}");
//                    }
//                    continue;
//                }
                if (line.contains(":languages:")) {
                    line = getPreparedDocumentPart(line, ":languages:", "");

                    for (Language language : applicant.getLanguages()) {
                        fw.write(columnSubtitle(language.getLanguage(), language.getLevelDesc()));
                    }
                }
//                if (line.contains(":softSkills:")) {
//                    line = getPreparedDocumentPart(line, ":softSkills:", "");
//                    fw.write(buildCommaSeparatedList(applicant.getSoftSkills()));
//                }
                if (line.contains(":hobbies:")) {
                    line = getPreparedDocumentPart(line, ":hobbies:", "");
                    fw.write(buildCommaSeparatedList(applicant.getHobbies()));
                }
                fw.write(line);
                fw.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String columnSubtitle(String key, String value) {
        return "\n\t\\columnsubtitle{" + key + "} & " + "\\singleitem{" + value + "}\\\\";
    }

    private String columnSubtitle(String key, List<String> listToFill) {
        return new StringBuilder("\n\t\\columnsubtitle{").append(key).append("} & ") //
                .append(buildCommaSeparatedList(listToFill)).append("\\\\").toString();
    }

    private String buildCommaSeparatedList(List<String> listToFill) {
        StringBuilder result = new StringBuilder("\\commaseparatedlist");
        listToFill.forEach(i -> result.append("{").append(i).append("}"));
        return result.toString();
    }
}
