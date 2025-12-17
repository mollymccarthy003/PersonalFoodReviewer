### Problem Statement:
Madison is home to incredible restaurants, food trucks, and cafes, but with so many options it's easy to forget where you had that perfect meal while balancing your busy lifestyle as a student. Trying to track your favorites on mainstream apps are overwhelming, filled with ads and don't focus on tracking personal taste. I'd like to build a Personal Food Reviewer website for local Madison students. The site will allow students to review resturants quickly, see personal ratings, and add photos to reviews, or document bad experiences. All your ratings of businesses are private and personal, like a diary but for food!!

Not only is this a fun way to document your current favorites, but its a way to look back on your college experience. 

### Live on Web

Check Hungry Badger out yourself: [https://hungrybadger.us-east-2.elasticbeanstalk.com/](https://hungrybadger.us-east-2.elasticbeanstalk.com/)

### Demo Video

[Demo Video on Youtube](https://www.youtube.com/watch?v=w3PSYqkwoq4)

### Technologies:

Database: MySQL 9.x

Dependency Management: Maven

CSS: Bootstrap/Custom

Logging: Log4J2

Hosting: AWS

Tech I'd like to explore: Bootstrap, Google Maps API, Hibernate Search

Unit Testing: JUnit 

IDE: IntelliJ IDEA

External API used: [https://icanhazdadjoke.com/api](https://icanhazdadjoke.com/api) For lighthearted content!

### POST MVP / V2 IDEAS:
- s3 for photo storage
- controller and authentication tests
- edit a review and photos in one page
- expand scope to ALL students anywhere in the US
- photos carosel on main page
- seperate Restaurant and Cuisine tables

## Notes
- Local uploads stored in `src/main/webapp/uploads`
- Production uploads stored in `/var/app/current/uploads` (managed via `.ebextensions`)
- Utility class automatically switches paths
- Images served through a servlet for consistency

## Quick Local Setup
1. Clone the repo:
   ```bash
   git clone <repo-url>
   cd PersonalFoodReviewer
   
2. Initialize the Database
   
CREATE DATABASE personal_food_reviewer;

SOURCE path/to/cleanDB.sql;

3. Build & Run
mvn clean install

Deploy WAR to local Tomcat or run via IDE

Visit: http://localhost:8080/PersonalFoodReviewer/

Deployment Notes

Deploy WAR file to AWS Elastic Beanstalk

Ensure .ebextensions config for uploads exists
   
### Design:

[Screen Designs](DesignDocuments/Screens.md)

[User Stories](DesignDocuments/UserStories.md)

### Project Plan

[Project Plan](ProjectPlan.md)

[Time Log](TimeLog.md)

[Change Log](Changelog.md)
