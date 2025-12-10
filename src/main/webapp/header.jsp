<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle != null ? pageTitle : 'Hungry Badger Food Reviewer'}"/></title>
    <link rel="stylesheet" href="css/main.css">
</head>
<body>

<!-- Sidebar Navigation -->
<nav class="sidebar" aria-label="Main Navigation">
    <ul>
        <li>
            <c:choose>
                <c:when test="${empty sessionScope.userName}">
                    <a href="${pageContext.request.contextPath}/logIn"
                       class="${page eq 'login' ? 'active' : ''}">
                        Sign In / Sign Up
                    </a>
                </c:when>
                <c:otherwise>
                    <h3 class="welcome-message">Welcome ${sessionScope.userName}</h3>
                </c:otherwise>
            </c:choose>
        </li>

        <!-- Standard Navigation Items -->
        <li>
            <a href="${pageContext.request.contextPath}/index.jsp"
               class="${page eq 'home' ? 'active' : ''}">Home</a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/reviewForm.jsp?action=add"
               class="${page eq 'newReview' ? 'active' : ''}">New Review</a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/reviews"
               class="${page eq 'reviews' ? 'active' : ''}">View All Reviews</a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/about.jsp"
               class="${page eq 'about' ? 'active' : ''}">About</a>
        </li>
    </ul>
</nav>

<!-- Main Content Wrapper -->
<div class="main-content">
    <!-- Top Header -->
    <div class="top-header">
        <div class="logo-section">
            <div class="logo-text">Hungry Badger</div>
        </div>
        <div class="search-section">
            <form action="searchReview" method="get">
                <input type="text" name="query" class="search-bar" placeholder="Search reviews...">
            </form>
        </div>
    </div>
    <!-- Page Content Starts Here -->