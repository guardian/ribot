# RIBOT (WIP)

##Setup instructions

* Setup an AWS user and associate the following policy:

```
{"Statement":[{"Action":["s3:Delete*","s3:Get*","s3:List*","s3:Put*"],"Resource":"arn:aws:s3:::gnm-billing/*","Effect":"Allow"},{"Action":["s3:Get*","s3:List*"],"Resource":"arn:aws:s3:::gnm-billing","Effect":"Allow"},{"Action":"ec2:Describe*","Resource":"*","Effect":"Allow"}]}
```

* Setup a local credentials file with the following policy details

NEW config format (.aws/credentials)
[billing]
aws_access_key_id=<access key>
aws_secret_access_key =<secret key>

OLD config format (.aws/config)
[profile billing]
aws_access_key_id=<access key>
aws_secret_access_key =<secret key>

* Finally, you'll need to create a .ribot file in your home directory and add this content to it:
s3.bucket=gnm-billing

* Compile and Run

##Run

```
cd ribot/
sbt

project web
run

```




