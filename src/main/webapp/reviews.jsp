<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="header.jsp">
    <jsp:param name="pageTitle" value="My Reviews"/>
    <jsp:param name="page" value="reviews"/>
</jsp:include>
<body>
<div class="container-fluid">
    <h2>My Reviews</h2>
        <a href="reviewForm.jsp?action=add" class="btn btn-primary mb-3">Add New Review</a>
        <br><br>
        <table id="reviewTable" class="display table table-striped table-bordered">
            <thead>
            <tr>
                <th>Restaurant Name</th>
                <th>Cuisine</th>
                <th>Personal Rating</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="review" items="${reviews}">
                <tr>
                    <td>
                        <!-- Link to single review page -->
                        <a href="reviews?id=${review.id}">${review.restaurantName}</a>
                    </td>
                    <td>${review.cuisineType}</td>
                    <td>${review.personalRating}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
</div>

<script type="text/javascript" class="init">
    $(document).ready(function () {
        $('#reviewTable').DataTable({
            "order": [[0, "asc"]]
        });
    });
</script>
<jsp:include page="footer.jsp"/>
</body>
</html>