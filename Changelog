# Changelog
## Updates I Made from Instructer and Peer Code Reviews

## Checkpoint 1 - Setup and Project Repo

### Clarifying Audience & Scope
- **Feedback:** Clarify whether the application is location-specific or intended for a broader audience.
- **Action Taken:** Updated user stories and problem statement to reflect an audience of Madison WI students.

### API Integration Requirement
- **Feedback:** Ensure that consuming an external API is included in the project scope.
- **Action Taken:** Integrated the icanhazdadjoke API to provide dynamic, server-side API consumption and display within the application.

### Database Relationships
- **Feedback:** Ensure multiple one-to-many relationships exist in the database design.
- **Action Taken:** Implemented and documented the following relationships:
  - User → Review (one-to-many)
  - Review → Photo (one-to-many, with cascade and orphan removal)

### Authentication & User Management
- **Feedback:** AWS handles sign-up/sign-in; custom password handling is unnecessary.
- **Action Taken:** Integrated AWS Cognito for authentication and removed the need to store passwords in the database. Users are persisted on first successful login.

### Planning & Task Breakdown
- **Feedback:** Expand the project plan with more granular implementation details.
- **Action Taken:** Project plan gets more detailed as time goes on. Weeks 1-9 was setup and weekly exercises. 9-13 was team project and 14-15 was the time I spent honing in on my project.

### Tooling & Workflow
- **Feedback:** Consider using GitHub Projects or Issues for task tracking.
- **Action Taken:** Used paper planning and project planning document. I also looked back on the issues made from instructors and peers.

### Wireframes & User Stories
- **Feedback:** Ensure wireframes map directly to user stories.
- **Action Taken:** Wireframes were validated against implemented flows (login, review listing, detail view, creation, editing, deletion, and search).

## Checkpoint 2 – Database & DAO Feedback 

### Database Structure & Relationships
- **Feedback:** Database design appears to show only a single one-to-many relationship. Confirm presence of a user table and add additional relationships to meet project requirements.
- **Action Taken:** Implemented and documented the following relationships:
  - User → Review (one-to-many)
  - Review → Photo (one-to-many with cascade delete and orphan removal)
- **Result:** Database meets the requirement for multiple one-to-many relationships and supports user-scoped data ownership.

### Entity & Table Completeness
- **Feedback:** Question raised about whether key tables (such as User) were missing from the design.
- **Action Taken:** Added and mapped a User entity tied to Reviews via a foreign key and persisted users on first successful Cognito login.

### Data Normalization Considerations
- **Feedback:** Suggested separating restaurant and cuisine data into their own tables to avoid duplication.
- **Action Taken:** Evaluated trade-offs and intentionally scoped Restaurant and Cuisine tables as **V2 enhancements** to keep MVP focused while meeting relational requirements.

### DAO Implementation & Testing
- **Feedback:** Ensure at least one DAO supports full CRUD operations and is fully unit tested.
- **Action Taken:** Implemented a reusable GenericDao with full CRUD support and created unit tests for User, Review, and Photo entities.

### Cascade & Deletion Behavior
- **Feedback:** Good test coverage for cascading deletes from Review → Photo; question raised about behavior when deleting a Photo.
- **Action Taken:** Verified Photo deletion does not affect the associated Review and added/confirmed unit test coverage to validate this behavior.

### Logging Standards
- **Feedback:** Replace remaining printStackTrace calls with proper logging and ensure no System.out.println statements remain.
- **Action Taken:** Refactored exception handling in PropertiesLoader to use Log4j and verified the project contains no System.out.println usage.

### Test Verification Artifacts
- **Feedback:** Include visual confirmation that DAO unit tests pass.
- **Action Taken:** Added screenshots of successful DAO test runs to the repository for reviewer verification.

## Austin S Code Review Notes

### Security & Repository Hygiene
- **Feedback:** Sensitive information is exposed and database files were still present in the repository.
- **Action Taken:** Removed database artifacts from version control and confirmed `.gitignore` rules properly exclude environment-specific files going forward.

## Checkpoint 3 - Cognito AWS

