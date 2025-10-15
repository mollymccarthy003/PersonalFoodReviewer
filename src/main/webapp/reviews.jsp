
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
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
                <td>
                    <ul>
                        <c:forEach var="photo" items="${review.photos}">
                            <li><img src="${photo.imagePath}" alt="Photo ${photo.id}" width="100"/></li>
                        </c:forEach>
                    </ul>
                </td>
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
