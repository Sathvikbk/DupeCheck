package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * The Lead class represents an individual lead, it contains 2 unique identifiers "id" and "email"
 * along with a timestamp.
 */
public class Lead {
  @JsonProperty("_id")
  private String id;
  private String email;
  private String firstName;
  private String lastName;
  private String address;
  private Instant entryDate;
  public Lead() {}

  /**
   * Parameterized constructor for creating a new lead object
   * @param id unique ID of the lead
   * @param email unique email of the lead
   * @param firstName first name of the lead
   * @param lastName last name of the lead
   * @param address address of the lead
   * @param entryDate timestamp of when the lead was added
   */
  public Lead(String id, String email, String firstName, String lastName, String address, Instant entryDate) {
    this.id = id;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.address = address;
    this.entryDate = entryDate;
  }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }

  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }

  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }

  public Instant getEntryDate() { return entryDate; }
  public void setEntryDate(Instant entryDate) { this.entryDate = entryDate; }

  /**
   * Retuers a string representations of the lead, used for logging.
   * @return a string with lead details
   */
  @Override
  public String toString() {
    return "Lead{" +
            "id='" + id + '\'' +
            ", email='" + email + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", address='" + address + '\'' +
            ", entryDate=" + entryDate +
            '}';
  }
}