### Inline Styling in JSPs
- **Feedback:** Large amounts of CSS are written directly in JSP files (e.g., `header.jsp`), making styling harder to maintain and reuse.
- **Action Taken: **
  - Moved inline `<style>` blocks into dedicated CSS files under `/css`.
  - Linked shared stylesheets across JSPs for consistency.

### Java Code Embedded in JSPs
- **Feedback:** Java logic is embedded directly in JSPs (e.g., `reviewForm.jsp`), which hurts maintainability.
- **Action Taken:**
  - Refactored JSPs to use Expression Language (EL) only.
  - Moved request-processing logic into Servlets or Controllers.
  - Keep JSPs strictly for presentation.

### Sensitive Information in Properties Files
- **Feedback:** Secrets and credentials (client secrets, database username/password) are committed to the repository.
- **Action Taken:**
  - Removed sensitive values from version control.
  - Used environment variables or external configuration files ignored by Git.
  - Updated `.gitignore` to prevent accidental commits of secrets.

### Use of `X-Forwarded-Proto` Header
- **Feedback:** The purpose of using `X-Forwarded-Proto` in `Auth.java` is unclear.
- **Action Taken:**
  - Documented why this header is required with a comment in code

### Missing JavaDoc
- **Feedback:** Several classes and methods (including tests) are missing JavaDoc comments.
- **Action Taken:**
  - Added JavaDoc to all public classes and methods.
  - Included documentation for test classes explaining test purpose and coverage.
  - Ensured documentation aligns with project standards.

### Empty `contextDestroyed` Method
- **Feedback:** The `contextDestroyed` method exists but performs no cleanup.
- **Action Taken:**
  - Documented its existence and why it is empty.

### Environment Variable Usage in Elastic Beanstalk
- **Feedback:** Environment variable loading was used but not explained.
- **Action Taken:**
  - Added documentation explaining deployment-specific configuration.

### Missing Unit Tests for `UserDao`
- **Feedback:** Unit tests appear to be missing for `UserDao`, especially around entity relationships and cascade behavior.
- **Action Taken:**
  - Added unit tests for `UserDao`.
  - Tested cascade behavior (e.g., deleting a user and related entities).
  - Verified that relationship rules behave as intended.

## Nick Hanson Code Review Notes

### Environment-Based Configuration
- **Feedback:** Replace hardcoded cloud/S3 placeholders with environment-based configuration.
- **Action Taken:** Changed all s3 placeholders to handle images in EB.

### Photo Upload Pipeline
- **Feedback:** Complete the photo upload pipeline (multipart form → storage → metadata persistence) or clearly scope it as post-MVP.
- **Action Taken:** Fully implemented the photo upload pipeline for MVP.

### User Feedback on Forms
- **Feedback:** Add user-facing success and error messages for all form submissions.
- **Action Taken:** Implemented clear, concise feedback messages on create/delete operations for reviews and photos.

### Entity Collection Initialization
- **Feedback:** Initialize entity collections (e.g., photos) to prevent null pointer issues in JSP rendering.
- **Action Taken:** Initialized lists and collections in entity constructors or use null-safe getters to prevent runtime exceptions.

### Controller JavaDoc
- **Feedback:** Add concise Javadoc to controller methods.
- **Action Taken:** Documented all controllers and methods, including parameter explanations and return values, for maintainability.

### Validation & Error Handling
- **Feedback:** Centralize validation and error handling to reduce duplication.
- **Action Taken:** Created shared validation and exception-handling utilities to standardize input checking and logging.

### Service Layer Separation
- **Feedback:** Move cloud/storage logic out of controllers into a dedicated service layer.
- **Action Taken:** Refactored S3 and file-handling logic into separate service classes to improve cohesion and testability.

### Deployment Documentation
- **Feedback:** Add the live AWS deployment URL and basic deployment steps to the README.
- **Action Taken:** Updated README with AWS Elastic Beanstalk deployment URL and step-by-step instructions for future verification.

### EB Configuration Documentation
- **Feedback:** Document the purpose of `.platform` and `.ebextensions` configuration files.
- **Action Taken:** Included a brief description in README explaining how EB configurations customize the environment.

### Project Tracking
- **Feedback:** Create a simple `CHANGELOG.md` to track project updates and decisions.
- **Action Taken:** Added a `CHANGELOG.md` listing feedback, changes made, and reasoning for transparency and traceability.
