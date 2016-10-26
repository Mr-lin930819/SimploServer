from simplo.thesis.query_templete import InfoQueryTemplate
import re
from bs4 import BeautifulSoup
import json
from django.http import HttpResponse

from simplo.thesis.models import UserInfoEntity
from simplo.thesis import request_keys


def query_exam(req):
    user = UserInfoEntity.objects.get(openAppUserId=req.GET[request_keys.OPEN_ID])
    query_info = ExamQueryInfo()
    query_info.number = user.stuNumber
    query_info.name = user.stuName
    query_info.cookie = user.storedCookie
    query_info.xn = req.GET[request_keys.XN]
    query_info.xq = req.GET[request_keys.XQ]
    query_info.funcId = "N121604"
    return HttpResponse(ExamQuery(query_info, "http://jwgl.fjnu.edu.cn/xskscx.aspx").doQuery())

def query_cet(req):
    user = UserInfoEntity.objects.get(openAppUserId=req.GET[request_keys.OPEN_ID])
    query_info = ExamQueryInfo()
    query_info.number = user.stuNumber
    query_info.name = user.stuName
    query_info.cookie = user.storedCookie
    #query_info.xn = req.GET[request_keys.XN]
    #query_info.xq = req.GET[request_keys.XQ]
    query_info.funcId = "N121606"
    return HttpResponse(CETQuery(query_info, "http://jwgl.fjnu.edu.cn/xsdjkscx.aspx").doQuery())

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
        print(table)
        exams = table.select("tbody")[0].select("tr")
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


class ExamQueryInfo:
    @property
    def number(self):
        return self.__number
    @property
    def name(self):
        return self.__name
    @property
    def cookie(self):
        return self.__cookie
    @property
    def xn(self):
        return self.__xn
    @property
    def xq(self):
        return self.__xq
    @property
    def funcId(self):
        return self.__funcId

    @number.setter
    def number(self, number):
        self.__number = number
    @name.setter
    def name(self, name):
        self.__name = name
    @cookie.setter
    def cookie(self, cookie):
        self.__cookie = cookie
    @xn.setter
    def xn(self, xn):
        self.__xn = xn

    @xq.setter
    def xq(self, xq):
        self.__xq = xq

    @funcId.setter
    def funcId(self, funcId):
        self.__funcId = funcId