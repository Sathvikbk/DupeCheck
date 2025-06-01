package org.example;

import java.util.List;

public class LeadWrapper {
  private List<Lead> leads;
  public LeadWrapper() {}
  public LeadWrapper(List<Lead> leads) { this.leads = leads; }
  public List<Lead> getLeads() { return leads; }
  public void setLeads(List<Lead> leads) { this.leads = leads; }
}
