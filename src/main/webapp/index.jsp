<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<jsp:include page="header.jsp">
    <jsp:param name="pageTitle" value="Home"/>
    <jsp:param name="page" value="home"/>
</jsp:include>
<head>
    <title>Hungry Badger</title>
</head>
<body>
<div class="container">
    <h1>Welcome to Hungry Badger</h1>
    <p>Browse all your reviews:</p>

    <    <a href="${pageContext.request.contextPath}/reviews" class="button">View Reviews</a>

</div>
</body>
</html>
