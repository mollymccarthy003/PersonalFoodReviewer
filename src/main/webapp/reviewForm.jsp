<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="org.hungrybadger.entity.*" %>

<%
    String action = request.getParameter("action");
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

    <form action="reviews" method="post" enctype="multipart/form-data">
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

        <!-- Upload New Photos -->
        <div class="form-group">
            <label>Upload Photos:</label>
            <input type="file" class="form-control" name="photos" multiple>
            <small class="form-text text-muted">You can upload multiple images.</small>
        </div>

        <!-- Display Existing Photos for Editing -->
        <c:if test="${action == 'edit' && review.photos != null}">
            <div class="form-group">
                <label>Existing Photos:</label>
                <div class="existing-photos">
                    <c:forEach var="photo" items="${review.photos}">
                        <div style="display:inline-block; margin:5px; text-align:center;">
                            <img src="${photo.imagePath}" class="img-thumbnail mb-1" style="max-width:150px;"><br>
                            <input type="checkbox" name="deletePhotoIds" value="${photo.id}"> Delete
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:if>

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