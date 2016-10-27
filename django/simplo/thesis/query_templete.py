from abc import ABCMeta, abstractmethod
from bs4 import BeautifulSoup
import re
from urllib import request, parse

class InfoQueryTemplate():
    @abstractmethod
    def handleError(self, reply):pass
    @abstractmethod
    def parseReply(self, reply):pass
    @abstractmethod
    def setSpecialParams(self, params):pass

    def __init__(self, number, name, cookie, funcId, url, isPost=True):
        self.mNumber = number
        self.mName      = name
        self.mCookie    = cookie
        self.mFuncId    = funcId
        self.mUrl       = url
        self.mIsPost    = isPost

    def parseViewState(self, reply):
        doc = BeautifulSoup(reply)
        form = doc.select("input[name=__VIEWSTATE]")[0]
        if not form:
            return ""
        retViewState = form["value"]
            # .replace('+', '%2B')
        return retViewState

    def parsePJKC(self, reply):
        nodes = BeautifulSoup(reply)
        eles = nodes.select("li[class=top]")[2]\
            .select("ul[class=sub]").select("li")
        for e in eles:
            ref = e.select("a")["href"]
            kcs = []
            matcher = re.compile("xkkh=(.*)&xh").match(ref)
            if matcher:
                kcs.append(matcher.group(1))
        return kcs

    def parseRef(self, reply):
        nodes = BeautifulSoup(reply)
        ele = nodes.select("li[class=top]")[2]\
            .select("ul[class=sub]").select("li")[0]
        ref_url = ele.select("a")["href"]
        return "http://jwgl.fjnu.edu.cn/" + ref_url

    def getReply(self, number,name, cookie, funcId, url):
        refererUrl = "http://jwgl.fjnu.edu.cn/xs_main.aspx?xh=" + number
        head = {"Cookie": cookie}
        newMainUrl = url + "?"
        newParam = {"xh": number, "xm":name, "gnmkdm": funcId}
        newMainUrl += parse.urlencode(newParam, encoding='utf-8')
        if funcId == "N12141": # 教学评价
            comment_req = request.Request(newMainUrl, headers=head)
            with request.urlopen(comment_req) as resp:
                reply = resp.read().decode('gbk')
            newMainUrl = self.parseRef(reply)
            list = self.parsePJKC(reply)

        head["Content-Type"] = "application/x-www-form-urlencoded"
        head["Referer"] = refererUrl
        new_req = request.Request(newMainUrl, headers=head)
        with request.urlopen(new_req) as resp:
            reply = resp.read().decode('gbk')
        if not self.mIsPost:
            return reply

        doc = BeautifulSoup(reply)
        form = doc.select("input[name=__VIEWSTATE]")[0]
        if not form:
            return ""
        updatedViewState = form["value"]
            # .replace('+', '%2B')

        params = {"__VIEWSTATE": updatedViewState}
        self.setSpecialParams(params)

        new_head = {"Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
                    "Accept-Encoding": "gzip,deflate",
                    "Accept-Language": "zh-CN",
                    "Cache-Control": "no-cache",
                    "Connection": "Keep-Alive",
                    "Content-Type": "application/x-www-form-urlencoded",
                    "Host": "jwgl.fjnu.edu.cn",
                    "Referer": newMainUrl,
                    "User-Agent": "Mozilla/5.0",
                    "Cookie": cookie}
        if funcId == "N12141": #教学评价
            tempViewState = updatedViewState
            for s in list:
                params["pjkc"] = s
                params["TextBox1"] = "0"
                params["txt1"] = ""
                params["pjxx"] = ""
                params["__VIEWSTATE"] = tempViewState
                del params["Button2"]
                params["Button1"] = "保  存"
                temp_req = request.Request(newMainUrl,
                                           data=parse.urlencode(params).encode('utf-8'),
                                           headers= new_head)
                request.urlopen(temp_req)
                reply = "N12141"
        else:
            print(newMainUrl)
            print(params)
            print(new_head)
            temp_req = request.Request(newMainUrl,
                                       data=parse.urlencode(params).encode('utf-8'),
                                       headers=new_head)
            with request.urlopen(temp_req) as resp:
                reply = resp.read().decode('gbk')
        return reply

    def doQuery(self):
        reply = self.getReply(self.mNumber, self.mName, self.mCookie,
                              self.mFuncId, self.mUrl)
        if reply == "":
            return self.handleError(reply)
        elif reply == "N12141": #返回"N12141"为特殊情况，进行一键评价
            return ""
        return self.parseReply(reply)


class CommonQueryInfo:
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