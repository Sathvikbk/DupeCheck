package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.nio.file.Paths;

public class Main {
  public static void main(String[] args) throws Exception {
    String filePath = "/Users/sathvikbk/Downloads/leads.json";
    File input = Paths.get(filePath).toFile();

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());

    LeadWrapper wrapper = mapper.readValue(input, LeadWrapper.class);
    LeadDeduplicator.Result result = LeadDeduplicator.deduplicate(wrapper.getLeads());

    LeadWrapper outWrapper = new LeadWrapper(result.getDeduped());
    String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(outWrapper);
    System.out.println("===== DEDUPED JSON =====\n" + prettyJson);

    System.out.println("\n===== CHANGE LOG =====");
    result.getLog().forEach(entry -> {
      System.out.println(entry);
      System.out.println("-----------------------------");
    });
  }
}