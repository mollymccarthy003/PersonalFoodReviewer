<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="header.jsp">
    <jsp:param name="pageTitle" value="Review Details"/>
    <jsp:param name="page" value="reviews"/>
</jsp:include>
<body>
<div class="container-fluid">
    <h2>Review Details</h2>

    <div class="card mb-3" style="padding: 20px; border: 1px solid #ccc;">
        <jsp:include page="/messages.jsp" />
        <h3>${review.restaurantName}</h3>
        <p><strong>Cuisine:</strong> ${review.cuisineType}</p>
        <p><strong>Personal Rating:</strong> ${review.personalRating} / 5</p>
        <p><strong>Notes:</strong></p>
        <p>${review.personalNotes}</p>
        <br>
        <c:if test="${not empty photos}">
            <div class="review-photos" style="margin-top:20px;">
                <h4>Photos</h4>
                <c:forEach var="photo" items="${photos}">
                    <div class="photo-item" style="display:inline-block; position:relative; margin:10px;">
                        <img src="${pageContext.request.contextPath}/uploads/${photo.imagePath}"
                             alt="Review Photo" class="review-photo" />
                        <form action="deletePhoto" method="post" class="photo-delete-form">
                            <input type="hidden" name="photoId" value="${photo.id}" />
                            <button type="submit" class="btn btn-danger btn-delete"
                                    onclick="return confirm('Delete this photo?');">X</button>
                        </form>
                    </div>
                </c:forEach>
            </div>
        </c:if>

    </div>

    <a href="reviews?id=${review.id}&action=edit" class="btn btn-warning">Edit</a>

    <form action="reviews" method="post" style="display:inline;">
        <input type="hidden" name="action" value="delete">
        <input type="hidden" name="id" value="${review.id}">
        <button type="submit" class="btn btn-danger"
                onclick="return confirm('Are you sure you want to delete this review?');">
            Delete
        </button>
    </form>

    <br><br>
    <a href="reviews" class="btn btn-secondary">Back to My Reviews</a>
    <a href="addPhoto.jsp?reviewId=${review.id}" class="btn btn-primary">
        Add Photo
    </a>



</div>
<jsp:include page="footer.jsp"/>
</body>
</html>
