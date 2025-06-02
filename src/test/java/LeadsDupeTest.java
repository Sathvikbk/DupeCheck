import org.example.Lead;
import org.example.LeadDeduplicator;
import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LeadsDupeTest {
  private static Lead lead(String id, String email, String first, String last, String address, Instant entryTime) {
    return new Lead(id, email, first, last, address, entryTime);
  }

  private static LeadDeduplicator.Result run(Lead... leads) {
    return LeadDeduplicator.deduplicate(List.of(leads));
  }

  /**
   * Same Id, unique email -> one with later time wins
   */
  @Test
  void duplicateId() {

    //Arrange
    Lead a = new Lead("abc", "abc1@email.com", "A", "X", "address1", Instant.parse("2024-05-07T17:30:20Z"));
    Lead b = new Lead("abc", "abc2@email.com", "B", "Y", "address2", Instant.parse("2024-05-07T17:32:20Z"));
    Lead c = new Lead("abc", "abc3@email.com", "C", "Z", "address3", Instant.parse("2024-05-07T17:28:20Z"));

    //Act
    LeadDeduplicator.Result res = run(a, b, c);

    //Assert
    assertEquals(1, res.getDeduped().size(), 1);
    assertEquals(b, res.getDeduped().get(0));
    assertEquals(2, res.getLog().size());
  }

  /**
   * Same Email unique id -> one with later time wins
   */
  @Test
  void duplicateEmail_uniqueId(){

    //Arrange
    Lead a = new Lead("abcc", "abc@email.com", "A", "X", "address1", Instant.parse("2024-05-07T17:30:20Z"));
    Lead b = new Lead("abcd", "abc@email.com", "B", "Y", "address2", Instant.parse("2024-05-07T17:32:20Z"));
    Lead c = new Lead("abca", "abc@email.com", "C", "Z", "address3", Instant.parse("2024-05-07T17:28:20Z"));

    //Act
    LeadDeduplicator.Result res = run(a,b,c);

    //Assert
    assertEquals(1, res.getDeduped().size());
    assertEquals(b, res.getDeduped().get(0));
    assertEquals(2, res.getLog().size());
  }

  /**
   * Same Email -> one with later time wins
   */
  @Test
  void duplicateEmailChain(){

    //Arrange
    Lead a = new Lead("abcc", "abc@email.com", "A", "X", "address1", Instant.parse("2024-05-07T17:30:20Z"));
    Lead b = new Lead("abcd", "abc@email.com", "B", "Y", "address2", Instant.parse("2024-05-07T17:32:20Z"));
    Lead c = new Lead("abca", "abc@email.com", "C", "Z", "address3", Instant.parse("2024-05-07T17:28:20Z"));
    Lead d = new Lead("abc", "abc@email.com", "D", "Z", "address3", Instant.parse("2024-05-07T17:37:20Z"));

    //Act
    LeadDeduplicator.Result res = run(a,b,c,d);

    //Assert
    assertEquals(1, res.getDeduped().size());
    assertEquals(d, res.getDeduped().get(0));
    assertEquals(3, res.getLog().size());
  }

  /**
   * Same Id -> one with later time wins
   */
  @Test
  void duplicateIdChain(){

    //Arrange
    Lead a = new Lead("abc", "abc1@email.com", "A", "X", "address1", Instant.parse("2024-05-07T17:30:20Z"));
    Lead b = new Lead("abc", "abc2@email.com", "B", "Y", "address2", Instant.parse("2024-05-07T17:32:20Z"));
    Lead c = new Lead("abc", "abc3@email.com", "C", "Z", "address3", Instant.parse("2024-05-07T17:28:20Z"));
    Lead d = new Lead("abc", "abc4@email.com", "D", "Z", "address3", Instant.parse("2024-05-07T17:29:20Z"));

    //Act
    LeadDeduplicator.Result res = run(a,b,c,d);

    //Assert
    assertEquals(1, res.getDeduped().size());
    assertEquals(b, res.getDeduped().get(0));
    assertEquals(3, res.getLog().size()) ;
  }

  /**
   * Chain of duplicates with either Email or Id
   */
  @Test
  void duplicateChain(){

    //Arrange
    Lead a = new Lead("abc", "abc1@email.com", "A", "X", "address1", Instant.parse("2024-05-07T17:30:20Z"));
    Lead b = new Lead("abc", "abc2@email.com", "B", "Y", "address2", Instant.parse("2024-05-07T17:32:20Z"));
    Lead c = new Lead("abcd", "abc2@email.com", "C", "Z", "address3", Instant.parse("2024-05-07T17:28:20Z"));
    Lead d = new Lead("abce", "abc4@email.com", "D", "Z", "address3", Instant.parse("2024-05-07T17:29:20Z"));

    //Act
    LeadDeduplicator.Result res = run(a,b,c,d);

    //Assert
    assertEquals(2, res.getDeduped().size());
    assertEquals(2, res.getLog().size());
    assertEquals(true, res.getDeduped().contains(b));
    assertEquals(true, res.getDeduped().contains(d));
  }

  /**
   * all uniqe records
   */
  @Test
  void uniqueRecords(){

    //Arrange
    Lead a = new Lead("a", "abc1@email.com", "A", "X", "address1", Instant.parse("2024-05-07T17:30:20Z"));
    Lead b = new Lead("ab", "abc2@email.com", "B", "Y", "address2", Instant.parse("2024-05-07T17:32:20Z"));
    Lead c = new Lead("abc", "abc3@email.com", "C", "Z", "address3", Instant.parse("2024-05-07T17:28:20Z"));
    Lead d = new Lead("abcd", "abc4@email.com", "D", "Z", "address4", Instant.parse("2024-05-07T17:29:20Z"));

    //Act
    LeadDeduplicator.Result res = run(a,b,c,d);

    //Assert
    assertEquals(4, res.getDeduped().size());
    assertEquals(0, res.getLog().size());
    assertEquals(true, res.getDeduped().contains(a));
    assertEquals(true, res.getDeduped().contains(b));
    assertEquals(true, res.getDeduped().contains(c));
    assertEquals(true, res.getDeduped().contains(d));

  }

  /**
   * Time stamps change
   */
  @Test
  void timeStamps(){

    //Arrange
    Lead a = new Lead("abc", "abc1@email.com", "A", "X", "address1", Instant.parse("2024-05-07T17:30:20Z"));
    Lead b = new Lead("abc", "abc2@email.com", "B", "Y", "address2", Instant.parse("2024-05-07T17:32:20Z"));
    Lead c = new Lead("abc", "abc3@email.com", "C", "Z", "address3", Instant.parse("2024-05-07T17:28:20Z"));

    //Act
    LeadDeduplicator.Result res = run(a,b,c);

    //Assert
    assertEquals(1, res.getDeduped().size());
    assertEquals(b, res.getDeduped().get(0));
    assertEquals(2, res.getLog().size());

  }

  /**
   * Same date different time
   */
  @Test
  void sameDateDiffTime(){

    //Arrange
    Lead a = new Lead("abc", "abc1@email.com", "A", "X", "address1", Instant.parse("2024-05-07T17:30:20Z"));
    Lead b = new Lead("abcd", "abc2@email.com", "B", "Y", "address2", Instant.parse("2024-05-07T17:32:20Z"));
    Lead c = new Lead("abc", "abc3@email.com", "C", "Z", "address3", Instant.parse("2024-05-07T17:28:20Z"));

    //Act
    LeadDeduplicator.Result res = run(a,b,c);

    //Assert
    assertEquals(2, res.getDeduped().size());
    assertEquals(a, res.getDeduped().get(0));
    assertEquals(b, res.getDeduped().get(1));
    assertEquals(1, res.getLog().size());

  }

  /**
   * Null value for one value
   */
  @Test
  void nullValue(){

    //Arrange
    Lead a = new Lead("1asd", "abc4@email.com", "A", "X", "address1", null);
    Lead b = new Lead("1asd", "abc4@email.com", "B", "Y", "address2", Instant.parse("2025-01-01T00:00:00Z"));

    //Act
    LeadDeduplicator.Result res = run(a,b);

    //Assert
    assertEquals(1, res.getDeduped().size());
    assertEquals(b,res.getDeduped().get(0));
    assertTrue(res.getInvalidLogs().get(0).contains("null"));

  }

  /**
   * Wrong Format for entry stamp
   */
  @Test
  void wrongEntryFormat(){
    assertThrows(DateTimeParseException.class, ()->{
      Lead a = new Lead("2ea", "abc@email.com", "A", "X", "address1", Instant.parse("05/30/2025"));
    });
  }

  /**
   * New record has a null value
   */
  @Test
  void nullValueInSecondEntry(){

    //Arrange
    Lead a = new Lead("2saf", "abc@email.com", "A", "X", "address1", Instant.parse("2025-01-01T10:00:00Z"));
    Lead b = new Lead("2saf", "abc@email.com", null, "Y", "address2", Instant.parse("2025-01-02T10:00:00Z"));

    //Act
    LeadDeduplicator.Result res = run(a,b);

    //Assert
    assertEquals(1, res.getDeduped().size());
    assertTrue(res.getInvalidLogs().get(0).contains("null"));
  }

  /**
   * New record has multiple null records
   */
  @Test
  void nullValues(){

    //Arrange
    Lead a = new Lead("11", "abc@email.com", "A", "B", "address1", Instant.parse("2025-01-01T10:00:00Z"));
    Lead b = new Lead("12", "abc@email.com", null, null, "address2", Instant.parse("2025-01-02T10:00:00Z"));

    //Act
    LeadDeduplicator.Result res = run(a,b);

    //Assert
    assertEquals(1, res.getDeduped().size());
    assertTrue(res.getInvalidLogs().get(0).contains("null"));
  }

  /**
   * case sensitve email
   */
  @Test
  void caseSensitive(){

    //Arrange
    Lead a = new Lead("wrwefr", "abc@email.com", "A", "X", "address1", Instant.parse("2025-01-01T10:00:00Z"));
    Lead b = new Lead("ewefd", "ABC@email.com", "B", "Y", "address2", Instant.parse("2025-01-02T10:00:00Z"));

    //Act
    LeadDeduplicator.Result res = run(a,b);

    //Assert
    assertEquals(1, res.getDeduped().size());
    assertEquals(b,res.getDeduped().get(0));

  }

  /**
   * White space in Email
   */
  @Test
  void whiteSpace(){

    //Arrange
    Lead a = new Lead("af", " abc@email.com ", "A", "X", "address1", Instant.parse("2025-01-01T10:00:00Z"));
    Lead b = new Lead("af", "abc@email.com", "B", "Y", "address2", Instant.parse("2025-01-02T10:00:00Z"));

    //Act
    LeadDeduplicator.Result res = run(a,b);

    //Assert
    assertEquals(1, res.getDeduped().size());
    assertEquals(b,res.getDeduped().get(0));
  }

}
