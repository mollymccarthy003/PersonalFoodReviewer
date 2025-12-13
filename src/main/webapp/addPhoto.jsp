<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="header.jsp">
    <jsp:param name="pageTitle" value="UploadPhoto"/>
    <jsp:param name="page" value="uploadPhoto"/>
</jsp:include>
<body>
<div class="container">
    <h2>Upload Photo</h2>

    <form action="uploadPhoto" method="post" enctype="multipart/form-data">
        <input type="hidden" name="reviewId" value="${param.reviewId}" />

        <div class="form-group">
            <label for="photo">Choose a photo:</label>
            <input type="file" id="photo" name="photo" accept="image/*" required />
        </div>

        <button type="submit" class="btn btn-primary">Upload Photo</button>
        <a href="reviews?id=${param.reviewId}" class="btn btn-secondary">Cancel</a>
    </form>
</div>
</body>
</html>
