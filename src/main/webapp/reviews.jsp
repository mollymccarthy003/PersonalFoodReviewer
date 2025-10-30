
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<jsp:include page="header.jsp">
    <jsp:param name="pageTitle" value="Reviews"/>
    <jsp:param name="page" value="reviews"/>
</jsp:include>
<body>
<div class="container-fluid">
    <h2>Review List</h2>

    <table id="reviewTable" class="display">
        <thead>
        <tr>
            <th>Restaurant Name</th>
            <th>Cuisine</th>
            <th>Personal Rating</th>
            <th>Personal Notes</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="review" items="${reviews}">
            <tr>
                <td>${review.restaurantName}</td>
                <td>${review.cuisineType}</td>
                <td>${review.personalRating}</td>
                <td>${review.personalNotes}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<script type="text/javascript" class="init">
    $(document).ready(function () {
        $('#reviewTable').DataTable();
    });
</script>
</body>
</html>
