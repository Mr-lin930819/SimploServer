import base64
import json
import re
from urllib import request
from urllib import parse

from bs4 import BeautifulSoup
from django.http import HttpResponse

from simplo.thesis.models import UserInfoEntity
from simplo.thesis import request_keys

OPEN_ID = 'open_id'


def check_img(req):
    return HttpResponse(get_check_img(req.GET['cookie']))


def session_verify(req):
    rsp_map_data = {'result': verify_session(req.GET[request_keys.OPEN_ID])}
    return HttpResponse(conv_map2json(rsp_map_data, "verifyRst"))


def get_check_img(cookie):
    img_url_str = "http://jwgl.fjnu.edu.cn/CheckCode.aspx"
    #resp = base64.b64decode(request.urlopen(img_url_str).read())
    resp = request.urlopen(img_url_str).read()
    return resp


def verify_session(open_id):
    userInfo = UserInfoEntity.objects.get(openAppUserId=open_id)
    query_param = {'xh': userInfo.stuNumber}
    query_header = {
        "Accept": "image/gif, image/jpeg, image/pjpeg, application/x-ms-application"
                  ", application/xaml+xml, application/x-ms-xbap, */*",
        "Content-Type": "application/x-www-form-urlencoded",
        "Cache-Control": "max-age=0",
        "Host": "jwgl.fjnu.edu.cn",
        "Referer": "http://jwgl.fjnu.edu.cn/",
        "Cookie": userInfo.storedCookie,
        "Accept-Encoding": "gzip, deflate",
        "Accept-Language": "zh-CN,en,*"}
    query_req = request.Request("http://jwgl.fjnu.edu.cn/xs_main.aspx?"
                                + parse.urlencode(query_param), headers=query_header)
    with request.urlopen(query_req) as f:
        stuMainPage = f.read().decode("gbk")
    xm_doc = BeautifulSoup(stuMainPage)
    find_name = xm_doc.find("span", attrs={'id': 'xhxm'}).string
    xm_matcher = re.compile("(.*)同学").match(find_name)
    if xm_matcher:
        return "SUCCE"
    else:
        return "ERREP"


def conv_map2json(obj_map, key):
    json_map = {key: obj_map}
    return json.dumps(json_map, ensure_ascii=False)
