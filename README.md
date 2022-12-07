# OD-API
A repository for transforming response data fromat from open-digger

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
3. edit application.yml `change spring.profile.active=dev` and config your own `redis config`
4. `mvn package`
5. `java -jar target/od-api-0.0.1-SNAPSHOT.jar`