<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${not empty sessionScope.successMessage}">
    <div class="toast toast-success">
            ${sessionScope.successMessage}
    </div>
    <% session.removeAttribute("successMessage"); %>
</c:if>

<c:if test="${not empty sessionScope.errorMessage}">
    <div class="toast toast-error">
            ${sessionScope.errorMessage}
    </div>
    <% session.removeAttribute("errorMessage"); %>
</c:if>
