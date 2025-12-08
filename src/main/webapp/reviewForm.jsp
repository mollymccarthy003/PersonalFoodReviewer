<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="org.hungrybadger.entity.*" %>
<%
    String action = request.getParameter("action");
    if (action == null || action.isEmpty()) {
        action = "add";
    }
    request.setAttribute("action", action); // EL can access it
    Review review = (Review) request.getAttribute("review");
%>

<html>
<jsp:include page="header.jsp">
    <jsp:param name="pageTitle" value="${action == 'edit' ? 'Edit Review' : 'Add Review'}"/>
    <jsp:param name="page" value="reviews"/>
</jsp:include>

<body>
<div class="container-fluid mt-4">
    <h2>${action == 'edit' ? 'Edit Review' : 'Add New Review'}</h2>

    <!-- Removed enctype since we are not handling files -->
    <form action="reviews" method="post">
        <input type="hidden" name="action" value="${action}">
        <c:if test="${action == 'edit'}">
            <input type="hidden" name="id" value="${review.id}">
        </c:if>

        <!-- Restaurant Name -->
        <div class="form-group">
            <label>Restaurant Name:</label>
            <input type="text" class="form-control" name="restaurantName"
                   value="${review != null ? review.restaurantName : ''}" required>
        </div>

        <!-- Cuisine Type -->
        <div class="form-group">
            <label>Cuisine Type:</label>
            <input type="text" class="form-control" name="cuisineType"
                   value="${review != null ? review.cuisineType : ''}" required>
        </div>

        <!-- Personal Rating -->
        <div class="form-group">
            <label>Personal Rating:</label>
            <input type="number" class="form-control" name="personalRating" min="1" max="5"
                   value="${review != null ? review.personalRating : ''}" required>
        </div>

        <!-- Personal Notes -->
        <div class="form-group">
            <label>Personal Notes:</label>
            <textarea class="form-control" name="personalNotes" rows="4">${review != null ? review.personalNotes : ''}</textarea>
        </div>

        <br>
        <!-- Submit / Cancel Buttons -->
        <button type="submit" class="btn btn-primary">
            ${action == 'edit' ? 'Update Review' : 'Add Review'}
        </button>
        <a href="reviews" class="btn btn-secondary">Cancel</a>
    </form>
</div>
</body>
</html>
