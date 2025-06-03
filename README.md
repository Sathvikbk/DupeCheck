## 🤖 DEDUPEINATOR
Cleaning up messy lead data? Say hello to DEDUPEINATOR — your friendly lead-scrubbing sidekick!

This tool zaps duplicate records by comparing id and email, keeping only the freshest one based on entryDate. No more worrying about cluttered, conflicting, or outdated leads — DEDUPEINATOR's got it covered.

It handles the hard stuff too:

- Missing values? 

- Weird date formats? 

- Tricky whitespace and case mismatches? 

- Unicode look-alikes that trick the eye? 

And it doesn’t just clean silently — it leaves a detailed audit trail so you always know what was removed and why.

📝 Assumptions
Here’s what DEDUPEINATOR expects:

Input data is clean (IDs and emails are in a standard format)

Order matters — leads are processed in the sequence they appear

Deduplication compares by id first, then email

Input must be a valid .json file

Leads with missing or null values in any field are rejected and logged as invalid

## How It Works
Scans for duplicates using:

Matching id

Matching email (case-insensitive and trimmed)

Keeps only the most recent entry based on entryDate

Skips anything with null or empty fields (and logs them under "Removed Leads")

Doesn’t use transitive logic — only direct duplicate matching is applied

All output is logged in a friendly, readable format for audit or review


## File Setup
The path to leads.json is hardcoded to the program’s resource folder.
Want to use your own file? Just update the path in Main.java.

## Features at a Glance
- Deduplicates using id and email

- Keeps the latest record using entryDate

- Handles all sorts of edge cases

- Logs everything: changes, removals, and invalid entries

- Outputs clean, deduplicated JSON

- Comes with full JUnit test suite for validation

- Time and space efficient: O(n)

Ready to turn data chaos into clean, deduplicated bliss?
DEDUPEINATOR is just a run away.
