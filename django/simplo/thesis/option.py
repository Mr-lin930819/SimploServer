from urllib import request
from urllib import parse
from django.http import HttpResponse
from bs4 import BeautifulSoup
import re
import json
from simplo.thesis.models import UserInfoEntity

def load_grade_option(req):
    user = UserInfoEntity.objects.get(openAppUserId=req.GET['openUserId'])
    resultPage = post_for_condition(user.stuNumber,
                                    user.stuName, user.storedCookie)
    json_text = parse_reply2_json(resultPage)
    return HttpResponse(json_text)


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
    return json.dumps(body)