<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<jsp:include page="header.jsp">
    <jsp:param name="pageTitle" value="Search Results"/>
    <jsp:param name="page" value="reviews"/>
</jsp:include>
<body>
<div class="container-fluid">

    <h2>Search Results</h2>

    <!-- If no results found -->
    <c:if test="${empty reviews}">
        <div class="alert alert-info mt-3">
            No reviews found for your search.
        </div>
    </c:if>

    <!-- Loop over reviews and display each in a card -->
    <c:forEach var="review" items="${reviews}">
        <div class="card mb-3" style="padding: 20px; border: 1px solid #ccc;">
            <h3>${review.restaurantName}</h3>

            <p><strong>Cuisine:</strong> ${review.cuisineType}</p>
            <p><strong>Personal Rating:</strong> ${review.personalRating} / 5</p>
            <p><strong>Notes:</strong></p>
            <p>${review.personalNotes}</p>

            <!-- Action buttons -->
            <a href="reviews?id=${review.id}&action=details" class="btn btn-primary mb-2">View Details</a>
            <a href="reviews?id=${review.id}&action=edit" class="btn btn-warning mb-2">Edit</a>

            <form action="reviews" method="post" style="display:inline;">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="id" value="${review.id}">
                <button type="submit" class="btn btn-danger"
                        onclick="return confirm('Are you sure you want to delete this review?');">
                    Delete
                </button>
            </form>
        </div>
    </c:forEach>

    <br>
    <a href="reviews" class="btn btn-secondary">Back to All Reviews</a>

</div>
<jsp:include page="footer.jsp"/>
</body>
</html>
