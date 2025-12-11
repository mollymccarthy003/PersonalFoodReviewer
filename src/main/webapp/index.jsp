<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="header.jsp">
    <jsp:param name="pageTitle" value="Home"/>
    <jsp:param name="page" value="home"/>
</jsp:include>

<head>
    <title>Hungry Badger Food Reviewer</title>
</head>

<div class="container" style="padding:20px;">
    <h1>Welcome to the Hungry Badger Food Reviewer</h1>

    <p>
        Madison is home to incredible restaurants, food trucks, and cafes, but with so many options it's easy
        to forget where you had that perfect meal while balancing your busy lifestyle as a student.
        Trying to track your favorites on mainstream apps can be overwhelming, filled with ads,
        and don't focus on tracking personal taste.
    </p>
    <br>
    <p>
        Hungry Badger is a Personal Food Reviewer website designed for local Madison students.
        The site allows students to review restaurants quickly, see personal ratings, discover favorites,
        or document bad experiences. All your ratings of businesses are private and personal, like a diary but for food!
    </p>
    <br>
    <p>
        Not only is this a fun way to document your current favorites, but it is also a way to look back
        on your college experience and remember the meals that made it special.
    </p>
    <br>
</div>

<jsp:include page="footer.jsp" />

