from django.db import models
import uuid

class UserInfoEntity(models.Model):
    openAppUserId = models.UUIDField(primary_key=True, default=uuid.uuid1().hex)
    stuNumber = models.CharField(max_length=15)
    stuPassword = models.CharField(max_length=25)
    storedCookie = models.CharField(max_length=50)
    genDate = models.DateField()
    stuName = models.CharField(max_length=12)