import base64
import json
import re
import time
from bs4 import BeautifulSoup
from urllib import request
from urllib import parse
from http.cookiejar import CookieJar
from django.http import HttpResponse

from simplo.thesis import request_keys
from simplo.thesis import servlets
from simplo.thesis.models import UserInfoEntity

cookie = CookieJar()
xm_str = ""

def try_login(req):
    login_info = {
        "number": req.GET[request_keys.NUMBER],
        "password": req.GET[request_keys.PASSWORD],
        "viewState": req.GET[request_keys.VIEWSTATE],
        "cookie": req.GET[request_keys.COOKIE],
        "checkCode": req.GET[request_keys.CHECKCODE]
    }
    rst_code = tryLogin(login_info)
    if rst_code == "SUCCESS":
        main_json = {"xm": xm_str,
                     "lgRstCode": "1",
                     "openAppId": save_user(xm_str, login_info)}
    elif rst_code == "CHECKCODE_ERROR":
        main_json = {"xm": "",
                     "lgRstCode": "2",
                     "openAppId": ""}
    else:
        main_json = {"xm": "",
                     "lgRstCode": "0",
                     "openAppId": ""}

    print(main_json)
    body = {"TRY":main_json}
    return HttpResponse(json.dumps(body, ensure_ascii=False))


def re_login(req):
    login_info = {"openid": req.GET[request_keys.OPEN_ID],
                "viewState": req.GET[request_keys.VIEWSTATE],
                "cookie": req.GET[request_keys.COOKIE],
                "checkCode": req.GET[request_keys.CHECKCODE]}
    main = {}
    rstCode = tryLogin(login_info)
    if rstCode=="SUCCESS":
        main["result"] = "SUCCE"
        update_user(login_info)
    elif rstCode=="CHECKCODE_ERROR":
        main["result"] = "ERRVR"
    elif rstCode=="PASSWD_ERROR":
        main["result"] = "ERRSV"
    body = {"reLoginRst": main}
    return HttpResponse(json.dumps(body))



def load_login_page(req):
    view_state= get_one()
    for item in cookie:
        if item.name == 'ASP.NET_SessionId':
            cookieString = item.value

    print('get cookie ' + cookieString)
    return HttpResponse(servlets.conv_map2json({
        "viewState":view_state, "cookie":'ASP.NET_SessionId=' + cookieString}, "loginPage"))


def get_one():
    global cookie
    cookieProc = request.HTTPCookieProcessor(cookie)
    opener = request.build_opener(cookieProc)
    request.install_opener(opener)

    header = {"content-type":"application/x-www-form-urlencoded"}
    req = request.Request("http://jwgl.fjnu.edu.cn/default2.aspx", headers=header)
    with request.urlopen(req) as resp:
        respPage = resp.read().decode("gbk")
    #print(respPage)
    doc = BeautifulSoup(respPage)
    view_state = doc.find("input", attrs={'name':'__VIEWSTATE'})['value']
    print('get viewState ' + view_state)
    new_view_state = view_state.replace('+', '%2B')
    return new_view_state


def tryLogin(loginInfo):
    view_state_new = loginInfo['viewState'].replace('+', '%2B')
    query_param = {"__VIEWSTATE": view_state_new,
                  "txtUserName": loginInfo["number"],
                  "TextBox2": loginInfo["password"],
                  "txtSecretCode": loginInfo["checkCode"],
                  "RadioButtonList1": "学生",
                  "Button1": "",
                  "lbLanguage": "",
                  "hidPdrs": "",
                  "hidsc": ""}
    main_page_get_param = {"xh":loginInfo["number"]}

    req_header = {"Accept":"image/gif, image/jpeg, image/pjpeg, application/x-ms-application, application/xaml+xml, application/x-ms-xbap, */*",
                  "Content-Type": "application/x-www-form-urlencoded",
                  "Cache-Control": "max-age=0",
                  "Host": "jwgl.fjnu.edu.cn",
                  "Referer": "http://jwgl.fjnu.edu.cn/",
                  "Cookie": loginInfo["cookie"],
                  "Accept-Encoding": "gzip, deflate",
                  "Accept-Language": "zh-CN,en,*"}

    query_req = request.Request("http://jwgl.fjnu.edu.cn/default2.aspx", headers=req_header,
                                data=parse.urlencode(query_param).encode('utf-8'))

    with request.urlopen(query_req) as resp:
        lg_rst_page = resp.read().decode("gbk")

    main_page_req = request.Request("http://jwgl.fjnu.edu.cn/xs_main.aspx?"
                                    + parse.urlencode(main_page_get_param), headers=req_header)
    with request.urlopen(main_page_req) as resp:
        stu_main_page = resp.read().decode("gbk")

    if (not lg_rst_page) or (not stu_main_page):
        return "PASSWD_ERROR"

    code_vaile_matcher = re.compile("验证码不正确")
    if code_vaile_matcher.match(lg_rst_page):
        return "CHECKCODE_ERROR"

    xm_doc = BeautifulSoup(stu_main_page)
    find_name = xm_doc.find("span", attrs={'id':'xhxm'}).string
    xm_matcher = re.compile("(.*)同学").match(find_name)
    if xm_matcher:
        global xm_str
        xm_str = xm_matcher.group(1)
        return "SUCCESS"
    else:
        return "PASSWD_ERROR"


def save_user(xm, login_info):
    user = UserInfoEntity(stuNumber=login_info['number'],
                          stuPassword=login_info['password'],
                          storedCookie=login_info['cookie'],
                          stuName=xm,
                          genDate=time.strftime('%Y-%m-%d'))
    user.save()
    return user.openAppUserId


def update_user(login_info):
    user = UserInfoEntity.objects.get(openAppUserId=login_info["openid"])
    user.storedCookie = login_info["cookie"]
    user.genDate = time.strftime('%Y-%m-%d')
    user.save()
