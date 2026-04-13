



部署rabbitmq

docker pull rabbitmq:3.13-management

docker run -d --name rabbitmq ^
-p 5672:5672 ^
-p 15672:15672 ^
-e RABBITMQ_DEFAULT_USER=admin ^
-e RABBITMQ_DEFAULT_PASS=123456 ^
rabbitmq:management