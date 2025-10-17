<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle != null ? pageTitle : 'Hungry Badger'}"/></title>
    <style>
        * { margin:0; padding:0; box-sizing:border-box; }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #ffffff; /* white background */
        }

        /* Sidebar */
        nav.sidebar {
            position: fixed;
            left: 0; top:0;
            width: 250px; height: 100vh;
            background: #000000; /* UW Madison black */
            padding-top: 20px;
            box-shadow: 2px 0 10px rgba(0,0,0,0.1);
            z-index: 1000;
        }
        nav.sidebar ul { list-style:none; padding:0; }
        nav.sidebar li { padding:0; }
        nav.sidebar a {
            display:block; color:#ffffff; text-decoration:none;
            padding:18px 25px; transition: all 0.3s ease;
            border-left: 4px solid transparent; font-size:16px;
        }
        nav.sidebar a:hover { background-color: #a50000; border-left-color:#ffffff; padding-left:30px; }
        nav.sidebar a.active { background-color: #d32f2f; border-left-color:#ffffff; }

        /* Main content */
        .main-content { margin-left:250px; min-height:100vh; }

        /* Header */
        .top-header {
            background-color:#ffffff; /* white header */
            padding:20px 40px;
            box-shadow:0 2px 5px rgba(0,0,0,0.1);
            display:flex; justify-content:space-between; align-items:center;
            margin:20px; border-radius:10px;
        }

        .logo-text {
            font-size:28px; font-weight:bold;
            background: linear-gradient(135deg, #a50000 0%, #d32f2f 100%); /* red gradient */
            -webkit-background-clip:text; -webkit-text-fill-color:transparent;
            background-clip:text;
        }

        .search-section { flex:1; max-width:500px; margin-left:40px; }
        .search-bar {
            width:100%; padding:12px 20px;
            border:2px solid #000000; border-radius:25px; font-size:15px;
            transition: all 0.3s ease; outline:none;
        }
        .search-bar:focus { border-color:#a50000; box-shadow:0 0 0 3px rgba(213,47,47,0.1); }
    </style>
</head>
<body>

<!-- Sidebar Navigation -->
<nav class="sidebar" aria-label="Main Navigation">
    <ul>
        <li><a href="${pageContext.request.contextPath}/index" class="${page eq 'home' ? 'active' : ''}">Home</a></li>
        <li><a href="${pageContext.request.contextPath}/reviews/new" class="${page eq 'newReview' ? 'active' : ''}">New Review</a></li>
        <li><a href="${pageContext.request.contextPath}/reviews" class="${page eq 'reviews' ? 'active' : ''}">View All Reviews</a></li>
        <li><a href="${pageContext.request.contextPath}/about" class="${page eq 'about' ? 'active' : ''}">About</a></li>
        <li><a href="${pageContext.request.contextPath}/login" class="${page eq 'login' ? 'active' : ''}">Sign In / Sign Up</a></li>
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
            <form action="searchReviews.jsp" method="get">
                <input type="text" name="query" class="search-bar" placeholder="Search reviews...">
            </form>
        </div>
    </div>
    <!-- Page Content Starts Here -->