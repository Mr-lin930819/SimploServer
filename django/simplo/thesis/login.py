import base64
import json
import re
from urllib import request
from urllib import parse
from django.http import HttpResponse

from simplo.thesis import request_keys
from simplo.thesis.models import UserInfoEntity

def try_login(req):
    login_info = {
        "number": req.GET[request_keys.OPEN_ID],
        "password": req.GET[request_keys.PASSWORD],
        "viewState": req.GET[request_keys.VIEWSTATE],
        "cookie": req.GET[request_keys.COOKIE],
        "checkCode": req.GET[request_keys.CHECKCODE]
    }