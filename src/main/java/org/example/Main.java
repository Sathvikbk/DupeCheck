package org.example;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Reads a JSON file containing the Leads, deduplicates them, logs the changes outputs the result
 * to the onsole and to the log file.
 */
public class Main {

  /**
   * Main method to execute the deduplication process.
   * @throws Exception if file I/O or parsing fails.
   */
  public static void main(String[] args) throws Exception {
    String filePath = "/Users/sathvikbk/Documents/PDP/Lab/DupeCheck/src/main/resources/leads.json";
    File input = Paths.get(filePath).toFile();

    if(input.length() == 0){
      System.err.println("Empty File:"+ filePath);
      return;
    }

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());

    LeadWrapper wrapper;
    try {
      wrapper = mapper.readValue(input, LeadWrapper.class);
    } catch (JsonParseException e){
      System.err.println("Bad JSON" + e.getMessage());
      return;
    }

    List<Lead> leads = (wrapper == null) ? null : wrapper.getLeads();
    if(leads == null || leads.isEmpty()){
      System.out.println("No leads found");
      return;
    }

    LeadDeduplicator.Result result = LeadDeduplicator.deduplicate(wrapper.getLeads());

    LeadWrapper outWrapper = new LeadWrapper(result.getDeduped());
    String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(outWrapper);
    System.out.println("------- DEDUPED JSON------- \n" + prettyJson);

    StringBuilder logBuilder = new StringBuilder();
    logBuilder.append("------- DEDUPED JSON ------- \n").append(prettyJson).append("");
    logBuilder.append("\n------- CHANGE LOG ------- \n");
    System.out.println("\n------- CHANGE LOG ------- \n");
    result.getLog().forEach(entry -> {
      logBuilder.append(entry).append("-------------------------");
      System.out.print(entry + "---------------------------");
      System.out.println(entry);
      System.out.println("-----------------------------");
    });
    if (!result.getInvalidLogs().isEmpty()){
      logBuilder.append("\n------- INVALID LEADS --------\n");
      result.getInvalidLogs().forEach(System.out::println);
    }
    String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
    Path logPath = input.getParent() == null ? input.toPath().resolve("dedup_log" + timeStamp + ".txt"):
            input.getParentFile().toPath().resolve("dedup_log" + timeStamp + ".txt");
    Files.writeString(logPath, logBuilder.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    System.out.println("\nLogs written to:"+logPath.toAbsolutePath());
  }
}