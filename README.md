# OD-API
A repository for transforming response data format from [open-digger](https://github.com/X-lab2017/open-digger)

feature:
- transforming response data format from [open-digger](https://github.com/X-lab2017/open-digger)
- cron job for importing data to database

# API Documentation
- [For repo](https://github.com/X-lab2017/open-digger#for-repos):
    - `http://api.x-lab.info/{org_name}/{repos_name}/{repo_metric}`
    - eg:  http://api.x-lab.info/X-lab2017/open-digger/openrank
- [For user](https://github.com/X-lab2017/open-digger#for-users):
  - `http://api.x-lab.info/{user}/{user_metric}`
  - eg: http://api.x-lab.info/zhicheng-ning/openrank

# Contributing
1. git clone https://github.com/X-lab2017/od-api.git
2. cd od-api
3. edit application.yml `change spring.profile.active=dev`  
4. config your own config, like `redis config` , `github token`
5. `mvn package`
6. `java -jar target/od-api-0.0.1-SNAPSHOT.jar`

# Deploy
## Linux

~~~shell
nohup java -jar od-api-0.0.1-SNAPSHOT.jar > console.log 2>&1 &
~~~

## Docker

~~~shell
1.docker build -t {your-docker-user-name}/od-api:{custom_version} .
2.docker push {your-docker-user-name}/od-api:{custom_version}
3.docker pull {your-docker-user-name}/od-api:{custom_version}
4.docker run -d -p 8081:8080 --restart=always --name od-api {your-docker-user-name}/od-api:{custom_version}
~~~