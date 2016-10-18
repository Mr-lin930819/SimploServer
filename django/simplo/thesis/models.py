from django.db import models

class UserInfoEntity(models.Model):
    openAppUserId = models.CharField(max_length=30)
    stuNumber = models.CharField(max_length=15)
    stuPassword = models.CharField(max_length=25)
    storedCookie = models.CharField(max_length=50)
    genDate = models.DateField()
    stuName = models.CharField(max_length=12)