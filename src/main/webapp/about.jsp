<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="header.jsp">
    <jsp:param name="pageTitle" value="About"/>
    <jsp:param name="page" value="about"/>
</jsp:include>
<head>
    <title>About Page</title>
    <link rel="stylesheet" href="css/main.css">
</head>
<body>
<div class="container">
    <h1>About Hungry Badger</h1>

    <p>
        Hi! My name is Molly McCarthy, and I'm the creator of Hungry Badger. As a student in Madison, I
        quickly realized how easy it is to forget the amazing meals you discover while juggling a busy college life.
        I wanted a way to personally track my favorite restaurants, food trucks, and cafes without getting lost
        in ads or overwhelming features on mainstream apps.
    </p>

    <p>
        Hungry Badger was born from this idea, a personal food reviewer for students, by a student, for my
        Enterprise Java class in Fall 2025. My goal is to provide a simple, fun, and private way for students to document their favorite eats,
        discover new spots, and even record the occasional disappointing meal.
    </p>

    <p>
        Thank you for checking out my website!
    </p>
</div>
<jsp:include page="footer.jsp" />