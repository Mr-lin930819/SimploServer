from simplo.thesis.query_templete import InfoQueryTemplate
from simplo.thesis.query_templete import CommonQueryInfo
import re
from bs4 import BeautifulSoup
import json
from django.http import HttpResponse

from simplo.thesis.models import UserInfoEntity
from simplo.thesis import request_keys


def query_exam(req):
    user = UserInfoEntity.objects.get(openAppUserId=req.GET[request_keys.OPEN_ID])
    query_info = ()
    query_info.number = user.stuNumber
    query_info.name = user.stuName
    query_info.cookie = user.storedCookie
    query_info.xn = req.GET[request_keys.XN]
    query_info.xq = req.GET[request_keys.XQ]
    query_info.funcId = "N121604"
    return HttpResponse(ExamQuery(query_info, "http://jwgl.fjnu.edu.cn/xskscx.aspx").doQuery())

def query_cet(req):
    user = UserInfoEntity.objects.get(openAppUserId=req.GET[request_keys.OPEN_ID])
    query_info = CommonQueryInfo()
    query_info.number = user.stuNumber
    query_info.name = user.stuName
    query_info.cookie = user.storedCookie
    #query_info.xn = req.GET[request_keys.XN]
    #query_info.xq = req.GET[request_keys.XQ]
    query_info.funcId = "N121606"
    return HttpResponse(CETQuery(query_info, "http://jwgl.fjnu.edu.cn/xsdjkscx.aspx").doQuery())


def query_course(req):
    user = UserInfoEntity.objects.get(openAppUserId=req.GET[request_keys.OPEN_ID])
    query_info = CommonQueryInfo()
    query_info.number = user.stuNumber
    query_info.name = user.stuName
    query_info.cookie = user.storedCookie
    query_info.xn = req.GET[request_keys.XN]
    query_info.xq = req.GET[request_keys.XQ]
    result = CourseQuery(query_info, "N121603").doQuery()
    return HttpResponse(result)

class ExamQuery(InfoQueryTemplate):
    def __init__(self, query_info, url):
        super(ExamQuery, self)\
            .__init__(query_info.number, query_info.name,query_info.cookie,
                      query_info.funcId, url)
        self.mQueryInfo = query_info

    def parseReply(self, reply):
        doc = BeautifulSoup(reply)
        table = doc.select("table[class=datelist]")[0]
        exams = table.select("tbody")[0].select("tr")
        ret_json = {}
        for exam in exams:
            if exams.index(exam)==0:
                continue
            exam_info = [exam.select("td")[3].string, exam.select("td")[4].string,
                         exam.select("td")[6].string, exam.select("td")[7].string]
            ret_json[exam.select("td")[3].string] = exam_info
        return json.dumps(ret_json, ensure_ascii=False)

    def handleError(self, reply):
        matcher = re.compile("alert\\('(.*)'").match(reply)
        if matcher:
            return "CODE2"
        return ""
    def setSpecialParams(self, params):
        params["__EVENTTARGET"] = "xnd"
        params["__EVENTARGUMENT"] = ""
        params["xnd"] = self.mQueryInfo.xn
        params["xqd"] = self.mQueryInfo.xq


class CETQuery(InfoQueryTemplate):
    def __init__(self, query_info, url):
        super(CETQuery, self)\
            .__init__(query_info.number, query_info.name,query_info.cookie,
                      query_info.funcId, url, False)
        self.mCETInfo = query_info
    def parseReply(self, reply):
        matcher = re.compile("alert\\((.*)\\)").match(reply)
        if matcher:
            return "CODE2"
        doc = BeautifulSoup(reply)
        table = doc.select("table[id=DataGrid1]")[0]
        exams = table.select("tr")
        ret_json = {}
        for exam in exams:
            if exams.index(exam)==0:
                continue
            items = exam.select("td")
            exam_info = [items[0].string, items[1].string, items[2].string,
                         items[3].string, items[4].string, items[5].string,
                         items[6].string, items[7].string, items[8].string]
            ret_json[str(exams.index(exam))] = exam_info
        return json.dumps(ret_json, ensure_ascii=False)
    def handleError(self, reply):
        matcher = re.compile("alert(.*)").match(reply)
        if matcher:
            return "CODE2"
        return ""

class CourseQuery(InfoQueryTemplate):
    def __init__(self, query_info, funcId):
        super(CourseQuery, self)\
            .__init__(query_info.number, query_info.name,query_info.cookie,
                      funcId, "http://jwgl.fjnu.edu.cn/xskbcx.aspx")
        self.mCourseQueryInfo = query_info
    def parseReply(self, reply):
        new_reply = reply.replace("<br>", " ")
        doc = BeautifulSoup(new_reply)
        courseName = [""] * 7
        lessons = {}
        table = doc.select("table[id=Table1]")[0]
        lesson = table.select("tr")
        for course in lesson:
            ind = lesson.index(course)
            if ind < 2:
                continue
            elif ind%2 == 0:
                lessonNum = course.select("td")[
							(ind==2 or ind==6 or ind==10) and 1 or 0].string
                courses = []
                for i in range(0, 6):
                    courseName[i] = course.select("td")\
                        [i + ((ind==2 or ind==6 or ind==10) and 2 or 1)]\
                        .get_text()
                    courses.append(self.parseLessonContent(courseName[i]))
                lessons[lessonNum] = courses
        return json.dumps(lessons, ensure_ascii=False)

    def setSpecialParams(self, params):
        params["xnd"] = self.mCourseQueryInfo.xn
        params["xqd"] = self.mCourseQueryInfo.xq
        params["__EVENTTARGET"] = "xqd"
        params["__EVENTARGUMENT"] = ""

    def handleError(self, reply):
        matcher = re.compile("alert(.*)").match(reply)
        if matcher:
            return "CODE2"
        return ""

    def parseLessonContent(self, text):
        dataCollection = []
        result = ""
        if text=="?" or text=="":
            return ""
        else:
            rawData = text.split(" ")
            dataCollection.extend(rawData)
        lessonTimePattern = re.compile('第(.{1,2})-(.{1,2})周')
        lessonOddEvenPattern = re.compile('第(.{1,2})-(.{1,2})周(.)(.)周')
        for timeText in dataCollection:
            tempMatcher = lessonOddEvenPattern.search(timeText)
            if tempMatcher:
                ind = dataCollection.index(timeText)
                result += tempMatcher.group(4) + "周;"
                result += dataCollection[ind - 2] + ";"
                result += dataCollection[ind + 1] + ";"
                if (ind+2) < len(dataCollection):
                    result += dataCollection[ind + 2] + "$"
                else:
                    result += "$"
            else:
                tempMatcher = lessonTimePattern.search(timeText)
                if tempMatcher:
                    ind = dataCollection.index(timeText)
                    result += (tempMatcher.group(1) + "-" + tempMatcher.group(2) + "周;")
                    result += dataCollection[ind - 2] + ";"
                    result += dataCollection[ind + 1] + ";"
                    if (ind + 2) < len(dataCollection):
                        result += dataCollection[ind+2] + "$"
                    else:
                        result += "$"
        print(result)
        return result
