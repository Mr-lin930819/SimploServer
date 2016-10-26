from simplo.thesis.query_templete import InfoQueryTemplate
import re

class ExamQueryInfo:
    @property
    def number(self):
        return self.__number
    @number.setter
    def number(self, number):
        self.__number = number

class ExamQuery(InfoQueryTemplate):
    def __init__(self, query_info, url):
        super(ExamQuery, self)\
            .__init__(query_info.number, query_info.name,query_info.cookie,
                      query_info.funcId, url)
        self.mQueryInfo = query_info

    def parseReply(self, reply):
        pass

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

