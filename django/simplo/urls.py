"""simplo URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/1.10/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  url(r'^$', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  url(r'^$', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.conf.urls import url, include
    2. Add a URL to urlpatterns:  url(r'^blog/', include('blog.urls'))
"""
from django.conf.urls import url
from django.contrib import admin

from simplo.thesis import servlets,login,option,query
from simplo.home import index

urlpatterns = [
    url(r'^admin/', admin.site.urls),
    url(r'^CheckImgServlet/', servlets.check_img),
    url(r'^SessionVerifyServlet/', servlets.session_verify),
    url(r'^LoginPageServlet/', login.load_login_page),
    url(r'^TryLoginServlet/', login.try_login),
    url(r'^GradeOptionServlet/', option.load_grade_option),
    url(r'^QueryExamServlet/', query.query_exam),
    url(r'^QueryCETServlet/', query.query_cet),
    url(r'^ReLoginServlet/', login.re_login),
    url(r'CourseOptionServlet/', option.load_course_option),
    url(r'QueryCourseServlet/', query.query_course),
    url(r'^$', index.main_load)
]
