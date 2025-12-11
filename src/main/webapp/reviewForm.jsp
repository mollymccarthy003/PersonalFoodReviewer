<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Set default "action" if missing -->
<c:set var="action" value="${empty param.action ? 'add' : param.action}" />

<html>
<jsp:include page="header.jsp">
    <jsp:param name="pageTitle" value="${action == 'edit' ? 'Edit Review' : 'Add Review'}"/>
    <jsp:param name="page" value="reviews"/>
</jsp:include>

<body>
<div class="container-fluid mt-4">
    <h2>${action == 'edit' ? 'Edit Review' : 'Add New Review'}</h2>

    <!-- Show form only if user is logged in -->
    <c:if test="${not empty sessionScope.user}">
        <form action="reviews" method="post">
            <input type="hidden" name="action" value="${action}">

            <c:if test="${action == 'edit'}">
                <input type="hidden" name="id" value="${review.id}">
            </c:if>

            <!-- Restaurant Name -->
            <div class="form-group">
                <label>Restaurant Name:</label>
                <input type="text" class="form-control" name="restaurantName"
                       value="${review.restaurantName}" required>
            </div>

            <!-- Cuisine Type -->
            <div class="form-group">
                <label>Cuisine Type:</label>
                <input type="text" class="form-control" name="cuisineType"
                       value="${review.cuisineType}" required>
            </div>

            <!-- Personal Rating -->
            <div class="form-group">
                <label>Personal Rating:</label>
                <input type="number" class="form-control" name="personalRating" min="1" max="5"
                       value="${review.personalRating}" required>
            </div>

            <!-- Personal Notes -->
            <div class="form-group">
                <label>Personal Notes:</label>
                <textarea class="form-control" name="personalNotes" rows="4">${review.personalNotes}</textarea>
            </div>

            <br>

            <button type="submit" class="btn btn-primary">
                    ${action == 'edit' ? 'Update Review' : 'Add Review'}
            </button>
            <a href="reviews" class="btn btn-secondary">Cancel</a>
        </form>
    </c:if>

    <!-- Optional message for non-logged-in users -->
    <c:if test="${empty sessionScope.user}">
        <div class="login-message">
        <p class="text-muted">Please sign in to add or edit reviews.</p>
        </div>
    </c:if>

</div>
<jsp:include page="footer.jsp"/>
</body>
</html>
