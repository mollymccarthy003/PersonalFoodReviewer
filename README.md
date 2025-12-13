### Problem Statement:
Madison is home to incredible restaurants, food trucks, and cafes, but with so many options it's easy to forget where you had that perfect meal while balancing your busy lifestyle as a student. Trying to track your favorites on mainstream apps are overwhelming, filled with ads and don't focus on tracking personal taste. I'd like to build a Personal Food Reviewer website for local Madison students. The site will allow students to review resturants quickly, see personal ratings, and add photos to reviews, or document bad experiences. All your ratings of businesses are private and personal, like a diary but for food!!

Not only is this a fun way to document your current favorites, but its a way to look back on your college experience. 
### Technologies:

Database: MySQL 9.x

Dependency Management: Maven

CSS: Bootstrap/Custom

Logging: Log4J2

Hosting: AWS

Tech I'd like to explore: Bootstrap, Google Maps API, Hibernate Search

Unit Testing: JUnit 

IDE: IntelliJ IDEA

External API used: [https://icanhazdadjoke.com/api](https://icanhazdadjoke.com/api) I was inspired by a classmate to choose something
lighthearted to put on my website. I decided on dad jokes! I think this adds a great bit of whimsy to the site and makes it more casual.
My user story targets UW area students and I think this little bit of humor is a nice way to keep it light. My original plan was to use
my Team Project, Food Truck Restful API, but I had to pivot!

Post MVP ideas: comment on review, top-rated restaurant lists, most visited restuarant

Notes:For local development, uploaded images are stored in a local directory.
In production, Elastic Beanstalk stores uploads in /var/app/current/uploads, created via .ebextensions.
A utility class switches paths automatically, and images are served through a servlet for consistency.
### Design:

[Screen Designs](DesignDocuments/Screens.md)

[User Stories](DesignDocuments/UserStories.md)

### Project Plan

[Project Plan](ProjectPlan.md)

[Time Log](TimeLog.md)
