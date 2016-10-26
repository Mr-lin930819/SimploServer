from django.shortcuts import render


def main_load(req):
    list = ["这是一个测试界面","他没有任何内容","只有这几行字"]
    return render(req, 'index.html', {"list":list})