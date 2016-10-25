import base64
import json
import re
from bs4 import BeautifulSoup
from urllib import request
from urllib import parse
from django.http import HttpResponse

from simplo.thesis import request_keys
from simplo.thesis import servlets
from simplo.thesis.models import UserInfoEntity

def try_login(req):
    login_info = {
        "number": req.GET[request_keys.OPEN_ID],
        "password": req.GET[request_keys.PASSWORD],
        "viewState": req.GET[request_keys.VIEWSTATE],
        "cookie": req.GET[request_keys.COOKIE],
        "checkCode": req.GET[request_keys.CHECKCODE]
    }


def load_login_page(req):
    view_state, cookie = get_one()
    return HttpResponse(servlets.conv_map2json({
        "viewState":view_state, "cookie":cookie}, "loginPage"))


def get_one():
    header = {"content-type":"application/x-www-form-urlencoded"}
    req = request.Request("http://jwgl.fjnu.edu.cn/default2.aspx", headers=header)
    with request.urlopen(req) as resp:
        cookie = resp.info().getheader('set-cookie')
        respPage = resp.read().decode("gbk")
    doc = BeautifulSoup(respPage)
    view_state = doc.find('input', name_='__VIEWSTATE')['value']
    new_view_state = view_state.replace('+', '%2B')
    return new_view_state, cookie
