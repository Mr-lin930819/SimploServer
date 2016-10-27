from urllib import request
from urllib import parse
from django.http import HttpResponse
from bs4 import BeautifulSoup
import re
import json
from simplo.thesis.models import UserInfoEntity
from simplo.thesis import request_keys

def load_grade_option(req):
    user = UserInfoEntity.objects.get(openAppUserId=req.GET[request_keys.OPEN_ID])
    resultPage = post_for_condition(user.stuNumber,
                                    user.stuName, user.storedCookie)
    json_text = parse_reply2_json(resultPage)
    return HttpResponse(json_text)


def load_course_option(req):
    user = UserInfoEntity.objects.get(openAppUserId=req.GET[request_keys.OPEN_ID])
    json_data = parse_2_json(post_4_option(user.stuNumber, user.stuName,
                                           user.storedCookie))
    return HttpResponse(json_data)


def post_for_condition(number, xm, cookie):
    main_param = {'xh': number, 'xm':xm, 'gnmkdm': "N121618"}
    referUrl = "http://jwgl.fjnu.edu.cn/xs_main.aspx?xh=" + number
    mainUrl = "http://jwgl.fjnu.edu.cn/xscj_gc.aspx?"
    head = {"Cookie": cookie,
            "Content-Type": "application/x-www-form-urlencoded",
            "Referer": referUrl}
    main_req = request.Request(mainUrl+parse.urlencode(main_param),
                               headers=head)
    with request.urlopen(main_req) as resp:
        reply = resp.read().decode('gbk')
    return reply

def parse_reply2_json(reply):
    majorMatcher = re.compile("专业：(.{0,30})</td>").match(reply)
    main_div = BeautifulSoup(reply)
    options = main_div.select('select[name=ddlXN]')
    data = []
    for elem in options[0].select('option'):
        data.append(elem.string)
    body = {"CXTJ": data}
    if majorMatcher:
        body["ZY"] = majorMatcher.group(1)
    else:
        zyString = main_div.select('span[id=Label7]')[0].string
        if zyString:
            body["ZY"] = zyString
        else:
            body["ZY"] = "暂无专业信息"

    xyLabel = main_div.select('span[id=Label6]')[0].string
    collegeMatcher = re.compile("学院：(.{0,30})").match(xyLabel)
    if collegeMatcher:
        body["XY"] = collegeMatcher.group(1)
    else:
        body["XY"] = "暂无学院信息"
    return json.dumps(body, ensure_ascii=False)


def parse_2_json(reply):
    select = BeautifulSoup(reply).select("select[name=xnd]")[0]
    options = select.select("option")
    data = []
    for elem in options:
        data.append(elem.string)
    body = {"CXTJ": data}
    return json.dumps(body, ensure_ascii=False)
    

def post_4_option(number, xm, cookie):
    referUrl = "http://jwgl.fjnu.edu.cn/xs_main.aspx?xh=" + number
    mainParm = {"xh": number,
                "xm": xm,
                "gnmkdm": "N121603"}
    head = {"Cookie": cookie,
            "Content-Type": "application/x-www-form-urlencoded",
            "Referer": referUrl}
    main_request = request.Request("http://jwgl.fjnu.edu.cn/xskbcx.aspx?"
                +parse.urlencode(mainParm), headers=head)
    with request.urlopen(main_request) as resp:
        reply = resp.read().decode('gbk')
    return reply
