Badges for GitHub releases
=====================

Put together shields.io + gthub.api + java (jersey+jetty) + heroku produce this project. 
It duplicates some original shields.io functionality, but it can be simple java REST example.

## Usage
Common:
`http://github-release-version.herokuapp.com/github/{owner}/{repo}/release.{png|svg}[?style=flat]`

Markdown:
`[![release](http://github-release-version.herokuapp.com/github/allure-framework/allure-core/release.svg?style=flat)](https://github.com/allure-framework/allure-core/releases/latest)`
[![release](http://github-release-version.herokuapp.com/github/allure-framework/allure-core/release.svg?style=flat)](https://github.com/allure-framework/allure-core/releases/latest) 

## Upload to heroku 

```
heroku login
heroku create [app name]
git push heroku master
heroku open
```
